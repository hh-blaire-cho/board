package com.fastcampus.board.dto.response;

import com.fastcampus.board.dto.CommentDto;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String username,
        Long parentCommentId,
        Set<CommentResponse> childComments
) {

    public static CommentResponse of(
            Long id,
            String content,
            LocalDateTime createdAt,
            String email,
        String username,
            Long parentCommentId) {

        Comparator<CommentResponse> childCommentComparator = Comparator
                .comparing(CommentResponse::createdAt)
                .thenComparingLong(CommentResponse::id); // 혹시 모르니까 아이디 대소비교로 마무리

        return new CommentResponse(id, content, createdAt, email, username, parentCommentId,
            new TreeSet<>(childCommentComparator));
    }

    public static CommentResponse from(CommentDto dto) {
        return CommentResponse.of(
            dto.id(),
            dto.content(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            dto.userAccountDto().username(),
            dto.parentCommentId()
        );
    }

    public boolean hasParentComment() {
        return parentCommentId != null;
    }

}