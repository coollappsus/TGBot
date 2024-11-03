package com.example.tgbot.bot.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@Service
public class TelegramFileService {

    private final DefaultAbsSender telegramSender;
    private final String botToken;

    public TelegramFileService(
            @Lazy DefaultAbsSender telegramSender,
            @Value("${token.bot}") String botToken) {
        this.telegramSender = telegramSender;
        this.botToken = botToken;
    }

    public java.io.File getFile(String fileId) {
        File file;
        try {
            file = telegramSender.execute(GetFile.builder()
                    .fileId(fileId)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при скачивании файла с серверов Telegram", e);
            throw new RuntimeException("Ошибка при скачивании файла с серверов Telegram", e);
        }
        var urlToDownloadFile = file.getFileUrl(botToken);
        return getFileFromUrl(urlToDownloadFile);
    }

    private java.io.File getFileFromUrl(String urlToDownloadFile) {
        URL url = getUrl(urlToDownloadFile);
        var fileTemp = getFile();

        try (InputStream inputStream = url.openStream();
             FileOutputStream fileOutputStream = new FileOutputStream(fileTemp)) {
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            log.error("Ошибка при копировании файла из URL-адреса во временный файл", e);
            throw new RuntimeException("Ошибка при копировании файла", e);
        }
        return fileTemp;
    }

    private static URL getUrl(String urlToDownloadFile) {
        try {
            return new URI(urlToDownloadFile).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Ошибка при создании URL для временного файла", e);
            throw new RuntimeException("Ошибка при создании URL", e);
        }
    }

    private java.io.File getFile() {
        try {
            return java.io.File.createTempFile("telegram", ".ogg");
        } catch (IOException e) {
            log.error("Ошибка при создании временного файла", e);
            throw new RuntimeException("Ошибка при создании временного файла", e);
        }
    }
}