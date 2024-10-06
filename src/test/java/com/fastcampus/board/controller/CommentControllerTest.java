package com.fastcampus.board.controller;

import static com.fastcampus.board.TestHelper.randNumb;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fastcampus.board.TestSecurityConfig;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.dto.request.CommentRequest;
import com.fastcampus.board.service.CommentService;
import com.fastcampus.board.util.FormDataEncoder;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("뷰 컨트롤러 - 댓글")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private CommentService commentService;

    public CommentControllerTest(@Autowired MockMvc mvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 새 댓글 & 대댓글 등록 - 정상 호출")
    @Test
    void test_createComment() throws Exception {
        // Given new comment info
        long articleId = randNumb();
        long parentId = randNumb();
        CommentRequest commentRequest1 = CommentRequest.of(articleId, null, "content1");
        CommentRequest commentRequest2 = CommentRequest.of(articleId, parentId, "content2");
        willDoNothing().given(commentService).saveComment(any(CommentDto.class));

        // When requesting
        // Then saves the new comment properly
        mvc.perform(
                post("/comments/new")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(formDataEncoder.encode(commentRequest1))
                    .content(formDataEncoder.encode(commentRequest2))
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles/" + articleId))
            .andExpect(redirectedUrl("/articles/" + articleId));


        mvc.perform(
                post("/comments/new")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(formDataEncoder.encode(commentRequest2))
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles/" + articleId))
            .andExpect(redirectedUrl("/articles/" + articleId));

        then(commentService).should(times(2)).saveComment(any(CommentDto.class));
    }

    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 댓글 삭제 - 정상 호출")
    @Test
    void test_deleteComment() throws Exception {
        // Given articleId and commentId
        long articleId = randNumb();
        long commentId = randNumb();

        // When requesting
        // Then deleted that corresponding comment properly
        mvc.perform(
                post("/comments/" + commentId + "/delete")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles/" + articleId))
            .andExpect(redirectedUrl("/articles/" + articleId));
        then(commentService).should().deleteComment(commentId, "testUser");
    }

    @WithUserDetails(value = "testUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 댓글 삭제 - 사용자 다름")
    @Test
    void test_deleteComment_anotherAuthor() throws Exception {
        // Given articleId and commentId
        long articleId = randNumb();
        long commentId = randNumb();

        // When requesting
        // Then deleted that corresponding comment properly
        mvc.perform(
                post("/comments/" + commentId + "/delete")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                    .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/articles/" + articleId))
            .andExpect(redirectedUrl("/articles/" + articleId));
        then(commentService).should(never()).deleteComment(commentId, "wrongUser");
    }

    // TODO 댓글 업데이트
}