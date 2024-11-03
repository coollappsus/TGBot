package com.example.tgbot.bot.config;

import com.example.tgbot.bot.TelegramBot;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * конфигурация приложения
 *
 */
@Configuration
public class AppConfiguration {

    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor encryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = stringPBEConfig();
        encryptor.setConfig(config);
        return encryptor;
    }

    @Bean
    public TelegramBotsApi getTelegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBot);
        return telegramBotsApi;
    }

    private SimpleStringPBEConfig stringPBEConfig() {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("token");
        // дальше дефолтные параметры
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        return config;
    }
}
