package com.example.tgbot.bot.repository;

import com.example.tgbot.bot.model.Account;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUserId(Long userId);
    List<Account> findAllByOrderByIdDesc(Limit limit);
}
