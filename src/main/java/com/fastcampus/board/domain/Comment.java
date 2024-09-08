package com.fastcampus.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@ToString(callSuper = true)
@Entity
public class Comment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private Article article; // 게시글 (ID)

    @Setter
    @Column(updatable = false)
    private Long parentCommentId; // 부모 ID, 단방향 매핑

    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private final Set<Comment> childComments = new LinkedHashSet<>(); //추후 정렬해야할 수 있음 고려


    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "username")
    private UserAccount userAccount;


    @Setter
    @Column(nullable = false, length = 500)
    private String content;

    protected Comment() {
    }

    private Comment(Article article, Long parentCommentId, UserAccount userAccount, String content) {
        this.article = article;
        this.parentCommentId = parentCommentId;
        this.userAccount = userAccount;
        this.content = content;
    }

    public static Comment of(Article article, UserAccount userAccount, String content) {
        return new Comment(article, null, userAccount, content);
    }

    public void addChildComment(Comment child) {
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
