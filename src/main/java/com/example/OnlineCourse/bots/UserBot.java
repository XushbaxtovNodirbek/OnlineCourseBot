package com.example.OnlineCourse.bots;

import com.example.OnlineCourse.config.BotConfig;
import com.example.OnlineCourse.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component

public class UserBot extends TelegramLongPollingBot {
    private final UserService userService;
    private final BotConfig config;

    public UserBot(UserService userService, BotConfig config) {
        this.userService = userService;
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        userService.saveUser(update.getMessage().getChat().getFirstName(),update.getMessage().getChatId());
    }
}
