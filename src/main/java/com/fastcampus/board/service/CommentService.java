package com.fastcampus.board.service;

import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> searchComments(Long articleId) { //댓글 고유 아이디가 아니라, 상위 게시글 아이디. 서비스에서는 고유 아이디로 댓글을 검색하지 않음
        return List.of();
    }

    public void saveComment(CommentDto dto) {
    }

    public void updateComment(CommentDto dto) {
    }

    public void deleteComment(Long id) {
    }

}
