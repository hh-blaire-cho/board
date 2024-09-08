package com.fastcampus.board;

import com.fastcampus.board.dto.ArticleDto;
import com.fastcampus.board.dto.ArticleWithCommentsDto;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.dto.UserAccountDto;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

import static java.time.LocalDateTime.now;

public class TestHelper {

    private static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final Random RANDOM = new Random();

    //픽스쳐
    public static final UserAccountDto USER_ACCOUNT_DTO = UserAccountDto.of(
            "hcho", "password", "hcho@mail.com", "winkyhcho", "This is memo",
            now(), "hcho", now(), "hcho");

    public static ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(randNumb(), USER_ACCOUNT_DTO, title, content, hashtag,
                now(), randString(5), now(), randString(5));
    }

    public static ArticleDto createArticleDto() {
        return createArticleDto(randString(5), randString(5), "#" + randString(5));
    }

    public static CommentDto createCommentDto(Long itsId, Long articleId, Long parentId, String content, LocalDateTime createTime) {
        return CommentDto.of(itsId, articleId, parentId, USER_ACCOUNT_DTO, content,
                createTime, randString(5), now(), randString(5));
    }

    public static CommentDto createCommentDto(Long itsId, Long articleId, Long parentId) {
        return createCommentDto(itsId, articleId, parentId, randString(5), now());
    }

    public static CommentDto createCommentDto() {
        return createCommentDto(randNumb(), randNumb(), null, randString(5), now());
    }

    public static ArticleWithCommentsDto createArticleWithCommentsDto() {
        return createArticleWithCommentsDto(randNumb(), randString(5), randString(5), randString(5), Set.of());
    }

    public static ArticleWithCommentsDto createArticleWithCommentsDto(Long id, String title, String content, String hashtag, Set<CommentDto> dtos) {
        return ArticleWithCommentsDto.of(id, USER_ACCOUNT_DTO, dtos, title, content, hashtag,
                now(), randString(5), now(), randString(5));
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
