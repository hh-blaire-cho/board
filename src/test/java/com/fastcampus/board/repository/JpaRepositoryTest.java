package com.fastcampus.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fastcampus.board.config.JpaConfig;
import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.UserAccount;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepo;
    private final CommentRepository commentRepo;
    private final UserAccountRepository userRepo;

    public JpaRepositoryTest(
        @Autowired ArticleRepository articleRepo,
        @Autowired CommentRepository commentRepo,
        @Autowired UserAccountRepository userRepo
    ) {
        this.articleRepo = articleRepo;
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
    }

    @Test
    void selectTest() {
        List<Article> articles = articleRepo.findAll();
        assertThat(articles).isNotNull().hasSize(123);
    }

    @Test
    void insertTest() {
        long previousCount1 = articleRepo.count();
        long previousCount2 = userRepo.count();
        UserAccount user = userRepo.save(UserAccount.of("user", "pw", null, null, null));

        articleRepo.save(Article.of(user, "new article1", "new content2", "#spring"));
        articleRepo.save(Article.of(user, "new article1", "new content2", "#summer"));

        assertThat(articleRepo.count()).isEqualTo(previousCount1 + 2);
        assertThat(userRepo.count()).isEqualTo(previousCount2 + 1);
    }

    @Test
    void updateTest() {
        Article article = articleRepo.findById(1L).orElseThrow();
        String updatedHashtag = "#updated";
        article.setHashtag(updatedHashtag);
        Article savedArticle = articleRepo.saveAndFlush(article);
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @Test
    void deleteTest() {
        Article article = articleRepo.findById(1L).orElseThrow();
        long prevArticleCount = articleRepo.count();
        long prevCommentCount = commentRepo.count();
        int deletedCommentsSize = article.getComments().size();

        articleRepo.delete(article);

        assertThat(articleRepo.count()).isEqualTo(prevArticleCount - 1);
        assertThat(commentRepo.count()).isEqualTo(prevCommentCount - deletedCommentsSize);
    }

}