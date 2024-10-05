package com.fastcampus.board.dto.response;


import com.fastcampus.board.dto.ArticleWithCommentsDto;
import com.fastcampus.board.dto.CommentDto;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String username,
        Set<CommentResponse> commentResponses
) {

    public static ArticleWithCommentsResponse of(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String username,
        Set<CommentResponse> commentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtag, createdAt,
            email, username, commentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {

        return new ArticleWithCommentsResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            dto.userAccountDto().username(),
            organizeChildComments(dto.commentDtos())
        );
    }

    private static Set<CommentResponse> organizeChildComments(Set<CommentDto> dtos) {
        Map<Long, CommentResponse> map = dtos.stream().map(CommentResponse::from)
                .collect(Collectors.toMap(CommentResponse::id, Function.identity()));

        map.values().stream().filter(CommentResponse::hasParentComment).forEach(x -> {
            CommentResponse parent = map.get(x.parentCommentId());
            parent.childComments().add(x);
        });

        Comparator<CommentResponse> childCommentComparator = Comparator
                .comparing(CommentResponse::createdAt).reversed() // 최신순 정렬
                .thenComparingLong(CommentResponse::id); // 혹시 모르니까 아이디 대소비교로 마무리

        return map.values().stream().filter(x -> !x.hasParentComment())
                .collect(Collectors.toCollection(() -> new TreeSet<>(childCommentComparator)));
    }

}
