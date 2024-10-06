package com.fastcampus.board.dto;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.UserAccount;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record CommentDto(
        Long id,
        Long articleId,
        Long parentCommentId,
        UserAccountDto userAccountDto,
        Set<LikeDto> likesDtos,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static CommentDto of(
            Long id,
            Long articleId,
            Long parentCommentId,
            UserAccountDto userAccountDto,
            Set<LikeDto> likesDtos,
            String content,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {
        return new CommentDto(id, articleId, parentCommentId, userAccountDto, likesDtos,
            content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static CommentDto of(
            Long articleId,
            Long parentCommentId,
            UserAccountDto userAccountDto,
            String content) {
        return CommentDto.of(null, articleId, parentCommentId, userAccountDto, null,
            content, null, null, null, null);
    }

    public static CommentDto from(Comment entity) {
        return new CommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                entity.getParentCommentId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getLikes().stream()
                    .map(LikeDto::from)
                    .collect(Collectors.toCollection(LinkedHashSet::new)),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Comment toEntity(Article article, UserAccount userAccount) {
        return Comment.of(
                article,
                userAccount, //userAccountDto 에 비해 id 있음을 확실히 보장
                content
        );
    }

}