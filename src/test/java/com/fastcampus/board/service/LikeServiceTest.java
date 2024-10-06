package com.fastcampus.board.service;

import static com.fastcampus.board.TestHelper.USER_ACCOUNT_DTO;
import static com.fastcampus.board.TestHelper.randNumb;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.Like;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.CommentRepository;
import com.fastcampus.board.repository.LikeRepository;
import com.fastcampus.board.repository.UserAccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@DisplayName("비즈니스 로직 - 좋아요")
@SpringBootTest
class LikeServiceTest {

    @Autowired
    ArticleService articleService;

    @MockBean
    private ArticleRepository articleRepo;

    @Autowired
    CommentService commentService;

    @MockBean
    private CommentRepository commentRepo;

    @MockBean
    private UserAccountRepository userRepo;

    @MockBean
    private LikeRepository likeRepo;


    @Test
    @DisplayName("좋아요 추가 정상 케이스")
    void test_toggleLike_addLike() {
        Long articleId = randNumb();
        Long commentId = randNumb();
        String username = "randomUsername";
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article = Article.of(userAccount, "title", "content", "#hash");
        Comment comment = Comment.of(article, userAccount, "content");
        when(articleRepo.findById(articleId)).thenReturn(Optional.of(article));
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userAccount));
        when(likeRepo.findByUserAccountAndArticle(userAccount, article)).thenReturn(Optional.empty());
        when(likeRepo.findByUserAccountAndComment(userAccount, comment)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> articleService.toggleLike(articleId, username));
        assertDoesNotThrow(() -> commentService.toggleLike(commentId, username));
        verify(likeRepo, times(2)).save(any(Like.class));
    }

    @Test
    @DisplayName("좋아요 취소 정상 케이스 - 게시글")
    void test_toggleLikeArticle_removeLike() {
        Long articleId = 1L;
        String username = "randomUsername";
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article = Article.of(userAccount, "title", "content", "#hash");
        Like like = Like.createForArticle(userAccount, article);

        when(articleRepo.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userAccount));
        when(likeRepo.findByUserAccountAndArticle(userAccount, article)).thenReturn(Optional.of(like));

        assertDoesNotThrow(() -> articleService.toggleLike(articleId, username));
        verify(likeRepo, times(1)).delete(like);
    }

    @Test
    @DisplayName("좋아요 취소 정상 케이스 - 댓글")
    void test_toggleLikeComment_removeLike() {
        Long articleId = 1L;
        Long commentId = randNumb();
        String username = "randomUsername";
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article = Article.of(userAccount, "title", "content", "#hash");
        Comment comment = Comment.of(article, userAccount, "content");
        Like like = Like.createForComment(userAccount, comment);

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userAccount));
        when(likeRepo.findByUserAccountAndComment(userAccount, comment)).thenReturn(Optional.of(like));

        assertDoesNotThrow(() -> commentService.toggleLike(commentId, username));
        verify(likeRepo, times(1)).delete(like);
    }

    @Test
    @DisplayName("좋아요 개수 조회 정상 케이스 - 게시글")
    void test_getLikeCountOfArticle() {
        Long articleId = randNumb();
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        String username = userAccount.getUsername();
        Article article = Article.of(userAccount, "title", "content", "#hash");
        int originalLikeCount = (int) randNumb();

        when(articleRepo.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userAccount));
        when(likeRepo.countByArticle(article)).thenReturn(originalLikeCount);
        assertEquals(originalLikeCount, articleService.getLikeCount(articleId));
    }


    @Test
    @DisplayName("좋아요 토글 시 게시글이나 댓글이 없으면 에러")
    void test_toggleLike_withoutArticleOrComment() {
        Long articleId = randNumb();
        Long commentId = randNumb();
        String username = "tstUser";
        when(articleRepo.findById(articleId)).thenReturn(Optional.empty());
        when(commentRepo.findById(articleId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> articleService.toggleLike(articleId, username));
        assertThrows(RuntimeException.class, () -> commentService.toggleLike(commentId, username));
    }

    @Test
    @DisplayName("좋아요 토글 시 유저가 없으면 에러")
    void test_toggleLike_withoutUser() {
        Long articleId = 1L;
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article = Article.of(userAccount, "random1", "random2", "random3");
        String username = "randomUsername";
        when(articleRepo.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> articleService.toggleLike(articleId, username));
    }

    @Test
    @DisplayName("좋아요 개수 조회 시 게시글이나 댓글이 없으면 에러")
    void test_getLikeCount_withoutArticleOrComment() {
        Long articleId = randNumb();
        Long commentId = randNumb();
        when(articleRepo.findById(articleId)).thenReturn(Optional.empty());
        when(commentRepo.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> articleService.getLikeCount(articleId));
        assertThrows(RuntimeException.class, () -> commentService.getLikeCount(commentId));
    }
}