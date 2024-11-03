package com.example.tgbot.bot.command.handler;

import com.example.tgbot.bot.command.TelegramCommandHandler;
import com.example.tgbot.bot.command.TelegramCommands;
import com.example.tgbot.bot.service.AccountService;
import com.example.tgbot.bot.service.SecureService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.example.tgbot.bot.service.SecureService.NOT_ENOUGH_PERMISSIONS_TEXT;

@Component
@AllArgsConstructor
public class AddMoneyCommandHandler implements TelegramCommandHandler {

    private static final String INCREASE_BALANCE_TEXT = "Баланс аккаунта %s увеличен на %s руб.";

    private final SecureService secureService;
    private final AccountService accountService;

    @Override
    public BotApiMethod<?> processCommand(Message update) {
        if (!secureService.isAdmin(update)) {
            SendMessage.builder()
                    .chatId(update.getChatId())
                    .text(NOT_ENOUGH_PERMISSIONS_TEXT)
                    .build();
        }
        String userName = getUserName(update.getText());
        Double sum = getSum(update.getText());
        accountService.increaseBalance(userName, sum);
        return SendMessage.builder()
                .chatId(update.getChatId())
                .text(INCREASE_BALANCE_TEXT.formatted(userName, sum))
                .build();
    }

    @Override
    public TelegramCommands getSupportedCommand() {
        return TelegramCommands.ADD_MONEY_COMMAND;
    }

    private String getUserName(String text) {
        return text.split("[^/a-zA-Z\\d]+")[2];
    }

    private Double getSum(String text) {
        return Double.parseDouble(text.split("[^/a-zA-Z\\d]+")[3]);
    }
}
