package com.fastcampus.board.controller;


import com.fastcampus.board.dto.request.CommentRequest;
import com.fastcampus.board.dto.security.BoardPrincipal;
import com.fastcampus.board.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "create new comment")
    @PostMapping("/new")
    public String postNewComment(@AuthenticationPrincipal BoardPrincipal auth, CommentRequest commentRequest) {
        commentService.saveComment(commentRequest.toDto(auth.toDto()));
        return "redirect:/articles/" + commentRequest.articleId();
    }

    @Operation(summary = "delete selected comment")
    @PostMapping("/{commentId}/delete")
    public String deleteComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal BoardPrincipal boardPrincipal,
        Long articleId) {
        commentService.deleteComment(commentId, boardPrincipal.getUsername());

        return "redirect:/articles/" + articleId;
    }

    // 좋아요 토글 기능
    @PostMapping("/{commentId}/like")
    public String toggleCommentLike(
        @PathVariable Long commentId, Long articleId,
        @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        commentService.toggleLike(commentId, boardPrincipal.getUsername());
        return "redirect:/articles/" + articleId;
    }

    // TODO 댓글 업데이트
}