package com.fastcampus.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Table(name = "\"like\"")
@Entity
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "username")
    @ManyToOne(optional = false)
    private UserAccount userAccount;

    @Setter
    @JoinColumn(name = "article_id")
    @ManyToOne
    private Article article; // 게시글 (ID)

    @Setter
    @JoinColumn(name = "comment_id")
    @ManyToOne
    private Comment comment; // 댓글 (ID)

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    protected Like() {
    }

    // Private constructor
    private Like(Article article, Comment comment, UserAccount userAccount) {
        this.article = article;
        this.comment = comment;
        this.userAccount = userAccount;
    }

    // Factory method to create Like for Article
    public static Like createForArticle(Article article, UserAccount userAccount) {
        if (article == null || userAccount == null) {
            throw new IllegalArgumentException("Article and UserAccount must not be null");
        }
        return new Like(article, null, userAccount);
    }

    // Factory method to create Like for Comment
    public static Like createForComment(Comment comment, UserAccount userAccount) {
        if (comment == null || userAccount == null) {
            throw new IllegalArgumentException("Comment and UserAccount must not be null");
        }
        return new Like(null, comment, userAccount);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Like that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @PrePersist
    void createdAt() {
        this.createdAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }
}


