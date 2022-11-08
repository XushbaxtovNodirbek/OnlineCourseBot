package com.example.OnlineCourse.bots;

import com.example.OnlineCourse.entity.User;
import com.example.OnlineCourse.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

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
            // Owner service
            if (user.getRole().equals("OWNER")){
                if (message.hasText()){
                    String text=message.getText();
                    switch (user.getStep()){
                        case "START"->{
                            switch (text){
                                case "/start"->{
                                    String message1="Assalomu alaykum "+message.getChat().getFirstName()+".\nAmallardan birini tanlang:";
                                    sendMessageWithInlineKeyboard(message1,
                                            inlineKeyboardMarkup("Adminlar ro'yxati","Admin qo'shish"),chatId);
                                }
                            }
                        }
                    }
                }
            }
            
        } else if (update.hasCallbackQuery()) {
            User user;
            Message message=update.getCallbackQuery().getMessage();
            Long chatId=message.getChatId();
            String data=update.getCallbackQuery().getData();
            System.out.println(chatId);
            if ((user=userService.findById(chatId))==(null)){
                user=userService.saveUser(message.getChat().getFirstName(),chatId);
            }
            // Owner Service
            if (user.getRole().equals("OWNER")){
                switch (user.getStep()){
                    case "START"->{
                        switch (data){
                            case "firsButton"->{
                                List<User> adminList=userService.getAllAdmin();
                                if (adminList.isEmpty()){
                                    sendMessageWithInlineKeyboard("Adminlar yoq!"
                                            ,inlineKeyboardMarkup("Admin qo'shish")
                                            ,chatId);
                                }else {
                                    for (User user1 : adminList) {
                                        sendMessageWithInlineKeyboard(user1.getName()
                                                ,inlineKeyboardMarkup("Adminlikdan chiqarish",user1.getChatId())
                                                ,chatId);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    //SendMessageWithInlineKeyboard
    public void sendMessageWithInlineKeyboard(String message, InlineKeyboardMarkup markup,Long chatId){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    // 2 InlineKeyboardButton
    public InlineKeyboardMarkup inlineKeyboardMarkup(String firstButton,String secondText){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard=new ArrayList<>();
        List<InlineKeyboardButton> row=new ArrayList<>();
        //First button
        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText(firstButton);
        button.setCallbackData("firsButton");
        row.add(button);
        //Second button
        button=new InlineKeyboardButton();
        button.setText(secondText);
        button.setCallbackData("secondButton");
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
    // 1 InlineKeyboard button
    public InlineKeyboardMarkup inlineKeyboardMarkup(String firstButton){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard=new ArrayList<>();
        List<InlineKeyboardButton> row=new ArrayList<>();
        //First button
        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText(firstButton);
        button.setCallbackData("secondButton");
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
    //for delete end save admins
    public InlineKeyboardMarkup inlineKeyboardMarkup(String firstButton,Long data){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard=new ArrayList<>();
        List<InlineKeyboardButton> row=new ArrayList<>();
        //First button
        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText(firstButton);
        button.setCallbackData(data.toString());
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

}
