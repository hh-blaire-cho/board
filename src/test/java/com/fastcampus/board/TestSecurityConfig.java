package com.fastcampus.board;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.fastcampus.board.config.SecurityConfig;
import com.fastcampus.board.domain.UserAccount;
import com.fastcampus.board.repository.UserAccountRepository;
import java.util.Optional;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserAccountRepository userAccountRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountRepository.findById(anyString()))
            .willReturn(Optional.of(UserAccount.of(
                "testUser",
                "qwerty",
                "test@email.com",
                "testMemo"
            )));
    }

}
