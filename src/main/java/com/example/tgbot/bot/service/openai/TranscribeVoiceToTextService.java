package com.example.tgbot.bot.service.openai;

import com.example.tgbot.bot.service.openai.api.OpenAIClient;
import com.example.tgbot.bot.service.openai.api.dto.CreateTranscriptionRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@AllArgsConstructor
public class TranscribeVoiceToTextService {

    private static final String TRANSCRIBE_MODEL = "whisper-1";

    private final OpenAIClient openAIClient;

    public String transcribe(File audioFile) {
        var response = openAIClient.createTranscription(CreateTranscriptionRequest.builder()
                        .audioFile(audioFile)
                        .model(TRANSCRIBE_MODEL)
                .build());
        return response.text();
    }

}