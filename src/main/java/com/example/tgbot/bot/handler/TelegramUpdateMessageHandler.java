package com.example.tgbot.bot.handler;

import com.example.tgbot.bot.command.TelegramCommandsDispatcher;
import com.example.tgbot.bot.model.Account;
import com.example.tgbot.bot.service.AccountService;
import com.example.tgbot.bot.service.telegram.TelegramAsyncMessageSender;
import com.example.tgbot.bot.service.telegram.TelegramAsyncTypingIndicatorSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Service
@AllArgsConstructor
public class TelegramUpdateMessageHandler {

    private final TelegramCommandsDispatcher telegramCommandsDispatcher;
    private final TelegramAsyncMessageSender telegramAsyncMessageSender;
    private final TelegramTextHandler telegramTextHandler;
    private final TelegramVoiceHandler telegramVoiceHandler;
    private final AccountService accountService;
    private final TelegramAsyncTypingIndicatorSender typingIndicatorSender;

    public BotApiMethod<?> handleMessage(Message message) {
        log.info("Начало обработки сообщения: message={}", message);
        if (telegramCommandsDispatcher.isCommand(message)) {
            return telegramCommandsDispatcher.processCommand(message);
        }

        var chatId = message.getChatId().toString();
        if (!hasActiveSubscribe(message.getFrom())) {
            return getErrorMessage(new Throwable("На балансе недостаточно средств для совершения запроса или " +
                    "не пройден процесс регистрации"), chatId);
        }

        if (message.hasVoice() || message.hasText()) {
            telegramAsyncMessageSender.sendMessageAsync(
                    chatId,
                    () -> handleMessageAsync(message),
                    (throwable) -> getErrorMessage(throwable, chatId)
            );
        }
        return null;
    }

    private SendMessage handleMessageAsync(Message message) {
        typingIndicatorSender.start(message.getChatId());
        SendMessage result = message.hasVoice()
                ? telegramVoiceHandler.processVoice(message)
                : telegramTextHandler.processTextMessage(message);
        result.setParseMode(ParseMode.MARKDOWN);
        typingIndicatorSender.stop();
        return result;
    }

    private SendMessage getErrorMessage(Throwable throwable, String chatId) {
        typingIndicatorSender.stop();
        log.error("Произошла ошибка, chatId={}", chatId, throwable);
        return SendMessage.builder()
                .chatId(chatId)
                .text("Произошла ошибка, попробуйте позже")
                .build();
    }

    private boolean hasActiveSubscribe(User user) {
        Account account = accountService.findByUserId(user.getId());
        if (account == null) {
            return false;
        }
        return account.getSubscribe().isActive();
    }
}