package com.fastcampus.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

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
    @ManyToOne(optional = false)
    @JoinColumn(name = "username")
    private UserAccount userAccount;


    @Setter
    @Column(nullable = false, length = 500)
    private String content;

    protected Comment() {
    }

    private Comment(Article article, UserAccount userAccount, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.content = content;
    }


    public static Comment of(Article article, UserAccount userAccount, String content) {
        return new Comment(article, userAccount, content);
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
