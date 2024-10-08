package com.fastcampus.board.service;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Like;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.domain.constant.SearchType;
import com.fastcampus.board.dto.ArticleDto;
import com.fastcampus.board.dto.ArticleWithCommentsDto;
import com.fastcampus.board.dto.LikeDto;
import com.fastcampus.board.repository.ArticleRepository;
import com.fastcampus.board.repository.LikeRepository;
import com.fastcampus.board.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final LikeRepository likeRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case USERNAME -> articleRepository.findByUserAccount_UsernameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case EMAIL -> articleRepository.findByUserAccount_EmailContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::from);
        };

    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().username());
        articleRepository.save(dto.toEntity(userAccount));
    }

    // 최초 생성할 때엔, id가 순차적으로 알아서 생겨 필요없지만, 수정할 때엔 필요 dto가 id가 없을 수도 있는 유연한 상황 고려
    public void updateArticle(Long articleId, ArticleDto dto) {
        // only title, content, hashtag can be modified
        try {
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().username());
            if (article.getUserAccount().equals(userAccount)) {
                if (dto.title() != null) {
                    article.setTitle(dto.title());
                }
                if (dto.content() != null) {
                    article.setContent(dto.content());
                }
                article.setHashtag(dto.hashtag());
            }
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }
    }

    public void deleteArticle(Long articleId, String username) {
        articleRepository.deleteByIdAndUserAccount_Username(articleId, username);
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find that article with given id : " + articleId));
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find that article with given id : " + articleId));
    }

    public long getArticlesCount() {
        return articleRepository.count();
    }

    @Transactional
    public void toggleLike(Long postId, String username) {
        Article article = articleRepository.findById(postId).orElseThrow(() ->
            new RuntimeException(String.format("cannot find post id: %d", postId)));

        UserAccount userAccount = userAccountRepository.findByUsername(username).orElseThrow(() ->
            new RuntimeException(String.format("cannot find user name: %d", postId)));

        Optional<Like> likeEntity = likeRepository.findByUserAccountAndArticle(userAccount, article);
        if (likeEntity.isPresent()) {
            likeRepository.delete(likeEntity.get());
        } else {
            likeRepository.save(Like.createForArticle(userAccount, article));
        }
    }

    @Transactional(readOnly = true)
    public List<LikeDto> getLikes(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() ->
            new RuntimeException(String.format("cannot find post id: %d", articleId)));

        return likeRepository.findAllByArticle(article).stream().map(LikeDto::from).toList();
    }

    public int getLikeCount(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() ->
            new RuntimeException(String.format("cannot find post id: %d", articleId)));

        return likeRepository.countByArticle(article);
    }
}
