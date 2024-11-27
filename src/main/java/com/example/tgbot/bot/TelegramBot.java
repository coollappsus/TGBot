package com.example.tgbot.bot;

import com.example.tgbot.bot.handler.TelegramUpdateMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final String BOT_USER_NAME = "Coollappsus chat-gpt bot";

    private final TelegramUpdateMessageHandler telegramUpdateMessageHandler;

    public TelegramBot(
            @Value("${token.bot}") String botToken,
            TelegramUpdateMessageHandler telegramUpdateMessageHandler
    ) {
        super(new DefaultBotOptions(), botToken);
        this.telegramUpdateMessageHandler = telegramUpdateMessageHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            var method = processUpdate(update);
            if (method != null ) {
                sendTypingIndicator(update.getMessage().getChatId());
                sendApiMethod(method);
            }
        } catch (Exception e) {
            log.error("Error while processing update", e);
            try {
                sendUserErrorMessage(update.getMessage().getChatId());
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private BotApiMethod<?> processUpdate(Update update) {
        return update.hasMessage()
                ? telegramUpdateMessageHandler.handleMessage(update.getMessage())
                : null;
    }

    public void sendTypingIndicator(long chatId) {
        SendChatAction action = SendChatAction.builder()
                .chatId(chatId)
                .action("typing")
                .build();
        try {
            execute(action);
        } catch (Exception e) {
            log.error("Error while processing update", e);
            try {
                sendUserErrorMessage(chatId);
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    private void sendUserErrorMessage(Long userId) throws TelegramApiException {
        sendApiMethod(SendMessage.builder()
                .chatId(userId)
                .text("Произошла ошибка, попробуйте позже")
                .build());
    }

    @Override
    public String getBotUsername() {
        return BOT_USER_NAME;
    }
}