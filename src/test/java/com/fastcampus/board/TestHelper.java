package com.fastcampus.board;

import com.fastcampus.board.domain.Article;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.dto.ArticleDto;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.dto.UserAccountDto;
import java.time.LocalDateTime;
import java.util.Random;

public class TestHelper {

    private static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final Random RANDOM = new Random();

    //픽스쳐
    public static final UserAccountDto USER_ACCOUNT_DTO = UserAccountDto.of(
        randNumb(), "hcho", "password", "hcho@mail.com", "winkyhcho", "This is memo",
        LocalDateTime.now(), "hcho", LocalDateTime.now(), "hcho");

    //픽스쳐
    public static UserAccount USER_ACCOUNT = USER_ACCOUNT_DTO.toEntity();

    //픽스쳐
    public static Article ARTICLE = Article.of(USER_ACCOUNT, "title", "content", "#tag");

    public static ArticleDto createArticleDto(UserAccountDto userAccountDto, String title, String content, String hashtag) {
        return ArticleDto.of(randNumb(), userAccountDto, title, content, hashtag,
            LocalDateTime.now(), randString(5), LocalDateTime.now(), randString(5));
    }

    public static ArticleDto createArticleDto() {
        return createArticleDto(USER_ACCOUNT_DTO, randString(5), randString(5), "#" + randString(5));
    }

    public static CommentDto createCommentDto(UserAccountDto userAccountDto, String content) {
        return CommentDto.of(randNumb(), randNumb(), userAccountDto, content,
            LocalDateTime.now(), randString(5), LocalDateTime.now(), randString(5));
    }

    public static CommentDto createCommentDto() {
        return createCommentDto(USER_ACCOUNT_DTO, randString(5));
    }


    public static long randNumb() {
        return RANDOM.nextLong(100) + 1;
    }

    public static String randString(long length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(UPPERCASE_ALPHABET.length());
            sb.append(UPPERCASE_ALPHABET.charAt(index));
        }
        return sb.toString();
    }

    public static String randString() {
        return randString(randNumb());
    }

}
