package com.fastcampus.board.service;

import static com.fastcampus.board.TestHelper.USER_ACCOUNT_DTO;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Like;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.repository.ArticleRepository;
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

    @MockBean
    private UserAccountRepository userRepo;

    @MockBean
    private LikeRepository likeRepo;


    @Test
    @DisplayName("좋아요 추가 정상 케이스")
    void test_toggleLikePost_addLike() {
        Long articleId = 1L;
        String username = "randomUsername";
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article = Article.of(userAccount, "title", "content", "#hash");
        when(articleRepo.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userAccount));
        when(likeRepo.findByUserAccountAndArticle(userAccount, article)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> articleService.toggleLike(articleId, username));
        verify(likeRepo, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("좋아요 취소 정상 케이스")
    void test_toggleLikePost_removeLike() {
        Long articleId = 1L;
        String username = "randomUsername";
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article = Article.of(userAccount, "title", "content", "#hash");
        Like likeEntity = Like.createForArticle(userAccount, article);

        when(articleRepo.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(userAccount));
        when(likeRepo.findByUserAccountAndArticle(userAccount, article)).thenReturn(Optional.of(likeEntity));

        assertDoesNotThrow(() -> articleService.toggleLike(articleId, username));
        verify(likeRepo, times(1)).delete(likeEntity);
    }

    @Test
    @DisplayName("좋아요 개수 조회 정상 케이스")
    void test_getLikeCountOfArticle() {
        Long articleId = 1L;
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article = Article.of(userAccount, "title", "content", "#hash");

        when(articleRepo.findById(articleId)).thenReturn(Optional.of(article));
        when(likeRepo.countByArticle(article)).thenReturn(99);
        int likeCount = articleService.getLikeCount(articleId);
        assertEquals(99, likeCount);
    }

    @Test
    @DisplayName("좋아요 추가 시 게시글이 없으면 에러")
    void test_toggleLikePost_withoutPost() {
        Long articleId = 1L;
        String username = "randomUsername";
        when(articleRepo.findById(articleId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> articleService.toggleLike(articleId, username));
    }

    @Test
    @DisplayName("좋아요 추가 시 유저가 없으면 에러")
    void test_toggleLikePost_withoutUser() {
        Long articleId = 1L;
        UserAccount userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article = Article.of(userAccount, "random1", "random2", "random3");
        String username = "randomUsername";
        when(articleRepo.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> articleService.toggleLike(articleId, username));
    }

    @Test
    @DisplayName("좋아요 개수 조회 시 게시글이 없으면 에러")
    void test_getLikeCount_OfArticle_withoutPost() {
        Long articleId = 1L;
        when(articleRepo.findById(articleId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> articleService.getLikeCount(articleId));
    }
}