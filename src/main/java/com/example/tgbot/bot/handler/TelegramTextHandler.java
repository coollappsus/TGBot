package com.example.tgbot.bot.handler;

import com.example.tgbot.bot.service.BalanceService;
import com.example.tgbot.bot.service.openai.ChatGptService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.example.tgbot.bot.model.MessageTypes.TEXT;

@Service
@AllArgsConstructor
public class TelegramTextHandler {

    private final ChatGptService gptService;
    private final BalanceService balanceService;

    public SendMessage processTextMessage(Message message) {
        var text = message.getText();
        var chatId = message.getChatId();

        var gptGeneratedText = gptService.getResponseChatForUser(chatId, text);
        balanceService.decreaseBalanceAccount(TEXT, message, gptGeneratedText);
        return new SendMessage(chatId.toString(), gptGeneratedText);
    }
}