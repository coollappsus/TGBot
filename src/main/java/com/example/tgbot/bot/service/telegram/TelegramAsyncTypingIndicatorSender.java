package com.example.tgbot.bot.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Тут все просто. Запускаем, бесконечно отправляем статус "Печатает", пока не остановим. Вызывающий сам должен
 * побеспокоится об остановке
 */
@Slf4j
@Service
public class TelegramAsyncTypingIndicatorSender {

    private final AtomicBoolean running;
    private final DefaultAbsSender defaultAbsSender;
    private CompletableFuture<Void> future;

    public TelegramAsyncTypingIndicatorSender(DefaultAbsSender defaultAbsSender) {
        this.defaultAbsSender = defaultAbsSender;
        this.running = new AtomicBoolean(false);
    }

    public void start(long chatId) {
        running.set(true);
        future = CompletableFuture.runAsync(() -> {
            while (running.get()) {
                sendTypingIndicator(chatId);

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    /**
     * Занимается отправкой статуса печати
     *
     * @param chatId - чат, куда необходимо отправить сообщение
     */
    private void sendTypingIndicator(long chatId) {
        try {
            defaultAbsSender.execute(SendChatAction.builder()
                    .chatId(chatId)
                    .action("typing")
                    .build());
        } catch (TelegramApiException ex) {
            log.error("Ошибка отправки статуса печати ответа клиенту с chatId=%s{}", chatId, ex);
            throw new RuntimeException(ex);
        }
    }

    public void stop() {
        running.set(false);
        if (future != null) {
            future.cancel(true);
        }
    }
}
