package com.fastcampus.board.dto.response;

import com.fastcampus.board.dto.LikeDto;

public record LikeResponse(
    Long id,
    Long articleId,
    Long commentId,
    String username
) {

    public static LikeResponse of(
        Long id,
        Long articleId,
        Long commentId,
        String username
    ) {

        return new LikeResponse(id, articleId, commentId, username);
    }

    public static LikeResponse from(LikeDto dto) {
        return LikeResponse.of(
            dto.id(),
            dto.articleDto().id(),
            dto.commentDto().id(),
            dto.userAccountDto().username()
        );
    }


}