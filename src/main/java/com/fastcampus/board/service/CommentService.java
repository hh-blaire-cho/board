package com.fastcampus.board.service;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.CommentRepository;
import com.fastcampus.board.repository.UserAccountRepository;
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
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> searchComments(Long articleId) { //댓글 고유 아이디가 아니라, 상위 게시글 아이디. 서비스에서는 고유 아이디로 댓글을 검색하지 않음
        return commentRepository.findByArticle_Id(articleId)
                .stream()
                .map(CommentDto::from)
                .toList();
    }

    public void saveComment(CommentDto dto) {
        Article article;
        UserAccount userAccount;
        try {
            article = articleRepository.getReferenceById(dto.articleId());
        } catch (EntityNotFoundException e) {
            log.warn("Comment save fail. Cannot find mapping article - dto: {}", dto);
            return;
        }

        //게시글 검사 후 사용자 존재여부 검사
        try {
            userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().username());
        } catch (EntityNotFoundException e) {
            log.warn("Comment save fail. Cannot find writer - dto: {}", dto);
            return;
        }

        Comment comment = dto.toEntity(article, userAccount);
        commentRepository.save(comment);
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
