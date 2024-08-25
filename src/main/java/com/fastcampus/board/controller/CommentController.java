package com.fastcampus.board.controller;


import com.fastcampus.board.dto.UserAccountDto;
import com.fastcampus.board.dto.request.CommentRequest;
import com.fastcampus.board.service.CommentService;
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


    @PostMapping("/new")
    public String postNewComment(CommentRequest commentRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        commentService.saveComment(commentRequest.toDto(UserAccountDto.of(
                "rhkwk3333", "password", "hcho302@mail.com", "KOLALALA", "memo", null, null, null, null
        )));

        return "redirect:/articles/" + commentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, Long articleId) {
        commentService.deleteComment(commentId);

        return "redirect:/articles/" + articleId;
    }

}