package com.fastcampus.board.service;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.CommentRepository;
import com.fastcampus.board.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static com.fastcampus.board.TestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class) //이렇게 해야, 굳이 Application 을 안켜서 가벼워짐.
class CommentServiceTest {

    @Mock
    private ArticleRepository articleRepo;

    @Mock
    private CommentRepository commentRepo;

    @Mock
    private UserAccountRepository userAccountRepo;

    @InjectMocks // Mock using Mock, CommentRepository 를 주입하는 대상, CommentService 에 의존함 (=사용당함)
    private CommentService sut; // System Under Test

    private static Article article;
    private static UserAccount userAccount;

    @BeforeAll
    static void setUpFixture() {
        userAccount = USER_ACCOUNT_DTO.toEntity();
        article = Article.of(userAccount, "title", "content", "hashtag");
    }

    @DisplayName("댓글 아이디 조회 시, 댓글 반환")
    @Test
    void test_getCommentUsingCommentId() {
        // Given commentId
        long commentId = randNumb();
        Comment comment = Comment.of(article, userAccount, "random-content");
        given(commentRepo.findById(commentId)).willReturn(Optional.of(comment));

        // When search list of comments from that corresponding post
        CommentDto commentDto = sut.getComment(commentId);

        // Then returns that well
        assertThat(comment).isNotNull();
        assertThat(comment).hasFieldOrPropertyWithValue("content", "random-content");
        then(commentRepo).should().findById(commentId);
    }


    @DisplayName("게시글 아이디 조회 시, 딸린 댓글 리스트 반환")
    @Test
    void test_searchCommentsUsingArticleId() {
        // Given article Id
        long articleId = randNumb();
        Comment expected1 = Comment.of(article, userAccount, "content1");
        Comment expected2 = Comment.of(article, userAccount, "content2");
        given(commentRepo.findByArticle_Id(articleId)).willReturn(List.of(expected1, expected2));

        // When search list of comments from that corresponding article
        List<CommentDto> actual = sut.searchComments(articleId);

        // Then returns that well
        assertThat(actual).hasSize(2);
        assertThat(actual).first().hasFieldOrPropertyWithValue("content", "content1");
        assertThat(actual).last().hasFieldOrPropertyWithValue("content", "content2");
        then(commentRepo).should().findByArticle_Id(articleId);
    }

    @DisplayName("게시글 아이디 조회 시, 딸린 댓글 리스트 반환 - 대댓글")
    @Test
    void test_searchCommentsUsingArticleId_v2() {
        // Given
        Comment parent = Comment.of(article, userAccount, "parentContent");
        Comment child1 = Comment.of(article, userAccount, "childContent1");
        Comment child2 = Comment.of(article, userAccount, "childContent2");
        long articleId = randNumb();
        long parentId = randNumb();
        long childId1 = randNumb();
        long childId2 = randNumb();
        ReflectionTestUtils.setField(article, "id", articleId);
        ReflectionTestUtils.setField(parent, "id", parentId);
        ReflectionTestUtils.setField(child1, "id", childId1);
        ReflectionTestUtils.setField(child2, "id", childId2);
        parent.addChildComment(child1);
        parent.addChildComment(child2);
        given(commentRepo.findByArticle_Id(articleId)).willReturn(List.of(parent, child1, child2));

        // When search list of comments from that corresponding article
        List<CommentDto> actual = sut.searchComments(articleId);

        // Then returns that well
        assertThat(actual).hasSize(3);
        assertThat(actual)
                .extracting("id", "articleId", "parentCommentId", "content")
                .containsExactlyInAnyOrder(
                        tuple(parentId, articleId, null, "parentContent"),
                        tuple(childId1, articleId, parentId, "childContent1"),
                        tuple(childId2, articleId, parentId, "childContent2")
                );
        then(commentRepo).should().findByArticle_Id(articleId);
    }

    @DisplayName("댓글 정보 입력 시, 댓글 저장")
    @Test
    void test_savingComment() {
        // Given comments info
        CommentDto dto = createCommentDto();
        given(articleRepo.getReferenceById(dto.articleId())).willReturn(article);
        given(userAccountRepo.getReferenceById(dto.userAccountDto().username())).willReturn(userAccount);
        given(commentRepo.save(any(Comment.class))).willReturn(null);

        // When try saving
        sut.saveComment(dto);

        // Then saves that properly
        then(articleRepo).should().getReferenceById(dto.articleId());
        then(userAccountRepo).should().getReferenceById(dto.userAccountDto().username());
        then(commentRepo).should().save(any(Comment.class));
        then(commentRepo).should(never()).getReferenceById(anyLong());
    }

    @DisplayName("대댓글 정보 입력 시, (부모 댓글 ID 필수), 대댓글 저장")
    @Test
    void test_savingChildComment() {
        // Given child comment info (parentCommentId must)
        Long parentCommentId = randNumb();
        Comment parent = Comment.of(article, userAccount, "parentContent");
        CommentDto child = createCommentDto(randNumb(), randNumb(), parentCommentId);
        given(articleRepo.getReferenceById(child.articleId())).willReturn(article);
        given(userAccountRepo.getReferenceById(child.userAccountDto().username())).willReturn(userAccount);
        given(commentRepo.getReferenceById(child.parentCommentId())).willReturn(parent);

        // When try saving
        sut.saveComment(child);

        // Then
        assertThat(child.parentCommentId()).isNotNull();
        then(articleRepo).should().getReferenceById(child.articleId());
        then(userAccountRepo).should().getReferenceById(child.userAccountDto().username());
        then(commentRepo).should(never()).save(any(Comment.class)); // 대댓글일떈 레포에 신규 저장 안함
        then(commentRepo).should().getReferenceById(child.parentCommentId());
    }

    @DisplayName("연계 게시글 정보가 없는데, 댓글 저장 시도 시, 경고 로그만 찍고 끝")
    @Test
    void test_savingCommentWithNoArticle() {
        // Given non-existing article
        CommentDto dto = createCommentDto();
        given(articleRepo.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

        // When trying saving comment
        sut.saveComment(dto);

        // Then do nothing but logging warning
        then(articleRepo).should().getReferenceById(dto.articleId());
        then(userAccountRepo).shouldHaveNoInteractions(); // 게시글 검색이 실패하면, 사용자는 검색하지 않음
        then(commentRepo).shouldHaveNoInteractions();
    }

    @DisplayName("연계 사용자 정보가 없는데, 댓글 저장 시도 시, 경고 로그만 찍고 끝")
    @Test
    void test_savingCommentWithNoUserAccount() {
        // Given non-existing user account
        CommentDto dto = createCommentDto();
        given(articleRepo.getReferenceById(dto.articleId())).willReturn(article); // 게시글은 정상이라고 가정
        given(userAccountRepo.getReferenceById(dto.userAccountDto().username())).willThrow(EntityNotFoundException.class);

        // When trying saving comment
        sut.saveComment(dto);

        // Then do nothing but logging warning
        then(articleRepo).should().getReferenceById(dto.articleId()); // 게시글 검색은 함
        then(userAccountRepo).should().getReferenceById(dto.userAccountDto().username());
        then(commentRepo).shouldHaveNoInteractions();
    }

    @DisplayName("바뀐 정보 입력 시, 댓글 수정")
    @Test
    void test_updatingComment() {
        // Given original entity and updated dto
        Comment original = Comment.of(article, userAccount, "old-string");
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