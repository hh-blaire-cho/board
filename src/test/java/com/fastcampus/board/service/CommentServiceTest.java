package com.fastcampus.board.service;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.dto.UserAccountDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

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

    private UserAccountDto userAccountDto;
    private UserAccount userAccount;
    private Article article;

    @BeforeEach
    public void setUp() { //픽스쳐를 만드는 과정
        article = Article.of(userAccount, "title", "content", "#tag");

        userAccountDto = UserAccountDto.of(
                1L, "hcho", "qwerty", "hcho@mail.com", "winky", "This is memo",
                LocalDateTime.now(), "hcho", LocalDateTime.now(), "hcho");

        userAccount = userAccountDto.toEntity();
    }


    @DisplayName("게시글 아이디 조회 시, 해당 댓글 리스트 반환")
    @Test
    void test_searchCommentsUsingArticleId() {
        // Given article Id
        long articleId = 1L;
        Comment expected = createComment("content");
        given(commentRepo.findByArticle_Id(articleId)).willReturn(List.of(expected));

        // When search list of comments from that corresponding article
        List<CommentDto> actual = sut.searchComments(articleId);

        // Then returns that well
        assertThat(actual).isNotNull();
        then(articleRepo).should().findById(articleId);
    }

    @DisplayName("댓글 정보 입력 시, 댓글 저장")
    @Test
    void test_savingComment() {
        // Given comments info
        CommentDto dto = createCommentDto("string");
        given(articleRepo.getReferenceById(dto.articleId())).willReturn(article);
        given(commentRepo.save(any(Comment.class))).willReturn(null);

        // When try saving
        sut.saveComment(dto);

        // Then saves that properly
        then(articleRepo).should().getReferenceById(dto.articleId());
        then(commentRepo).should().save(any(Comment.class));
    }

    @DisplayName("댓글 수정 정보 입력 시, 댓글 수정")
    @Test
    void test_updatingComment() {
        // Given original entity and updated dto
        Comment original = createComment("old-string");
        CommentDto updated = createCommentDto("new-string");
        given(commentRepo.getReferenceById(updated.id())).willReturn(original);

        // When try updating
        sut.updateComment(updated);

        // Then saves that properly
        then(commentRepo).should().getReferenceById(updated.id());
        then(commentRepo).should().save(any(Comment.class));
    }

    @DisplayName("댓글 아이디 입력하면 댓글 삭제")
    @Test
    void test_deletingComment() {
        // Given Comment Id
        long commentId = 1L;
        willDoNothing().given(commentRepo).deleteById(commentId);

        // When try deleting using that id
        sut.deleteComment(commentId);

        // Then deletes that properly
        then(commentRepo).should().deleteById(commentId);
    }

    private CommentDto createCommentDto(String content) {
        return CommentDto.of(1L, 1L, userAccountDto, content,
                LocalDateTime.now(), "hcho", LocalDateTime.now(), "hcho");
    }


    private Comment createComment(String content) {
        return Comment.of(article, userAccount, content);
    }
}