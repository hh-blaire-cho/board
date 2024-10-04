package com.fastcampus.board.dto.response;


import com.fastcampus.board.dto.ArticleWithAllDto;
import com.fastcampus.board.dto.CommentDto;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithAllResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String username,
        Set<CommentResponse> commentResponses,
        Set<LikeResponse> likeResponses
) {

    public static ArticleWithAllResponse of(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String email,
        String username,
        Set<CommentResponse> commentResponses,
        Set<LikeResponse> likeResponses
    ) {
        return new ArticleWithAllResponse(id, title, content, hashtag,
            createdAt, email, username, commentResponses, likeResponses);
    }

    public static ArticleWithAllResponse from(ArticleWithAllDto dto) {
        return new ArticleWithAllResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.createdAt(),
            dto.userAccountDto().email(),
            dto.userAccountDto().username(),
            organizeChildComments(dto.commentDtos()),
            dto.likeDtos().stream()
                .map(LikeResponse::from)
                .collect(Collectors.toCollection(LinkedHashSet::new))
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
