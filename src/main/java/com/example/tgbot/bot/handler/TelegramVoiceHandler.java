package com.example.tgbot.bot.handler;

import com.example.tgbot.bot.service.BalanceService;
import com.example.tgbot.bot.service.openai.ChatGptService;
import com.example.tgbot.bot.service.openai.TranscribeVoiceToTextService;
import com.example.tgbot.bot.service.telegram.TelegramFileService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.example.tgbot.bot.model.MessageTypes.AUDIO_TRANSCRIBE;
import static com.example.tgbot.bot.model.MessageTypes.AUDIO;

@Service
@AllArgsConstructor
public class TelegramVoiceHandler {

    private final ChatGptService gptService;
    private final TelegramFileService telegramFileService;
    private final TranscribeVoiceToTextService transcribeVoiceToTextService;
    private final BalanceService balanceService;

    /**
     * Обработчик голосовых сообщений.
     * Транскрибирует голос в текст и шлет его в ChatGptService.
     * Если сообщение переслано от другого пользователя, то просто вернет расшифровку.
     *
     * @param message сообщение
     * @return новое сообщение
     */
    public SendMessage processVoice(Message message) {
        var chatId = message.getChatId();
        var voice = message.getVoice();

        var fileId = voice.getFileId();
        var file = telegramFileService.getFile(fileId);
        var text = transcribeVoiceToTextService.transcribe(file);

        boolean isForwardMessage = message.getForwardOrigin() != null;
        if (isForwardMessage) {
            balanceService.decreaseBalanceAccount(AUDIO_TRANSCRIBE, message, text);
            return new SendMessage(chatId.toString(), text);
        }

        var gptGeneratedText = gptService.getResponseChatForUser(chatId, text);
        balanceService.decreaseBalanceAccount(AUDIO, message, gptGeneratedText);
        return new SendMessage(chatId.toString(), gptGeneratedText);
    }
}