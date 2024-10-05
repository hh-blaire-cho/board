package com.fastcampus.board.dto.response;

import com.fastcampus.board.dto.ArticleWithCommentsDto;
import com.fastcampus.board.dto.CommentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fastcampus.board.TestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("DTO - 댓글을 포함한 게시글 응답 테스트")
class ArticleWithCommentsResponseTest {

    @DisplayName("게시글댓글dto 정렬해서 api로 변환")
    @Test
    void test_ArticleWithCommentsResponseSorted() {
        // Given article with comments dto without child comments
        // When mapping
        // Then organizes the comments with order
        LocalDateTime now = LocalDateTime.now();
        String content = randString(3);
        long articleId = randNumb();
        Set<CommentDto> commentDtos = Set.of(
                createCommentDto(1L, articleId, null, content, now),
                createCommentDto(2L, articleId, null, content, now.plusDays(1L)),
                createCommentDto(3L, articleId, null, content, now.plusDays(3L)),
                createCommentDto(4L, articleId, null, content, now),
                createCommentDto(5L, articleId, null, content, now.plusDays(5L)),
                createCommentDto(6L, articleId, null, content, now.plusDays(4L)),
                createCommentDto(7L, articleId, null, content, now.plusDays(2L)),
                createCommentDto(8L, articleId, null, content, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(1L, "", "", "", commentDtos, Set.of());

        List<Long> actualIds = ArticleWithCommentsResponse.from(input).commentResponses().stream()
                .map(CommentResponse::id).collect(Collectors.toList());
        List<Long> expectedIds = List.of(8L, 5L, 6L, 3L, 7L, 2L, 1L, 4L);

        assertEquals(expectedIds, actualIds, "The comment IDs are not in the expected order.");

    }

    @DisplayName("게시글댓글dto 정렬해서 api로 변환 (대댓글케이스 O)")
    @Test
    void test_ArticleWithCommentsResponseSorted2() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        long articleId = randNumb();
        String content = randString();
        Set<CommentDto> commentDtos = Set.of(
                createCommentDto(1L, articleId, null, content, now),
                createCommentDto(2L, articleId, 1L, content, now.plusDays(1L)),
                createCommentDto(3L, articleId, 1L, content, now.plusDays(1L)),
                createCommentDto(4L, articleId, 1L, content, now),
                createCommentDto(5L, articleId, null, content, now.plusDays(5L)),
                createCommentDto(6L, articleId, null, content, now.plusDays(4L)),
                createCommentDto(7L, articleId, 6L, content, now.plusDays(2L)),
                createCommentDto(8L, articleId, 6L, content, now.plusDays(7L))
        );

        ArticleWithCommentsDto input = createArticleWithCommentsDto(1L, "", "", "", commentDtos, Set.of());

        Set<CommentResponse> actual = ArticleWithCommentsResponse.from(input).commentResponses();

        // Verify the main list of comment IDs
        assertThat(actual.stream().map(CommentResponse::id).collect(Collectors.toList()))
                .containsExactly(5L, 6L, 1L); // Verify the IDs in the expected order

        // Verify the child comment IDs using AssertJ
        assertThat(actual) // Start with the actual set
                .flatExtracting(CommentResponse::childComments) // Extracts all child comments
                .extracting(CommentResponse::id)                // Extract IDs from the child comments
                .containsExactly(7L, 8L, 4L, 2L, 3L); // Verify the IDs in the expected order


    }

}