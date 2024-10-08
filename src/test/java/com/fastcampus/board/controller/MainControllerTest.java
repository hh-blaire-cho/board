package com.fastcampus.board.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fastcampus.board.config.SecurityConfig;
import com.fastcampus.board.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@DisplayName("메인 페이지 리다이렉션")
@Import(SecurityConfig.class)
@WebMvcTest(MainController.class)
class MainControllerTest {

    private final MockMvc mvc;

    @MockBean
    private UserAccountRepository userAccountRepository;

    public MainControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void test_forwardToArticlesPage() throws Exception {
        // Given nothing
        // When requesting root page
        // Then redirects to articles page
        mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("forward:/articles"))
            .andExpect(forwardedUrl("/articles"))
            .andDo(MockMvcResultHandlers.print());
    }

}