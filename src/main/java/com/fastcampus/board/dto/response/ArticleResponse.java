package com.fastcampus.board.dto.response;

import com.fastcampus.board.dto.ArticleDto;
import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String username
) {

    public static ArticleResponse of(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String username) {
        return new ArticleResponse(id, title, content, hashtag, createdAt, email, username);
    }

    public static ArticleResponse from(ArticleDto dto) {
        return new ArticleResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            dto.userAccountDto().username()
        );
    }

}