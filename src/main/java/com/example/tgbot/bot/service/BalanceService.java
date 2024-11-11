package com.example.tgbot.bot.service;

import com.example.tgbot.bot.model.Account;
import com.example.tgbot.bot.model.MessageTypes;
import com.example.tgbot.bot.model.Subscribe;
import com.example.tgbot.bot.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.example.tgbot.bot.model.MessageTypes.*;

@Service
@AllArgsConstructor
public class BalanceService {

    private static final Double SUPPORT_RATIO = 0.18;

    private final AccountRepository accountRepository;
    private final SubscribeService subscribeService;

    public void increaseBalance(Long userId, Double sum) {
        if (userId == null || sum == null) {
            throw new IllegalArgumentException("Передано не корректное имя пользователя или сумма для начисления");
        }

        Account account = accountRepository.findByUserId(userId);
        if (account == null) {
            throw new IllegalArgumentException("Аккаунт с таким id не найден. Необходимо пройти регистрацию или " +
                    "проверить id пользователя");
        }
        Double currentBalance = account.getBalance();
        if (currentBalance == 0D) {
            Subscribe subscribe = account.getSubscribe();
            subscribe.setActive(true);
            subscribeService.save(subscribe);
        }
        account.setBalance(currentBalance + sum);
        accountRepository.save(account);
    }

    public void decreaseBalanceAccount(MessageTypes type, Message message, String gptGeneratedText) {
        double cost = calculationCost(type, message, gptGeneratedText);
        decreaseBalance(message.getFrom().getId(), cost);
    }

    private void decreaseBalance(Long userId, Double sum) {
        if (userId == null || sum == null) {
            throw new IllegalArgumentException("Передано не корректное имя пользователя или сумма для списания");
        }
        if (sum == 0) {
            return;
        }

        Account account = accountRepository.findByUserId(userId);
        if (account == null) {
            throw new IllegalArgumentException("Аккаунт с таким id не найден. Необходимо пройти регистрацию или " +
                    "проверить id пользователя");
        }

        double newBalanceAccount = Double.max(account.getBalance() - sum, 0D);
        if (newBalanceAccount == 0) {
            Subscribe subscribe = account.getSubscribe();
            subscribe.setActive(false);
            subscribeService.save(subscribe);
        }

        account.setBalance(newBalanceAccount);
        accountRepository.save(account);
    }

    private double calculationCost(MessageTypes type, Message inputMessage, String outputMessage) {
        double requestCost, responseCost;

        switch (type) {
            case TEXT -> {
                requestCost = getCountTokens(inputMessage.getText()) * TEXT.getRequestPrice();
                responseCost = getCountTokens(outputMessage) * TEXT.getResponsePrice();
            }
            case AUDIO -> {
                requestCost = inputMessage.getVoice().getDuration() * AUDIO.getRequestPrice();
                responseCost = getCountTokens(outputMessage) * AUDIO.getResponsePrice();
            }
            case AUDIO_TRANSCRIBE -> {
                requestCost = inputMessage.getVoice().getDuration() * AUDIO_TRANSCRIBE.getRequestPrice();
                responseCost = getCountTokens(outputMessage) * AUDIO_TRANSCRIBE.getResponsePrice();
            }
            default -> throw new IllegalArgumentException("Передан не поддерживаемый тип сообщения");
        }

        return (requestCost + responseCost) * SUPPORT_RATIO + (requestCost + responseCost);
    }

    private int getCountTokens(String text) {
        String[] tokens = text.split("\\s+|\\p{Punct}"); // Разделяем по пробелам и знакам препинания
        return tokens.length;
    }

}
