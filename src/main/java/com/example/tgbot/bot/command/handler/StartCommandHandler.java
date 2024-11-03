package com.example.tgbot.bot.command.handler;

import com.example.tgbot.bot.command.TelegramCommandHandler;
import com.example.tgbot.bot.command.TelegramCommands;
import com.example.tgbot.bot.model.Account;
import com.example.tgbot.bot.service.AccountService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartCommandHandler implements TelegramCommandHandler {

    private static final String HELLO_MESSAGE = """
            Привет %s,
            Твоя регистрация прошла успешно!
            Этим ботом ты можешь пользоваться для общения с чат GPT.
            Каждое сообщение запоминается для контекста.
            Если, видишь, что чат GPT тебя плохо понимает, попробуй очистить контекст с помощью команды /clear.
            Чаще всего, это не требуется.
            """;

    private static final String REGISTRATION_EXISTS = "%s, твой аккаунт уже создан, повторная регистрация не требуется";

    private final AccountService accountService;

    public StartCommandHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public BotApiMethod<?> processCommand(Message message) {
        Account account = accountService.findByUserId(message.getFrom().getId());
        if (account == null) {
            accountService.createNewAccount(message.getFrom());
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(HELLO_MESSAGE.formatted(
                            message.getChat().getFirstName()
                    ))
                    .build();
        }
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(REGISTRATION_EXISTS.formatted(
                        message.getChat().getFirstName()
                ))
                .build();
    }

    @Override
    public TelegramCommands getSupportedCommand() {
        return TelegramCommands.START_COMMAND;
    }
}