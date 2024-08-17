package com.fastcampus.board.domain.type;

import lombok.Getter;

//검색어 종류
@Getter
public enum SearchType {
    TITLE("제목"),
    CONTENT("본문"),
    USERNAME("유저네임"),
    NICKNAME("닉네임"),
    HASHTAG("해시태그");

    private final String description;

    SearchType(String description) {
        this.description = description;
    }
}