package com.fastcampus.board.service;

import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.fastcampus.board.TestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class) //이렇게 해야, 굳이 Application 을 안켜서 가벼워짐.
class CommentServiceTest {

    @Mock
    private ArticleRepository articleRepo;
    @Mock
    private CommentRepository commentRepo;

    @InjectMocks // Mock using Mock, CommentRepository 를 주입하는 대상, CommentService 에 의존함 (=사용당함)
    private CommentService sut; // System Under Test


    @DisplayName("게시글 아이디 조회 시, 딸린 댓글 리스트 반환")
    @Test
    void test_searchCommentsUsingArticleId() {
        // Given article Id
        long articleId = randNumb();
        Comment expected1 = Comment.of(ARTICLE, USER_ACCOUNT, "content1");
        Comment expected2 = Comment.of(ARTICLE, USER_ACCOUNT, "content2");
        given(commentRepo.findByArticle_Id(articleId)).willReturn(List.of(expected1, expected2));

        // When search list of comments from that corresponding article
        List<CommentDto> actual = sut.searchComments(articleId);

        // Then returns that well
        assertThat(actual).hasSize(2);
        assertThat(actual).first().hasFieldOrPropertyWithValue("content", "content1");
        assertThat(actual).last().hasFieldOrPropertyWithValue("content", "content2");
        then(commentRepo).should().findByArticle_Id(articleId);
    }

    @DisplayName("댓글 정보 입력 시, 댓글 저장")
    @Test
    void test_savingComment() {
        // Given comments info
        CommentDto dto = createCommentDto();
        given(articleRepo.getReferenceById(dto.articleId())).willReturn(ARTICLE);
        given(commentRepo.save(any(Comment.class))).willReturn(null);

        // When try saving
        sut.saveComment(dto);

        // Then saves that properly
        then(articleRepo).should().getReferenceById(dto.articleId());
        then(commentRepo).should().save(any(Comment.class));
    }

    @DisplayName("맞는 게시글이 없는데, 댓글 저장 시도 시, 경고 로그만 찍고 끝")
    @Test
    void test_savingCommentWithNoArticle() {
        // Given non-existing article
        CommentDto dto = createCommentDto();
        given(articleRepo.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

        // When trying saving comment
        sut.saveComment(dto);

        // Then do nothing but logging warning
        then(articleRepo).should().getReferenceById(dto.articleId());
        then(commentRepo).shouldHaveNoInteractions();
    }

    @DisplayName("바뀐 정보 입력 시, 댓글 수정")
    @Test
    void test_updatingComment() {
        // Given original entity and updated dto
        Comment original = Comment.of(ARTICLE, USER_ACCOUNT, "old-string");
        CommentDto updated = createCommentDto();
        given(commentRepo.getReferenceById(updated.id())).willReturn(original);

        // When try updating
        sut.updateComment(updated);

        // Then saves that properly
        then(commentRepo).should().getReferenceById(updated.id());
        then(commentRepo).should().save(any(Comment.class));
    }

    @DisplayName("없는 댓글 수정 시도 시, 경고 로그만 찍고 끝")
    @Test
    void test_updatingNonExistedComment() {
        // Given updated info without original
        CommentDto updated = createCommentDto();
        given(commentRepo.getReferenceById(updated.id())).willThrow(EntityNotFoundException.class);

        // When try updating it
        sut.updateComment(updated);

        // Then should NOT update it but just logging warning
        then(commentRepo).should().getReferenceById(updated.id());
        verify(commentRepo, never()).save(any(Comment.class));
    }

    @DisplayName("아이디로 댓글 삭제")
    @Test
    void test_deletingComment() {
        // Given Comment Id
        long commentId = randNumb();
        willDoNothing().given(commentRepo).deleteById(commentId);

        // When try deleting using that id
        sut.deleteComment(commentId);

        // Then deletes that properly
        then(commentRepo).should().deleteById(commentId);
    }

}