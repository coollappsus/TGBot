package com.example.tgbot.bot.service;

import com.example.tgbot.bot.model.Subscribe;
import com.example.tgbot.bot.repository.SubscribeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;

    public Subscribe createNewSubscribe() {
        Subscribe subscribe = Subscribe.builder()
                .isActive(false)
                .build();
        return subscribeRepository.save(subscribe);
    }

    public void save(Subscribe subscribe) {
        subscribeRepository.save(subscribe);
    }
}
