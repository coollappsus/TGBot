package com.example.tgbot.bot.service;

import com.example.tgbot.bot.model.Account;
import com.example.tgbot.bot.model.Role;
import com.example.tgbot.bot.model.Subscribe;
import com.example.tgbot.bot.repository.AccountRepository;
import com.example.tgbot.bot.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static com.example.tgbot.bot.model.Roles.USER_ROLE_ID;

@Service
@AllArgsConstructor
public class AccountService {

    private static final Double SUPPORT_RATIO = 0.15;
    private static final Double DEFAULT_PRICE = 0.13;

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

    public void increaseBalance(String userName, Double sum) {
        if (StringUtils.isBlank(userName) || sum == null) {
            throw new IllegalArgumentException("Передано неккоректное имя пользователя или сумма для начисления");
        }

        Account account = accountRepository.findByUserName(userName);
        if (account == null) {
            throw new IllegalArgumentException("Аккаунт с таким именем не найден. Необходимо пройти регистрацию или " +
                    "проверить имя пользователя");
        }
        account.setBalance(sum);
        accountRepository.save(account);

        Subscribe subscribe = account.getSubscribe();
        subscribe.setActive(true);
        subscribeService.save(subscribe);
    }

    public void decreaseBalance(String userName, Double sum) {
        if (StringUtils.isBlank(userName) || sum == null) {
            throw new IllegalArgumentException("Передано неккоректное имя пользователя или сумма для списания");
        }
        if (sum == 0) {
            sum = DEFAULT_PRICE;
        }

        Account account = accountRepository.findByUserName(userName);
        if (account == null) {
            throw new IllegalArgumentException("Аккаунт с таким именем не найден. Необходимо пройти регистрацию или " +
                    "проверить имя пользователя");
        }

        double newBalanceAccount = Double.max(account.getBalance() - ((sum * SUPPORT_RATIO) + sum), 0D);
        if (newBalanceAccount == 0) {
            Subscribe subscribe = account.getSubscribe();
            subscribe.setActive(false);
            subscribeService.save(subscribe);
        }

        account.setBalance(newBalanceAccount);
        accountRepository.save(account);
    }

    public Account findByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }
}
