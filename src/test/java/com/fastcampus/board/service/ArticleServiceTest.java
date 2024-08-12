package com.fastcampus.board.service;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.domain.type.SearchType;
import com.fastcampus.board.dto.ArticleDto;
import com.fastcampus.board.dto.ArticleWithCommentsDto;
import com.fastcampus.board.dto.UserAccountDto;
import com.fastcampus.board.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비지니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class) //이렇게 해야, 굳이 Application 을 안켜서 가벼워짐.
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepo;

    @InjectMocks // Mock using Mock, ArticleRepository 를 주입하는 대상, ArticleService 에 의존함 (=사용당함)
    private ArticleService sut; // System Under Test

    private UserAccountDto userAccountDto;
    private UserAccount userAccount;

    @BeforeEach
    public void setUp() { //픽스쳐를 만드는 과정
        userAccountDto = UserAccountDto.of(
                createRandomId(), "hcho", "password", "hcho@mail.com", "winkyhcho", "This is memo",
                LocalDateTime.now(), "hcho", LocalDateTime.now(), "hcho");

        userAccount = userAccountDto.toEntity();
    }

    @DisplayName("게시글 아이디 조회 시, 해당 게시글 반환")
    @Test
    void test_getArticlesUsingArticleId() {
        // Given articleId
        long articleId = createRandomId();
        Article article = createArticle("random1", "random2", "random3");
        given(articleRepo.findById(articleId)).willReturn(Optional.of(article));

        // When searching it
        ArticleWithCommentsDto articles = sut.getArticleByArticleId(articleId);

        // Then returns that specific article
        assertThat(articles).isNotNull();
        assertThat(articles)
                .hasFieldOrPropertyWithValue("title", "random1")
                .hasFieldOrPropertyWithValue("content", "random2")
                .hasFieldOrPropertyWithValue("hashtag", "random3");
        then(articleRepo).should().findById(articleId);
    }

    @DisplayName("게시글 검색 시 관련 게시글들 반환")
    @Test
    void test_searchArticlesUsingParameters() {
        // Given search parameters
        SearchType searchType = SearchType.TITLE;
        String searchKey = "search-keyword";

        // When searching it
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKey, null);

        // Then returns matching articles
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글 정보 입력 시, 게시글 저장")
    @Test
    void test_savingArticle() {
        // Given article info
        ArticleDto dto = createArticleDto("title", "content", "#tag");
        given(articleRepo.save(any(Article.class))).willReturn(null);

        // When try saving it
        sut.saveArticle(dto);

        // Then should save it properly
        then(articleRepo).should().save(any(Article.class));
    }

    @DisplayName("게시글 아이디와 바뀐 정보 입력 시 수정")
    @Test
    void test_updatingArticle() {
        // Given article id and updated info
        Article original = createArticle("title", "content", "#tag");
        ArticleDto updated = createArticleDto("newtitle", "newcontent", "#newtag");
        given(articleRepo.getReferenceById(updated.id())).willReturn(original);

        // When try updating it
        sut.updateArticle(updated);

        // Then should update it properly
        assertThat(original)
                .hasFieldOrPropertyWithValue("title", updated.title())
                .hasFieldOrPropertyWithValue("content", updated.content())
                .hasFieldOrPropertyWithValue("hashtag", updated.hashtag());
        then(articleRepo).should().getReferenceById(updated.id());
        then(articleRepo).should().save(any(Article.class));
    }

    @DisplayName("게시글 아이디 입력하면 게시글 삭제")
    @Test
    void test_deletingArticle() {
        // Given article id
        long articleId = createRandomId();
        willDoNothing().given(articleRepo).deleteById(articleId);

        // When try deleting using that id
        sut.deleteArticle(articleId);

        // Then should delete it properly
        then(articleRepo).should().deleteById(articleId);
    }


    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L, userAccountDto, title, content, hashtag,
                LocalDateTime.now(), "hcho", LocalDateTime.now(), "hcho");
    }

    private Article createArticle(String title, String content, String hashtag) {
        return Article.of(userAccount, title, content, hashtag);
    }

    private long createRandomId() {
        return (long) (Math.random() * 99);
    }

}