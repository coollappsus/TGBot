package com.example.tgbot.bot.service.openai.api;

import com.example.tgbot.bot.service.openai.api.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
public class OpenAIClient {

    private final String token;
    private final RestTemplate restTemplate;

    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) {
        String url = "https://api.proxyapi.ru/openai/v1/chat/completions";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        httpHeaders.set("Content-type", "application/json");

        HttpEntity<ChatCompletionRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

        ResponseEntity<ChatCompletionResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, ChatCompletionResponse.class
        );
        return responseEntity.getBody();
    }

    public TranscriptionResponse createTranscription(CreateTranscriptionRequest request) {
        String url = "https://api.proxyapi.ru/openai/v1/audio/transcriptions";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        httpHeaders.set("Content-type", "multipart/form-data");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(request.audioFile()));
        body.add("model", request.model());

        var httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<TranscriptionResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, TranscriptionResponse.class
        );
        return responseEntity.getBody();
    }

    public TotalBalanceResponse createTotalBalance(TotalBalanceRequest request) {
        String url = "https://api.proxyapi.ru/proxyapi/balance";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        httpHeaders.set("Content-type", "application/json");

        HttpEntity<TotalBalanceRequest> httpEntity = new HttpEntity<>(request, httpHeaders);

        ResponseEntity<TotalBalanceResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, httpEntity, TotalBalanceResponse.class
        );
        return responseEntity.getBody();
    }
}