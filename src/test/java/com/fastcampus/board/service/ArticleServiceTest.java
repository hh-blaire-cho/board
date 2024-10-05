package com.fastcampus.board.service;

import static com.fastcampus.board.TestHelper.USER_ACCOUNT_DTO;
import static com.fastcampus.board.TestHelper.createArticleDto;
import static com.fastcampus.board.TestHelper.randNumb;
import static com.fastcampus.board.TestHelper.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.domain.constant.SearchType;
import com.fastcampus.board.dto.ArticleDto;
import com.fastcampus.board.dto.ArticleWithCommentsDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class) //이렇게 해야, 굳이 Application 을 안켜서 가벼워짐.
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepo;

    @Mock
    private UserAccountRepository userAccountRepo;

    @InjectMocks // Mock using Mock, ArticleRepository 를 주입하는 대상, ArticleService 에 의존함 (=사용당함)
    private ArticleService sut; // System Under Test

    private static Page<Article> searchResults;
    private static UserAccount userAccount;

    @BeforeAll
    static void setUpBeforeAll() {
        userAccount = USER_ACCOUNT_DTO.toEntity();
        Article article1 = Article.of(userAccount, "search", "content", "#hash");
        Article article2 = Article.of(userAccount, "key", "content", "#hash");
        Article article3 = Article.of(userAccount, "vord", "content", "#hash");
        searchResults = new PageImpl<>(List.of(article1, article2, article3));

    }

    @DisplayName("아이디로 게시글 검색 시, 해당 게시글 반환")
    @Test
    void test_getArticlesUsingArticleId() {
        // Given articleId
        long articleId = randNumb();
        Article article = Article.of(userAccount, "random1", "random2", "random3");
        given(articleRepo.findById(articleId)).willReturn(Optional.of(article));

        // When searching it
        ArticleDto articles = sut.getArticle(articleId);

        // Then returns that specific article
        assertThat(articles).isNotNull();
        assertThat(articles)
                .hasFieldOrPropertyWithValue("title", "random1")
                .hasFieldOrPropertyWithValue("content", "random2")
                .hasFieldOrPropertyWithValue("hashtag", "random3");
        then(articleRepo).should().findById(articleId);
    }

    @DisplayName("아이디로 게시글 검색 시, 해당 게시글&댓글 반환")
    @Test
    void test_getArticleWithCommentsUsingGoodArticleId() {
        // Given articleId
        long articleId = randNumb();
        Article article = Article.of(userAccount, "random1", "random2", "random3");
        given(articleRepo.findById(articleId)).willReturn(Optional.of(article));

        // When searching that articleWithComments
        ArticleWithCommentsDto articles = sut.getArticleWithComments(articleId);

        // Then returns that specific article
        assertThat(articles).isNotNull();
        assertThat(articles)
                .hasFieldOrPropertyWithValue("title", "random1")
                .hasFieldOrPropertyWithValue("content", "random2")
                .hasFieldOrPropertyWithValue("hashtag", "random3");
        then(articleRepo).should().findById(articleId);
    }

    @DisplayName("없는 아이디로 게시글 검색 시, 예외 던지기")
    @Test
    void test_getArticleUsingWrongArticleId() {
        // Given wrong articleId
        long articleId = randNumb();
        given(articleRepo.findById(articleId)).willReturn(Optional.empty());

        // When searching it
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // Then returns that specific article
        assertThat(t).isInstanceOf(EntityNotFoundException.class);
        assertThat(t).hasMessage("Cannot find that article with given id : " + articleId);
    }

    @DisplayName("없는 아이디로 게시글(w댓글) 검색 시, 예외 던지기")
    @Test
    void test_getArticleWithCommentsUsingWrongArticleId() {
        // Given wrong articleId
        long articleId = randNumb();
        given(articleRepo.findById(articleId)).willReturn(Optional.empty());

        // When searching that articleWithComments
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        // Then returns throwable
        assertThat(t).isInstanceOf(EntityNotFoundException.class);
        assertThat(t).hasMessage("Cannot find that article with given id : " + articleId);
    }

    @DisplayName("파라미터로 게시글 검색 시, 관련 게시글들 반환")
    @Test
    void test_searchArticlesUsingParameters() {
        // Given search parameters
        SearchType searchType = SearchType.TITLE;
        String searchKey = "exist-keyword";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepo.findByTitleContaining(searchKey, pageable)).willReturn(searchResults);

        // When searching it
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKey, pageable);

        // Then returns matching articles
        assertNotNull(articles);
        assertFalse(articles.isEmpty());
        assertEquals(3, articles.getTotalElements());

        verify(articleRepo, never()).findByContentContaining(any(), any());
        verify(articleRepo, never()).findByUserAccount_UsernameContaining(any(), any());
        verify(articleRepo, never()).findByUserAccount_EmailContaining(any(), any());
        verify(articleRepo, never()).findByHashtag(any(), any());
        then(articleRepo).should().findByTitleContaining(any(), any());
    }

    @DisplayName("파라미터 없이 게시글 검색 시, 모든 게시글 목록 반환")
    @Test
    void test_searchArticlesWithoutParameter() {
        // Given no parameters
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepo.findAll(pageable)).willReturn(searchResults);

        // When searching it
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // Then returns all articles
        assertNotNull(articles);
        assertFalse(articles.isEmpty());
        assertEquals(3, articles.getTotalElements());

        verify(articleRepo, never()).findByTitleContaining(any(), any());
        verify(articleRepo, never()).findByContentContaining(any(), any());
        verify(articleRepo, never()).findByUserAccount_UsernameContaining(any(), any());
        verify(articleRepo, never()).findByUserAccount_EmailContaining(any(), any());
        verify(articleRepo, never()).findByHashtag(any(), any());
        then(articleRepo).should().findAll(pageable);
    }

    @DisplayName("파라미터 매칭 결과가 없으면, 빈 목록 반환")
    @Test
    void test_searchArticlesUsingNonMatchingParameter() {
        // Given no parameters
        SearchType searchType = SearchType.TITLE;
        String searchKey = "non-exist-keyword";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepo.findByTitleContaining(searchKey, pageable)).willReturn(Page.empty());

        // When searching it
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKey, pageable);

        // Then returns matching articles
        assertNotNull(articles);
        assertTrue(articles.isEmpty());
        then(articleRepo).should().findByTitleContaining(any(), any());
    }


    @DisplayName("게시글 정보 입력 시, 게시글 저장")
    @Test
    void test_savingArticle() {
        // Given article info
        ArticleDto dto = createArticleDto();
        Article article = Article.of(userAccount, "random1", "random2", "random3");
        given(userAccountRepo.getReferenceById(dto.userAccountDto().username())).willReturn(userAccount);
        given(articleRepo.save(any(Article.class))).willReturn(article);

        // When try saving it
        sut.saveArticle(dto);

        // Then should save it properly
        then(userAccountRepo).should().getReferenceById(dto.userAccountDto().username());
        then(articleRepo).should().save(any(Article.class));
    }

    @DisplayName("바뀐 정보 입력 시 게시글 수정")
    @Test
    void test_updatingArticle() {
        // Given updated info with original
        Article original = Article.of(userAccount, "title", "content", "#tag");
        ArticleDto updated = createArticleDto("newtitle", "newcontent", "#newtag");
        given(articleRepo.getReferenceById(updated.id())).willReturn(original);
        given(userAccountRepo.getReferenceById(updated.userAccountDto().username()))
            .willReturn(updated.userAccountDto().toEntity());

        // When try updating it
        sut.updateArticle(updated.id(), updated); // 시험 단계에서 original id 는 알 수 없고 null 이니, 정확한 아이디를 넣었다고 가정.

        // Then should update it properly
        assertThat(original)
                .hasFieldOrPropertyWithValue("title", updated.title())
                .hasFieldOrPropertyWithValue("content", updated.content())
                .hasFieldOrPropertyWithValue("hashtag", updated.hashtag());
        then(articleRepo).should().getReferenceById(updated.id());
        then(userAccountRepo).should().getReferenceById(updated.userAccountDto().username());
    }

    @DisplayName("없는 게시글 수정 시도 시, 경고 로그만 찍고 끝")
    @Test
    void test_updatingNonExistedArticle() {
        // Given updated info without original
        ArticleDto updated = createArticleDto();
        given(articleRepo.getReferenceById(updated.id())).willThrow(EntityNotFoundException.class);

        // When try updating it
        sut.updateArticle(updated.id(), updated); // 시험 단계에서 original id 는 알 수 없고 null 이니, 정확한 아이디를 넣었다고 가정

        // Then should NOT update it but just logging warning
        then(articleRepo).should().getReferenceById(updated.id());
        verify(articleRepo, never()).save(any(Article.class));
    }

    @DisplayName("아이디로 게시글 삭제")
    @Test
    void test_deletingArticle() {
        // Given article id
        long articleId = randNumb();
        String usrname = randString(3);
        willDoNothing().given(articleRepo).deleteByIdAndUserAccount_Username(articleId, usrname);

        // When try deleting using that id
        sut.deleteArticle(articleId, usrname);

        // Then should delete it properly
        then(articleRepo).should().deleteByIdAndUserAccount_Username(articleId, usrname);
    }

    @DisplayName("게시글 갯수 조회")
    @Test
    void test_determiningArticlesCount1(){
        // Given nothing
        long expected = randNumb();
        given(articleRepo.count()).willReturn(expected);

        // When counting articles
        long actual = sut.getArticlesCount();

        // Then should return correct count
        assertEquals(expected, actual);
        then(articleRepo).should().count();
    }

}