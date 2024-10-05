package com.fastcampus.board.dto.response;

import com.fastcampus.board.dto.CommentDto;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public record CommentResponse(
        Long id,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String email,
        String username,
        Set<CommentResponse> childComments
) {

    public static CommentResponse of(
        Long id,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String email,
        String username
    ) {

        Comparator<CommentResponse> childCommentComparator = Comparator
                .comparing(CommentResponse::createdAt)
                .thenComparingLong(CommentResponse::id); // 혹시 모르니까 아이디 대소비교로 마무리

        return new CommentResponse(id, parentCommentId, content, createdAt, email, username,
            new TreeSet<>(childCommentComparator));
    }

    public static CommentResponse from(CommentDto dto) {
        return CommentResponse.of(
            dto.id(),
            dto.parentCommentId(),
            dto.content(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            dto.userAccountDto().username()
        );
    }

    public boolean hasParentComment() {
        return parentCommentId != null;
    }

}