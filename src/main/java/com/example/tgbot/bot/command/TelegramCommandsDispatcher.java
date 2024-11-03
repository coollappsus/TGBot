package com.example.tgbot.bot.command;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Service
@AllArgsConstructor
public class TelegramCommandsDispatcher {

    private final List<TelegramCommandHandler> telegramCommandHandlerList;

    public BotApiMethod<?> processCommand(Message message) {
        if (!isCommand(message)) {
            throw new IllegalArgumentException("Переданное сообщение не является командой");
        }
        var text = message.getText();
        boolean isStrictlyEquals = getCountWordsInCommand(text) <= 2;

        var suitedHandler = telegramCommandHandlerList.stream()
                .filter(it -> {
                    if (isStrictlyEquals) {
                        return it.getSupportedCommand().getCommandValue().equals(text);
                    }
                    return text.startsWith(it.getSupportedCommand().getCommandValue());
                })
                .findAny();
        if (suitedHandler.isEmpty()) {
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text("Данная команда не поддерживается: command=%s".formatted(text))
                    .build();
        }
        return suitedHandler.orElseThrow().processCommand(message);
    }

    public boolean isCommand(Message message) {
        return message.hasText() && message.getText().startsWith("/");
    }

    public int getCountWordsInCommand(String text) {
        return text.split("[^/a-zA-Z\\d]+").length;
    }
}