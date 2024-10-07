package com.fastcampus.board.domain.constant;

import lombok.Getter;

@Getter
public enum FormStatus {
    ARTICLE_CREATE("게시글 생성", 1),
    ARTICLE_UPDATE("게시글 수정", 2),
    COMMENT_UPDATE("댓글 수정", 3);

    private final String description;
    private final int type;

    FormStatus(String description, int type) {
        this.description = description;
        this.type = type;
    }

}