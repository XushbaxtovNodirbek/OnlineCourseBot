package com.example.OnlineCourse.bots;

import com.example.OnlineCourse.entity.User;
import com.example.OnlineCourse.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class AdminBot extends TelegramLongPollingBot {
    private final UserServiceImpl userService;

    @Override
    public String getBotUsername() {
        return "course_admin_xn_bot";
    }

    @Override
    public String getBotToken() {
        return "5794147566:AAGqUS0at3aZ7YKNGStvYPmeezaAOQtuTBI";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()){
            User user;
            Message message=update.getMessage();
            Long chatId=message.getChatId();
            System.out.println(chatId);
            if ((user=userService.findById(chatId))==(null)){
                user=userService.saveUser(message.getChat().getFirstName(),chatId);
            }
            if (user.getRole().equals("OWENER")){
                if (message.hasText()){
                    String text=message.getText();
                    switch (user.getStep()){
                        case "START"->{

                        }
                    }
                }
            }
            
        }

    }

}
