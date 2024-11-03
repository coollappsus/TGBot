package com.example.tgbot.bot.service.openai.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatCompletionRequest(
        String model,
        List<Message> messages) {
}
