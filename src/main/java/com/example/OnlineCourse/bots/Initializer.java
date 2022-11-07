package com.example.OnlineCourse.bots;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class Initializer {
    final
    UserBot bot;
    final
    AdminBot adminBot;

    public Initializer(UserBot bot,AdminBot adminBot) {
        this.bot = bot;
        this.adminBot=adminBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init(){
        try {
            TelegramBotsApi botsApi=new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            botsApi.registerBot(adminBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

}
