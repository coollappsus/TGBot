package com.example.tgbot.bot.service.openai;

import com.example.tgbot.bot.service.openai.api.dto.Message;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatHistory(
        List<Message> chatMessages
) {
}