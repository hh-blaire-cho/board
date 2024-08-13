package com.fastcampus.board.dto.response;

import com.fastcampus.board.dto.CommentDto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname
) {

    public static CommentResponse of(
            Long id,
            String content,
            LocalDateTime createdAt,
            String email,
            String nickname) {
        return new CommentResponse(id, content, createdAt, email, nickname);
    }

    public static CommentResponse from(CommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().username();
        }

        return new CommentResponse(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname
        );
    }

}