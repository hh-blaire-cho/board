package com.fastcampus.board.controller;

import static com.fastcampus.board.TestHelper.createArticleDto;
import static com.fastcampus.board.TestHelper.createArticleWithCommentsDto;
import static com.fastcampus.board.TestHelper.randNumb;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fastcampus.board.TestSecurityConfig;
import com.fastcampus.board.domain.constant.FormStatus;
import com.fastcampus.board.domain.constant.SearchType;
import com.fastcampus.board.dto.ArticleDto;
import com.fastcampus.board.dto.request.ArticleRequest;
import com.fastcampus.board.dto.response.ArticleResponse;
import com.fastcampus.board.service.ArticleService;
import com.fastcampus.board.service.PaginationService;
import com.fastcampus.board.util.FormDataEncoder;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("뷰 컨트롤러 - 게시글")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private PaginationService paginationService;

    public ArticleControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][GET] 게시판 페이지 - 정상 호출")
    @Test
    public void test_ArticlesViewRequest() throws Exception {
        // Given Nothing
        // When Request Articles View
        // Then Returns That View
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class)))
            .willReturn(Page.empty()); // Page.empty() = 아무 페이지나 상관없다는 뜻
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt()))
            .willReturn(List.of());

        mvc.perform(get("/articles"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(model().attributeExists("searchTypes"))
            .andExpect(model().attributeExists("paginationBarNumbers"))
            .andExpect(model().attributeExists("articles"));

        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시판 페이지 - 페이징 & 정렬 기능")
    @Test
    void test_ArticlesPagePagingAndSorting() throws Exception {
        // Given paging and sorting params
        // When searching articles page with that
        // then show sorted articles
        String sortName = "title";
        String direction = "desc";
        int pageNumber = (int) randNumb();
        int pageSize = pageNumber + (int) randNumb();
        List<Integer> barNumbers = List.of();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));

        given(articleService.searchArticles(null, null, pageable))
            .willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages()))
            .willReturn(barNumbers);


        mvc.perform(
                get("/articles")
                    .queryParam("page", String.valueOf(pageNumber))
                    .queryParam("size", String.valueOf(pageSize))
                    .queryParam("sort", sortName + "," + direction)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"))
            .andExpect(model().attribute("paginationBarNumbers", barNumbers));

        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view][GET] 게시판 페이지 - 검색")
    @Test
    public void test_ArticlesPageSearched() throws Exception {
        // Given search keyword
        // When searching articles page view
        // Then returns selected articles using that keyword as parameter

        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        given(articleService.searchArticles(eq(searchType), eq(searchKeyword), any(Pageable.class)))
            .willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt()))
            .willReturn(List.of());

        mvc.perform(
                get("/articles")
                    .queryParam("searchType", searchType.name())
                    .queryParam("searchValue", searchKeyword)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"))
            .andExpect(model().attributeExists("searchTypes"));

        then(articleService).should().searchArticles(eq(searchType), eq(searchKeyword), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 페이지 - 정상 호출")
    @Test
    public void test_ArticleViewRequest() throws Exception {
        // Given article id
        // When Request Article View
        // Then Returns That View
        Long articleId = randNumb();
        Long totalCount = randNumb();
        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());
        given(articleService.getArticlesCount()).willReturn(totalCount);

        mvc.perform(get("/articles/" + articleId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/detail"))
            .andExpect(model().attributeExists("article"))
            .andExpect(model().attributeExists("comments"));

        then(articleService).should().getArticleWithComments(articleId);
        then(articleService).should().getArticlesCount();
    }

    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 새 게시글 등록 - 정상 호출")
    @Test
    void test_createArticleController() throws Exception {
        // Given new article info
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        // When requesting
        // Then saves the new article properly
        mvc.perform(
                post("/articles/form")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(formDataEncoder.encode(articleRequest))
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles"))
            .andExpect(redirectedUrl("/articles"));

        then(articleService).should().saveArticle(any(ArticleDto.class));
    }

    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void test_updatingArticleController() throws Exception {
        // Given updated article info
        long articleId = randNumb();
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).updateArticle(eq(articleId), any(ArticleDto.class));

        // When requesting
        // Then updates the article properly
        mvc.perform(
                post("/articles/" + articleId + "/form")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(formDataEncoder.encode(articleRequest))
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles/" + articleId))
            .andExpect(redirectedUrl("/articles/" + articleId));

        then(articleService).should().updateArticle(eq(articleId), any(ArticleDto.class));
    }

    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 삭제 - 정상 호출")
    @Test
    void test_deleteArticleController() throws Exception {
        // Given articleId
        long articleId = 1L;
        String username = "testUser";
        willDoNothing().given(articleService).deleteArticle(articleId, username);

        // When requesting
        // Then deleted that corresponding article properly
        mvc.perform(
                post("/articles/" + articleId + "/delete")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles"))
            .andExpect(redirectedUrl("/articles"));
        then(articleService).should().deleteArticle(articleId, username);
    }

    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] 새 게시글 작성 페이지")
    @Test
    void test_creatingWritingNewArticlePage() throws Exception {
        // Given nothing
        // When requesting the page of writing article
        // Then returns that page properly

        mvc.perform(get("/articles/form"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/form"))
            .andExpect(model().attribute("formStatus", FormStatus.ARTICLE_CREATE));
    }

    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] 게시글 수정 페이지")
    @Test
    void test_creatingModifyingArticlePage() throws Exception {
        // Given nothing
        // When requesting the page of writing article
        // Then returns that page properly

        long articleId = 1L;
        ArticleDto dto = createArticleDto();
        given(articleService.getArticle(articleId)).willReturn(dto);

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/form"))
            .andExpect(model().attribute("article", ArticleResponse.from(dto)))
            .andExpect(model().attribute("formStatus", FormStatus.ARTICLE_UPDATE));
        then(articleService).should().getArticle(articleId);
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void test_ArticleSearchViewRequest() throws Exception {
        // Given Nothing
        // When Request ArticleSearch View
        // Then Returns That View
        mvc.perform(get("/articles/search"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(model().attributeExists("articles/search"));
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    public void test_HashTagSearchViewRequest() throws Exception {
        // Given Nothing
        // When Request ArticleHashTagSearch View
        // Then Returns That View
        mvc.perform(get("/articles/search-hashtag"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(model().attributeExists("articles/search-hashtag"));
    }

}