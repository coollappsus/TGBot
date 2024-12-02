package com.example.tgbot.bot.service;

import com.example.tgbot.bot.model.Account;
import com.example.tgbot.bot.model.Role;
import com.example.tgbot.bot.model.Subscribe;
import com.example.tgbot.bot.repository.AccountRepository;
import com.example.tgbot.bot.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

import static com.example.tgbot.bot.model.Roles.USER_ROLE_ID;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final SubscribeService subscribeService;
    private final RoleRepository roleRepository;

    public void createNewAccount(User user) {
        Optional<Role> role = roleRepository.findById(USER_ROLE_ID.getId());
        Subscribe subscribe = subscribeService.createNewSubscribe();
        Account account = Account.builder()
                .balance(0D)
                .role(role.orElse(null))
                .subscribe(subscribe)
                .userName(user.getFirstName())
                .userId(user.getId())
                .build();
        accountRepository.save(account);
    }

    public Account findByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public String getAccountsInfoByLimit(int limit) {
        List<Account> accounts = accountRepository.findAllByOrderByIdDesc(Limit.of(limit));
        StringBuilder sb = new StringBuilder("Информация о последних аккаунтах(").append(accounts.size())
                .append(" шт)");
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            sb.append("\n").append(i + 1).append(". ").append(acc.getUserName()).append(", ").append(acc.getUserId())
                    .append(", ").append(acc.getBalance()).append(" руб.");
        }
        return sb.toString();
    }
}
