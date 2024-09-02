package com.fastcampus.board.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Spring Data REST 통합 테스트는 무겁고, 공부 목적 외 불필요")
@DisplayName("Data Rest Test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class DataRestTest {

    private final MockMvc mvc;
    private final String halJson = "application/hal+json";
    private final String restHeader = "/restapi";

    public DataRestTest(@Autowired MockMvc mockMvc) {
        this.mvc = mockMvc;
    }

    @DisplayName(("[api] 총 게시글 리스트 조회"))
    @Test
    void test_requestArticleList() throws Exception {
        // given nothing
        // when request article list
        // then return that json responses
        mvc.perform(get(restHeader + "/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf(halJson)));
    }

    @DisplayName(("[api] 게시글 단건 아이디로 조회"))
    @Test
    void test_requestArticle() throws Exception {
        // given nothing
        // when request article by its id
        // then return that json responses
        mvc.perform(get(restHeader + "/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf(halJson)));
    }

    @DisplayName(("[api] 게시글에 딸린 댓글들 조회"))
    @Test
    void test_requestCommentFromArticle() throws Exception {
        // given nothing
        // when request comments from the corresponding article
        // then return that json responses
        mvc.perform(get(restHeader + "/articles/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf(halJson)));
    }

    @DisplayName(("[api] 총 댓글 리스트 조회"))
    @Test
    void test_requestCommentList() throws Exception {
        // given nothing
        // when request comment list
        // then return that json responses
        mvc.perform(get(restHeader + "/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf(halJson)));
    }

    @DisplayName(("[api] 댓글 단건 아이디로 조회"))
    @Test
    void test_requestComment() throws Exception {
        // given nothing
        // when request a comment by its id
        // then return that json responses
        mvc.perform(get(restHeader + "/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf(halJson)));
    }

    @DisplayName("[api] 회원 관련 API 는 일체 제공하지 않음")
    @Test
    void test_userAccountSecurity() throws Exception {
        // Given nothing
        // When request UserAccount related
        // then throws exception due to security
        mvc.perform(get(restHeader + "/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(post(restHeader + "/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(put(restHeader + "/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(patch(restHeader + "/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(delete(restHeader + "/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(head(restHeader + "/userAccounts")).andExpect(status().isNotFound());
    }
}