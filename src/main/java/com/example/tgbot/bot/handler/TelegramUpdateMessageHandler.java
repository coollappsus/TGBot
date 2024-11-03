package com.example.tgbot.bot.handler;

import com.example.tgbot.bot.command.TelegramCommandsDispatcher;
import com.example.tgbot.bot.model.Account;
import com.example.tgbot.bot.service.AccountService;
import com.example.tgbot.bot.service.openai.ChatGptService;
import com.example.tgbot.bot.service.telegram.TelegramAsyncMessageSender;
import jakarta.transaction.Transactional;
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
    private final ChatGptService chatGptService;

    public BotApiMethod<?> handleMessage(Message message) {
        log.info("Начало обработки сообщения: message={}", message);
        if (telegramCommandsDispatcher.isCommand(message)) {
            return telegramCommandsDispatcher.processCommand(message);
        }

        var chatId = message.getChatId().toString();
        if (!hasActiveSubscribe(message.getFrom())) {
            return getErrorMessage("На балансе недостаточно средств для совершения запроса или не пройден " +
                    "процесс регистрации", chatId);
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
        Double balanceBeforeRequest = getTotalBalance();

        SendMessage result = message.hasVoice()
                ? telegramVoiceHandler.processVoice(message)
                : telegramTextHandler.processTextMessage(message);
        result.setParseMode(ParseMode.MARKDOWN);

        Double balanceAfterRequest = getTotalBalance();
        accountService.decreaseBalance(message.getFrom().getFirstName(), balanceBeforeRequest - balanceAfterRequest);
        return result;
    }

    private SendMessage getErrorMessage(Throwable throwable, String chatId) {
        log.error("Произошла ошибка, chatId={}", chatId, throwable);
        return SendMessage.builder()
                .chatId(chatId)
                .text("Произошла ошибка, попробуйте позже")
                .build();
    }

    private SendMessage getErrorMessage(String text, String chatId) {
        log.error(text + ", chatId={}", chatId);
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    private boolean hasActiveSubscribe(User user) {
        Account account = accountService.findByUserId(user.getId());
        if (account == null) {
            return false;
        }
        return account.getSubscribe().isActive();
    }

    @Transactional
    private Double getTotalBalance() {
        return Double.parseDouble(chatGptService.getTotalBalance());
    }

}