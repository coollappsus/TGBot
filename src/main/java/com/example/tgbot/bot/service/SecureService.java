package com.example.tgbot.bot.service;

import com.example.tgbot.bot.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@AllArgsConstructor
public class SecureService {

    public static final String NOT_ENOUGH_PERMISSIONS_TEXT = "Недостаточно прав для выполнения данной операции";

    private final AccountRepository accountRepository;

    public boolean isAdmin(Message event) {
        return accountRepository.findByUserId(event.getFrom().getId()).getRole().isAdmin();
    }
}
