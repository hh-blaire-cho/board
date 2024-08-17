package com.fastcampus.board.repository;

import com.fastcampus.board.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

}
