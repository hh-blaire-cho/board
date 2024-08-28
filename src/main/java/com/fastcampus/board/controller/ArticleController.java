package com.fastcampus.board.controller;


import com.fastcampus.board.domain.constant.FormStatus;
import com.fastcampus.board.domain.constant.SearchType;
import com.fastcampus.board.dto.UserAccountDto;
import com.fastcampus.board.dto.request.ArticleRequest;
import com.fastcampus.board.dto.response.ArticleResponse;
import com.fastcampus.board.dto.response.ArticleWithCommentsResponse;
import com.fastcampus.board.service.ArticleService;
import com.fastcampus.board.service.PaginationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    // TODO: 실제 인증 정보를 넣어줘야 한다.
    private final UserAccountDto temp_user_dto = UserAccountDto.of(
            "iady7777", "pw", "hcho302@mail.com", "KOLALA", "memo", null, null, null, null
    );

    @Operation(summary = "display all article with pagination")
    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {

        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());

        return "articles/index";
    }

    @Operation(summary = "display the selected article")
    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        map.addAttribute("article", article);
        map.addAttribute("comments", article.commentResponses());
        map.addAttribute("totalCount", articleService.getArticlesCount());

        return "articles/detail";
    }

    @Operation(summary = "display new article writing form")
    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    @Operation(summary = "post a new article")
    @PostMapping("/form")
    public String postNewArticle(ArticleRequest articleRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.saveArticle(articleRequest.toDto(temp_user_dto));

        return "redirect:/articles";
    }

    @Operation(summary = "display existing article editing form")
    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));
        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);
        return "articles/form";
    }

    @Operation(summary = "update changed article")
    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId, ArticleRequest articleRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.updateArticle(articleId, articleRequest.toDto(temp_user_dto));

        return "redirect:/articles/" + articleId;
    }

    @Operation(summary = "delete existing article")
    @PostMapping("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.deleteArticle(articleId);

        return "redirect:/articles";
    }

}