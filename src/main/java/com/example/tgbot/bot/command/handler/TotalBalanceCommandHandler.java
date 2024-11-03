package com.example.tgbot.bot.command.handler;

import com.example.tgbot.bot.command.TelegramCommandHandler;
import com.example.tgbot.bot.command.TelegramCommands;
import com.example.tgbot.bot.service.openai.ChatGptService;
import com.example.tgbot.bot.service.SecureService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.example.tgbot.bot.service.SecureService.NOT_ENOUGH_PERMISSIONS_TEXT;

@Component
@AllArgsConstructor
public class TotalBalanceCommandHandler implements TelegramCommandHandler {

    private static final String TOTAL_BALANCE_TEXT = "Баланс составляет - %s руб.";

    private final SecureService secureService;
    private final ChatGptService chatGptService;

    @Override
    public BotApiMethod<?> processCommand(Message update) {
        if (!secureService.isAdmin(update)) {
            SendMessage.builder()
                    .chatId(update.getChatId())
                    .text(NOT_ENOUGH_PERMISSIONS_TEXT)
                    .build();
        }
        String balance = chatGptService.getTotalBalance();
        return SendMessage.builder()
                .chatId(update.getChatId())
                .text(TOTAL_BALANCE_TEXT.formatted(balance))
                .build();
    }

    @Override
    public TelegramCommands getSupportedCommand() {
        return TelegramCommands.TOTAL_BALANCE_COMMAND;
    }
}
