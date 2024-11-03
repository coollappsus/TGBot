package com.example.tgbot.bot.service.openai.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Choice (
    @JsonProperty("message") Message message
) {}