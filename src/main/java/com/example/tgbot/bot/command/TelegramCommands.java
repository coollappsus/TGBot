package com.example.tgbot.bot.command;

import lombok.Getter;

@Getter
public enum TelegramCommands {
    START_COMMAND("/start"),
    CLEAR_COMMAND("/clear"),
    BALANCE_COMMAND("/balance"),
    ID_COMMAND("/id"),

    ADD_MONEY_COMMAND("/add_money"),
    TOTAL_BALANCE_COMMAND("/total_balance"),
    GET_LAST_CONTACT_INFO_COMMAND("/get_last_contact_info");

    private final String commandValue;

    TelegramCommands(String commandValue) {
        this.commandValue = commandValue;
    }
}