package com.fastcampus.board.controller;


import com.fastcampus.board.dto.UserAccountDto;
import com.fastcampus.board.dto.request.CommentRequest;
import com.fastcampus.board.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class CommentController {

    private final CommentService commentService;

    // TODO: 실제 인증 정보를 넣어줘야 한다.
    private final UserAccountDto temp_user_dto = UserAccountDto.of(
            "iady7777", "pw", "hcho302@mail.com", "KOLALA", "memo", null, null, null, null
    );


    @Operation(summary = "create new comment")
    @PostMapping("/new")
    public String postNewComment(CommentRequest commentRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        commentService.saveComment(commentRequest.toDto(temp_user_dto));

        return "redirect:/articles/" + commentRequest.articleId();
    }

    @Operation(summary = "delete selected comment")
    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, Long articleId) {
        commentService.deleteComment(commentId);

        return "redirect:/articles/" + articleId;
    }

}