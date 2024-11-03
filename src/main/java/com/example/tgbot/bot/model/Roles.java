package com.example.tgbot.bot.model;

public enum Roles {

    ADMIN_ROLE_ID(1L),
    USER_ROLE_ID(2L);

    private final Long id;

    Roles(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
