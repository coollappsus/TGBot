package com.example.tgbot.bot.service.openai;

import com.example.tgbot.bot.service.openai.api.OpenAIClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfiguration {

    @Bean
    public OpenAIClient openAIClient(
            @Value("${open.ai.token}") String botToken,
            RestTemplateBuilder restTemplateBuilder) {
        return new OpenAIClient(botToken, restTemplateBuilder.build());
    }

}