package com.example.tgbot.bot.model;

public enum MessageTypes {
    AUDIO((1.75 + 0.45) / 60, 0.177 / 1000),
    AUDIO_TRANSCRIBE(1.75 / 60, 0.1 / 1000),
    TEXT(0.045 / 1000, 0.177 / 1000);

    private final double requestPrice;
    private final double responsePrice;

    MessageTypes(double requestPrice, double responsePrice) {
        this.requestPrice = requestPrice;
        this.responsePrice = responsePrice;
    }

    public double getRequestPrice() {
        return requestPrice;
    }

    public double getResponsePrice() {
        return responsePrice;
    }
}
