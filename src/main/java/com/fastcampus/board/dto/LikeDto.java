package com.fastcampus.board.dto;

import com.fastcampus.board.domain.Like;
import java.sql.Timestamp;

public record LikeDto(
    Long id,
    UserAccountDto userAccountDto,
    ArticleDto articleDto,
    CommentDto commentDto,
    Timestamp createdAt,
    Timestamp updatedAt,
    Timestamp deletedAt
) {

    public static LikeDto of(
        Long id,
        UserAccountDto userAccountDto,
        ArticleDto articleDto,
        CommentDto commentDto,
        Timestamp createdAt,
        Timestamp updatedAt,
        Timestamp deletedAt
    ) {
        return new LikeDto(id, userAccountDto, articleDto, commentDto, createdAt, updatedAt, deletedAt);
    }

    public static LikeDto of(
        UserAccountDto userAccountDto,
        ArticleDto articleDto,
        CommentDto commentDto
    ) {
        return new LikeDto(null, userAccountDto, articleDto, commentDto, null, null, null);
    }

    public static LikeDto from(Like entity) {
        return new LikeDto(
            entity.getId(),
            UserAccountDto.from(entity.getUserAccount()),
            ArticleDto.from(entity.getArticle()),
            CommentDto.from(entity.getComment()),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt()
        );
    }
}
