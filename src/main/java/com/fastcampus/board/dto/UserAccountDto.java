package com.fastcampus.board.dto;

import com.fastcampus.board.domain.UserAccount;
import java.time.LocalDateTime;

public record UserAccountDto(
        String username,
        String password,
        String email,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static UserAccountDto of(
            String username,
            String password,
            String email,
            String memo,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {
        return new UserAccountDto(username, password, email, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static UserAccountDto of(
            String username,
            String password,
            String email,
        String memo
    ) {
        return new UserAccountDto(username, password, email, memo, null, null, null, null);
    }

    public static UserAccountDto from(UserAccount entity) {
        return new UserAccountDto(
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
                username,
                password,
                email,
                memo
        );
    }

}