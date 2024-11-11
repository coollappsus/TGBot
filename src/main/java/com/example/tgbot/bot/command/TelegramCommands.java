package com.example.tgbot.bot.command;

public enum TelegramCommands {
    START_COMMAND("/start"),
    CLEAR_COMMAND("/clear"),
    BALANCE_COMMAND("/balance"),
    ID_COMMAND("/id"),

    ADD_MONEY_COMMAND("/add_money"),
    TOTAL_BALANCE_COMMAND("/total_balance");

    private final String commandValue;

    TelegramCommands(String commandValue) {
        this.commandValue = commandValue;
    }

    public String getCommandValue() {
        return commandValue;
    }
}