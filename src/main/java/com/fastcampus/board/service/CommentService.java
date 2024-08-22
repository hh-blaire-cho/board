package com.fastcampus.board.service;

import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> searchComments(Long articleId) { //댓글 고유 아이디가 아니라, 상위 게시글 아이디. 서비스에서는 고유 아이디로 댓글을 검색하지 않음
        return commentRepository.findByArticle_Id(articleId)
                .stream()
                .map(CommentDto::from)
                .toList();
    }

    public void saveComment(CommentDto dto, UserAccount userAccount) {
        try {
            Comment comment = dto.toEntity(articleRepository.getReferenceById(dto.articleId()), userAccount);
            commentRepository.save(comment);
        } catch (EntityNotFoundException e) {
            log.warn("Comment save fail. Cannot find mapping article - dto: {}", dto);
        }
    }

    public void updateComment(CommentDto dto) {
        try {
            Comment comment = commentRepository.getReferenceById(dto.id());
            if (dto.content() != null) {
                comment.setContent(dto.content());
            }
            commentRepository.save(comment);
        } catch (EntityNotFoundException e) {
            log.warn("comment update fail. cannot find that comment - dto: {}", dto);
        }

    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
