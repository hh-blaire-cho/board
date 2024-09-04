package com.fastcampus.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@Table(indexes = {
    @Index(columnList = "email", unique = true),
    @Index(columnList = "createdAt"),
    @Index(columnList = "createdBy")
})
@Entity
public class UserAccount extends AuditingFields {

    @Id
    @Column(length = 50)
    private String username;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(length = 100)
    private String email;

    @Setter
    @Column(length = 100)
    private String nickname;

    @Setter
    private String memo;


    protected UserAccount() {
    }

    private UserAccount(String username, String password, String email, String nickname,
        String memo) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
    }

    public static UserAccount of(String userId, String userPassword, String email, String nickname,
        String memo) {
        return new UserAccount(userId, userPassword, email, nickname, memo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAccount that)) {
            return false;
        }
        return username != null && username.equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}