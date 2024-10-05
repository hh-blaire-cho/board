package com.fastcampus.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.Comment;
import com.fastcampus.board.domain.UserAccount;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DisplayName("JPA connection test")
@Import(JpaRepositoryTest.TestJpaConfig.class)
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
    void test_select() {
        List<Article> articles = articleRepo.findAll();
        assertThat(articles).isNotNull().hasSize(123);
    }

    @Test
    void test_insert() {
        long previousCount1 = articleRepo.count();
        long previousCount2 = userRepo.count();
        UserAccount user = userRepo.save(UserAccount.of("user", "pw", null, null));

        articleRepo.save(Article.of(user, "new article1", "new content2", "#spring"));
        articleRepo.save(Article.of(user, "new article1", "new content2", "#summer"));

        assertThat(articleRepo.count()).isEqualTo(previousCount1 + 2);
        assertThat(userRepo.count()).isEqualTo(previousCount2 + 1);
    }

    @Test
    void test_update() {
        Article article = articleRepo.findById(1L).orElseThrow();
        String updatedHashtag = "#updated";
        article.setHashtag(updatedHashtag);
        Article savedArticle = articleRepo.saveAndFlush(article);
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @Test
    void test_delete() {
        Article article = articleRepo.findById(1L).orElseThrow();
        long prevArticleCount = articleRepo.count();
        long prevCommentCount = commentRepo.count();
        int deletedCommentsSize = article.getComments().size();

        articleRepo.delete(article);

        assertThat(articleRepo.count()).isEqualTo(prevArticleCount - 1);
        assertThat(commentRepo.count()).isEqualTo(prevCommentCount - deletedCommentsSize);
    }

    @DisplayName("대댓글 조회 테스트")
    @Test
    void test_selectChildComments() {
        // Given parent comment id as 1 (mock data 설정상 대댓글 4개를 가진 댓글은 1번 댓글임)
        // When selecting it
        // Then returns list of child comments
        Optional<Comment> parentComment = commentRepo.findById(1L);

        assertThat(parentComment).get()
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                .hasSize(4);
    }

    @DisplayName("댓글에 대댓글 삽입 테스트")
    @Test
    void test_insertChildComment() {
        // Given parent comment

        Comment parent = commentRepo.getReferenceById(1L);
        Comment child1 = Comment.of(parent.getArticle(), parent.getUserAccount(), "cocoment");
        Comment child2 = Comment.of(parent.getArticle(), parent.getUserAccount(), "cocoment");
        int originalSize = parent.getChildComments().size();

        // When saving child comment
        parent.addChildComment(child1);
        parent.addChildComment(child2);
        commentRepo.flush();

        // Then saves it
        assertThat(commentRepo.findById(1L)).get()
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                .hasSize(originalSize + 2);
    }

    @DisplayName("댓글 삭제에 따른 대댓글 연계 삭제 테스트")
    @Test
    void test_chainDeleteTest() {
        // Given comment having children
        Comment parent = commentRepo.getReferenceById(1L);
        long previousCommentCount = commentRepo.count();
        int childCount = parent.getChildComments().size();

        // When deleting parent comment
        commentRepo.delete(parent);

        // Then deletes every child
        assertThat(commentRepo.count()).isEqualTo(previousCommentCount - childCount - 1); // 테스트 부모댓글 + 대댓글수
    }

    //TODO 스프링 시큐리티 추가하고 나서, 유저 삭제에 따른 댓글 및 대댓글 삭제 실험해보기
//    @DisplayName("댓글 삭제와 대댓글 전체 연동 삭제 테스트 - 댓글 ID + 유저 ID")
//    @Test

    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig {

        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("jpa-test-user");
        }
    }

}