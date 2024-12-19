package com.example.tgbot.bot.model;

import lombok.Getter;

@Getter
public enum MessageTypes {
    AUDIO((1.8 + 0.45) / 60, 0.177 / 1000),
    AUDIO_TRANSCRIBE(1.8 / 60, 0.1 / 1000),
    TEXT(0.05 / 1000, 0.177 / 1000);

    private final double requestPrice;
    private final double responsePrice;

    MessageTypes(double requestPrice, double responsePrice) {
        this.requestPrice = requestPrice;
        this.responsePrice = responsePrice;
    }
}
