package com.example.tgbot.bot.command.handler;

import com.example.tgbot.bot.command.TelegramCommandHandler;
import com.example.tgbot.bot.command.TelegramCommands;
import com.example.tgbot.bot.model.Account;
import com.example.tgbot.bot.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@AllArgsConstructor
public class BalanceCommandHandler implements TelegramCommandHandler {

    private static final String BALANCE_TEXT = "Баланс составляет - %s руб.";
    private static final String NOT_EXISTS_ACCOUNT_TEXT = """
            %s, у тебя отсутствует аккаунт! Воспользуйся командой /start для регистрации
            """;

    private final AccountService accountService;

    @Override
    public BotApiMethod<?> processCommand(Message update) {
        Account account = accountService.findByUserId(update.getFrom().getId());
        if (account != null) {
            return SendMessage.builder()
                    .chatId(update.getChatId())
                    .text(BALANCE_TEXT.formatted(account.getBalance().toString()))
                    .build();
        }
        return SendMessage.builder()
                .chatId(update.getChatId())
                .text(NOT_EXISTS_ACCOUNT_TEXT.formatted(update.getFrom().getFirstName()))
                .build();
    }

    @Override
    public TelegramCommands getSupportedCommand() {
        return TelegramCommands.BALANCE_COMMAND;
    }
}
