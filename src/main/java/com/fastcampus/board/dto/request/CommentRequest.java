package com.fastcampus.board.dto.request;

import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.dto.UserAccountDto;

public record CommentRequest(
        Long articleId,
        String content
) {
    public static CommentRequest of(Long articleId, String content) {
        return new CommentRequest(articleId, content);
    }

    public CommentDto toDto(UserAccountDto userAccountDto) {
        return CommentDto.of(
                articleId,
                userAccountDto,
                content
        );
    }
}