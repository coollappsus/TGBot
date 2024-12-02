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
public class GetLastContactInfoCommandHandler implements TelegramCommandHandler {

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
        int countContacts = Integer.getInteger(getCountContacts(update.getText()));
        String result = accountService.getAccountsInfoByLimit(countContacts);
        return SendMessage.builder()
                .chatId(update.getChatId())
                .text(result)
                .build();
    }

    @Override
    public TelegramCommands getSupportedCommand() {
        return TelegramCommands.GET_LAST_CONTACT_INFO_COMMAND;
    }

    private String getCountContacts(String text) {
        return text.split("[^/a-zA-Z\\d]+")[4];
    }
}
