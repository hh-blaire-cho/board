package com.fastcampus.board.repository;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.Like;
import com.fastcampus.board.domain.UserAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAccountAndArticle(UserAccount userAccount, Article article);

    Optional<Like> findByUserAccountAndComment(UserAccount userAccount, Comment comment);

    List<Like> findAllByArticle(Article article);

    List<Like> findAllByComment(Comment comment);

    List<Like> findByArticle_Id(Long articleId);

    List<Like> findByComment_Id(Long commentId);

    int countByArticle(Article article);

    int countByComment(Comment comment);

}
