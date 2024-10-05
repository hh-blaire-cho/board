package com.fastcampus.board.dto.security;

import com.fastcampus.board.dto.UserAccountDto;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record BoardPrincipal(
    String username,
    String password,
    Collection<? extends GrantedAuthority> authorities,
    String email,
    String memo
) implements UserDetails {

    public static BoardPrincipal of(String username, String password, String email, String memo) {

        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new BoardPrincipal(
            username,
            password,
            roleTypes.stream()
                .map(RoleType::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet())
            ,
            email,
            memo
        );
    }

    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
            dto.username(),
            dto.password(),
            dto.email(),
            dto.memo()
        );
    }

    public UserAccountDto toDto() {
        return UserAccountDto.of(
            username,
            password,
            email,
            memo
        );
    }


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public enum RoleType {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }

}