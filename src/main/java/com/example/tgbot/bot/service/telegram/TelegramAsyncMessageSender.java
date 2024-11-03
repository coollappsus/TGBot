package com.example.tgbot.bot.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Service
public class TelegramAsyncMessageSender {

    public static final String WAIT_MESSAGE_TEXT = "Ваш запрос принят в обработку, ожидайте";
    private static final int TELEGRAM_SIZE_MESSAGE_LIMIT = 4096;
    //TODO: временное решение, так как существуют проблемы с парсингом форматирования, если разбивать сообщение
    // на несколько
    private boolean isOverLimit;

    private final DefaultAbsSender defaultAbsSender;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    public TelegramAsyncMessageSender(@Lazy DefaultAbsSender defaultAbsSender) {
        this.defaultAbsSender = defaultAbsSender;
    }

    public void sendMessageAsync(String chatId, Supplier<SendMessage> action,
                                 Function<Throwable, SendMessage> onErrorHandler) {
        log.info("Ассинхронная отправка сообщения ожидания: chatId={}", chatId);
        var message = getAndSendMessage(chatId, WAIT_MESSAGE_TEXT);

        CompletableFuture.supplyAsync(action, executorService)
                .exceptionally(onErrorHandler)
                .thenAccept(sendMessage -> {
                    log.info("Ассинхронная отправка сообщения результата: chatId={}", chatId);
                    sendMessagesInParts(chatId, message, sendMessage);
                });
    }

    private void sendMessagesInParts(String chatId, Message message, SendMessage sendMessage) {
        if (sendMessage.getText().length() > TELEGRAM_SIZE_MESSAGE_LIMIT) {
            isOverLimit = true;
            for (int index = 0; index < sendMessage.getText().length(); index += TELEGRAM_SIZE_MESSAGE_LIMIT) {
                if (index == 0) {
                    sendEditMessage(chatId, message.getMessageId(), sendMessage.getText().substring(index,
                            Math.min(index + TELEGRAM_SIZE_MESSAGE_LIMIT, sendMessage.getText().length())));
                } else {
                    getAndSendMessage(chatId, sendMessage.getText().substring(index,
                            Math.min(index + TELEGRAM_SIZE_MESSAGE_LIMIT, sendMessage.getText().length())));
                }
            }
        } else {
            sendEditMessage(chatId, message.getMessageId(), sendMessage.getText());
        }
    }

    private Message getAndSendMessage(String chatId, String message) {
        try {
            return defaultAbsSender.execute(SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки ответа клиенту с chatId=" + chatId, e);
            throw new RuntimeException(e);
        }
    }

    private void sendEditMessage(String chatId, int editMessageId, String newMessageText) {
        try {
//            List<MessageEntity> messageEntityList = List.of(MessageEntity.builder().type("pre").offset(0).length(10).language("java").build());
            defaultAbsSender.execute(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(editMessageId)
                    .text(newMessageText)
                    .parseMode(isOverLimit
                            ? null
                            : ParseMode.MARKDOWN)
//                    .entities(messageEntityList)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки ответа клиенту с chatId=" + chatId, e);
            throw new RuntimeException(e);
        }
    }
}