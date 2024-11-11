package com.example.tgbot.bot.command.handler;

import com.example.tgbot.bot.command.TelegramCommandHandler;
import com.example.tgbot.bot.command.TelegramCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class IdCommandHandler implements TelegramCommandHandler {

    @Override
    public BotApiMethod<?> processCommand(Message update) {
        return SendMessage.builder()
                .chatId(update.getChatId())
                .text(update.getFrom().getId().toString())
                .build();
    }

    @Override
    public TelegramCommands getSupportedCommand() {
        return TelegramCommands.ID_COMMAND;
    }
}
