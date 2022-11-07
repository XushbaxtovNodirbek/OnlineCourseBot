package com.example.OnlineCourse.bots;

import com.example.OnlineCourse.entity.Admins;
import com.example.OnlineCourse.service.AdminService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.checkerframework.checker.units.qual.Current;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminBot extends TelegramLongPollingBot {

    private final AdminService adminService;

    private Admins admin;

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
            Message message=update.getMessage();
            Long chatId=message.getChatId();
            admin=adminService.findByChatId(chatId);
            if(admin==null){
                adminService.saveAdmin(chatId,message.getChat().getFirstName(),"false");
                admin=adminService.findByChatId(chatId);
            }
            if (message.hasText()){
                String text=message.getText();
                if (text.equals("/start")  ){
                    if(!checkTime(admin.getUpdateAt().getTime(),new Date().getTime()) || admin.getIsActive().equals("false")){
                    sendText("Home",chatId);
                    }else {
                        sendText("Parolni Kiriting",chatId);
                        adminService.changeStep(chatId,"CHECK");
                    }
                }
                switch (admin.getStep()){
                    case "CHECK"->{
                        if (text.equals("0000")){
                            sendText("Xush Kelibsiz",chatId);
                            adminService.changeStep(chatId,"HOME");
                        }
                    }
                }
            }


        }


    }
    private boolean checkTime(Long updateAt,Long currentTime){
        return currentTime-updateAt<86400L;
    }
    private void sendMessageWithInlineKeyboard(String text,InlineKeyboardMarkup markup,Long chatId){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(markup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    private void sendText(String text,Long chatId){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }




}
