package com.example.tgbot.bot.service.openai;

import com.example.tgbot.bot.service.openai.api.OpenAIClient;
import com.example.tgbot.bot.service.openai.api.dto.ChatCompletionRequest;
import com.example.tgbot.bot.service.openai.api.dto.Message;
import com.example.tgbot.bot.service.openai.api.dto.TotalBalanceRequest;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatGptService {

    private static final String CHAT_GPT_MODEL = "gpt-4o-mini";
    private static final String CHAT_GPT_ROLE = "user";


    private final OpenAIClient openAIClient;
    private final ChatGptHistoryService chatGptHistoryService;

    @Nonnull
    public String getResponseChatForUser(
            Long userId,
            String userTextInput) {
        chatGptHistoryService.createHistoryIfNotExist(userId);
        var history = chatGptHistoryService.addMessageToHistory(
                userId,
                Message.builder()
                        .content(userTextInput)
                        .role(CHAT_GPT_ROLE)
                        .build()
        );

        var request = ChatCompletionRequest.builder()
                .model(CHAT_GPT_MODEL)
                .messages(history.chatMessages())
                .build();
        var response = openAIClient.createChatCompletion(request);

        var messageFromGpt = response.choices().get(0)
                .message();

        chatGptHistoryService.addMessageToHistory(userId, messageFromGpt);

        return messageFromGpt.content();
    }

    @Nonnull
    public String getTotalBalance() {
        var request = TotalBalanceRequest.builder().build();
        var response = openAIClient.createTotalBalance(request);
        return response.balance().toString();
    }
}