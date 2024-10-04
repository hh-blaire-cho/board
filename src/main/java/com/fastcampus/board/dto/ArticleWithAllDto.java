package com.fastcampus.board.dto;

import com.fastcampus.board.domain.Article;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithAllDto(
    Long id,
    UserAccountDto userAccountDto,
    Set<CommentDto> commentDtos,
    Set<LikeDto> likeDtos,
    String title,
    String content,
    String hashtag,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {

    public static ArticleWithAllDto of(
        Long id,
        UserAccountDto userAccountDto,
        Set<CommentDto> commentDtos, // ArticleDto 와의 차별화
        Set<LikeDto> likeDtos,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
    ) {
        return new ArticleWithAllDto(id, userAccountDto, commentDtos, likeDtos, title, content, hashtag,
            createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleWithAllDto from(Article entity) {
        return new ArticleWithAllDto(
            entity.getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getComments().stream()
                .map(CommentDto::from)
                .collect(Collectors.toCollection(LinkedHashSet::new)),
            entity.getLikes().stream()
                .map(LikeDto::from)
                .collect(Collectors.toCollection(LinkedHashSet::new)),
            entity.getTitle(),
            entity.getContent(),
            entity.getHashtag(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }

    // toEntity() X  <-- there is no such domain ArticleWithComments

}
