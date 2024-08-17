package com.fastcampus.board;

import com.fastcampus.board.dto.ArticleDto;
import com.fastcampus.board.dto.ArticleWithCommentsDto;
import com.fastcampus.board.dto.CommentDto;
import com.fastcampus.board.dto.UserAccountDto;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

public class TestHelper {

    private static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final Random RANDOM = new Random();

    //픽스쳐
    public static final UserAccountDto USER_ACCOUNT_DTO = UserAccountDto.of(
            "hcho", "password", "hcho@mail.com", "winkyhcho", "This is memo",
        LocalDateTime.now(), "hcho", LocalDateTime.now(), "hcho");

    public static ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(randNumb(), USER_ACCOUNT_DTO, title, content, hashtag,
            LocalDateTime.now(), randString(5), LocalDateTime.now(), randString(5));
    }

    public static ArticleDto createArticleDto() {
        return createArticleDto(randString(5), randString(5), "#" + randString(5));
    }

    public static CommentDto createCommentDto(String content) {
        return CommentDto.of(randNumb(), randNumb(), USER_ACCOUNT_DTO, content,
            LocalDateTime.now(), randString(5), LocalDateTime.now(), randString(5));
    }

    public static CommentDto createCommentDto() {
        return createCommentDto(randString(5));
    }

    public static ArticleWithCommentsDto createArticleWithCommentsDto() {
        return createArticleWithCommentsDto(randNumb(), randString(5), randString(5), randString(5));
    }

    public static ArticleWithCommentsDto createArticleWithCommentsDto(Long id, String title, String content, String hashtag) {
        return ArticleWithCommentsDto.of(id, USER_ACCOUNT_DTO, Set.of(), title, content, hashtag,
                LocalDateTime.now(), randString(5), LocalDateTime.now(), randString(5));
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
