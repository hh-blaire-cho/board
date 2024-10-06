package com.fastcampus.board.dto;

import com.fastcampus.board.domain.Like;
import java.sql.Timestamp;

public record LikeDto(
    Long id,
    UserAccountDto userAccountDto,
    Long articleId,
    Long commentId,
    Timestamp createdAt,
    Timestamp updatedAt,
    Timestamp deletedAt
) {

    public static LikeDto of(
        Long id,
        UserAccountDto userAccountDto,
        Long articleId,
        Long commentId,
        Timestamp createdAt,
        Timestamp updatedAt,
        Timestamp deletedAt
    ) {
        return new LikeDto(id, userAccountDto, articleId, commentId, createdAt, updatedAt, deletedAt);
    }

    public static LikeDto of(
        UserAccountDto userAccountDto,
        Long articleId,
        Long commentId
    ) {
        return new LikeDto(null, userAccountDto, articleId, commentId, null, null, null);
    }

    public static LikeDto from(Like entity) {
        return new LikeDto(
            entity.getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getArticle() != null ? entity.getArticle().getId() : null,
            entity.getComment() != null ? entity.getComment().getId() : null,
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt()
        );
    }
}
