package com.fastcampus.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private String memo;


    protected UserAccount() {
    }

    private UserAccount(String username, String password, String email, String memo) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.memo = memo;
    }

    public static UserAccount of(String username, String password, String email, String memo) {
        return new UserAccount(username, password, email, memo);
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