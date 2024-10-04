package com.fastcampus.board.service;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.Like;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.dto.LikeDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.CommentRepository;
import com.fastcampus.board.repository.LikeRepository;
import com.fastcampus.board.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserAccountRepository userAccountRepository;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> searchComments(Long articleId) { //댓글 고유 아이디가 아니라, 상위 게시글 아이디. 서비스에서는 고유 아이디로 댓글을 검색하지 않음
        return commentRepository.findByArticle_Id(articleId)
                .stream()
                .map(CommentDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentDto getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .map(CommentDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find that comment with given id : " + commentId));
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

        //게시글 존재여부 검사 후 사용자 존재여부 검사
        try {
            userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().username());
        } catch (EntityNotFoundException e) {
            log.warn("Comment save fail. Cannot find writer - dto: {}", dto);
            return;
        }

        Comment comment = dto.toEntity(article, userAccount);

        if (dto.parentCommentId() != null) {
            Comment parentComment = commentRepository.getReferenceById(dto.parentCommentId());
            parentComment.addChildComment(comment); // child.setParentCommentId 도 같이 해줌
        } else {
            commentRepository.save(comment); // 대댓글일떈 레포에 신규 저장 안함
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

    // 이제부터는 CRUD가 아닌 좋아요에 대한 기능들
    @Transactional
    public void toggleLike(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
            new RuntimeException(String.format("cannot find comment id: %d", commentId)));

        UserAccount userAccount = userAccountRepository.findByUsername(username).orElseThrow(() ->
            new RuntimeException(String.format("cannot find user name: %d", commentId)));

        Optional<Like> likeEntity = likeRepository.findByUserAccountAndComment(userAccount, comment);
        if (likeEntity.isPresent()) {
            likeRepository.delete(likeEntity.get());
        } else {
            likeRepository.save(Like.createForComment(comment, userAccount));
        }
    }

    @Transactional(readOnly = true)
    public List<LikeDto> getLikes(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
            new RuntimeException(String.format("cannot find comment id: %d", commentId)));

        return likeRepository.findAllByComment(comment).stream().map(LikeDto::from).toList();
    }

    public int getLikeCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
            new RuntimeException(String.format("cannot find comment id: %d", commentId)));

        return likeRepository.countByComment(comment);
    }
}
