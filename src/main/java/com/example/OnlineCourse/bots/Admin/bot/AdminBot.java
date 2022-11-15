package com.example.OnlineCourse.bots.Admin.bot;

import com.example.OnlineCourse.entity.*;
import com.example.OnlineCourse.service.impl.AdminTmpServiceImp;
import com.example.OnlineCourse.service.impl.CourseServiceImpl;
import com.example.OnlineCourse.service.impl.UserServiceImpl;
import com.example.OnlineCourse.service.impl.VideoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.Cache;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminBot extends TelegramLongPollingBot {
    @Value("${spring.owner.chatId}")
    private Long ownerId;

    @Value("${spring.adminBot.username}")
    private String adminBotUsername;

    @Value("${spring.adminBot.token}")
    private String token;

    private final UserServiceImpl userService;
    private final VideoServiceImpl videoService;
    private final AdminTmpServiceImp adminTmpService;
    private final CourseServiceImpl courseService;

    @Override
    public String getBotUsername() {
        return adminBotUsername;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            User user;
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            System.out.println(chatId);
            if ((user = userService.findById(chatId)) == (null)) {
                user = userService.saveUser(message.getChat().getFirstName(), chatId);
            }
            // Owner service
            if (user.getRole().equals("OWNER")) {
                if (message.hasText()) {
                    String text = message.getText();
                    switch (user.getStep()) {
                        case "START" -> {
                            switch (text) {
                                case "/start" -> {
                                    deleteMessage(chatId, message.getMessageId() - 1);
                                    String message1 = "Assalomu alaykum " + message.getChat().getFirstName() + ".\nAmallardan birini tanlang:";
                                    sendMessageWithInlineKeyboard(message1,
                                            inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);
                                }
                                default -> {
                                    SendMessage sendMessage = new SendMessage();
                                    sendMessage.setText("Iltimos tugmalardan foydalaning");
                                    sendMessage.setChatId(chatId);

                                    try {
                                        execute(sendMessage);
                                    } catch (TelegramApiException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            }
                        }
                        default -> {
                            if (text.equals("/start")) {
                                deleteMessage(chatId, message.getMessageId() - 1);
                                sendMessageWithInlineKeyboard("Bosh sahifa",
                                        inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"),
                                        chatId);
                                userService.changeStep(chatId, "START");
                            }
                        }
                    }
                }
            } else if (user.getRole().equals("USER")) {
                if (message.hasText()) {
                    String text = message.getText();
                    switch (text) {
                        case "/start" -> {
                            String txt = "Kechirasiz bu bot faqat adminlar uchun.Agar admin bolmasangiz iltimos botni tark eting." +
                                    "Agar yangi admin bolsangiz yuborish tugmasini bosing";
                            sendMessageWithReplyKeyboard(txt, markup(), chatId);
                        }
                        case "Yuborish" -> {
                            AdminTmp adminTmp = new AdminTmp(user.getName(),
                                    message.getChat().getUserName(),
                                    user.getChatId());
                            if (!checkAdmin(adminTmpService.getAll(), adminTmp)) {
                                String txt = "Arizangiz moderatorga yuborildi.Arizangiz " +
                                        "qabul qilinishi bilan sizga habar beramiz;)";
                                sendMessage(txt, chatId);
                                adminTmpService.saveAdminTmp(adminTmp.getName(), adminTmp.getUserName(), chatId);
                                txt = "Yangi ariza!\nAdmin ismi : " + user.getName() + "\nAdmin foydalanuvchi nomi : @" + message.getChat().getUserName();
                                sendMessageWithInlineKeyboard(txt, sendOwner(chatId), ownerId);
                            } else {
                                sendMessage("Siz ariza yuborgansiz iltimos javobni kuting", chatId);
                            }
                        }
                        default -> {
                            sendMessage("Iltimos berilgan tugmalar yoki belgilardan foydalaning", chatId);
                        }
                    }
                }
            }
            //Admin service
            else {
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start")) {
                        String txt = "\nAssalomu alaykum " + user.getName() + ".\nAmallardan birini tanlang.";
                        sendMessageWithInlineKeyboard(txt, inlineKeyboardMarkup("Video dars joylash", "E'lon berish"), chatId);
                    }
                }
                else if(message.hasVideo()){
                    switch (user.getStep()){
                        case "UPLOAD_VIDEO"->{
//                            String
                        }
                    }
                }
            }

        } else if (update.hasCallbackQuery()) {
            User user;
            Message message = update.getCallbackQuery().getMessage();
            Long chatId = message.getChatId();
            String data = update.getCallbackQuery().getData();
            System.out.println(chatId);
//            System.out.println(update.getCallbackQuery().getMessage());
            if ((user = userService.findById(chatId)) == (null)) {
                user = userService.saveUser(message.getChat().getFirstName(), chatId);
            }
            // Owner Service
            if (user.getRole().equals("OWNER")) {
                switch (user.getStep()) {
                    case "START" -> {
                        switch (data) {
                            case "firstButton" -> {
                                List<User> adminList = userService.getAllAdmin();
                                if (adminList.isEmpty()) {
                                    deleteMessage(chatId, message.getMessageId());
                                    sendMessageWithInlineKeyboard("Adminlar yoq",
                                            inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);
                                } else {
                                    deleteMessage(chatId, message.getMessageId());
                                    for (User user1 : adminList) {
                                        sendMessageWithInlineKeyboard(user1.getName(), inlineKeyboardMarkup("O'chirish",
                                                user1.getChatId()), chatId);
                                    }
                                    userService.changeStep(chatId, "EDIT_ADMIN");
                                }
                            }
                            case "secondButton" -> {
                                List<AdminTmp> adminList = adminTmpService.getAll();
                                if (adminList.isEmpty()) {
                                    deleteMessage(chatId, message.getMessageId());
                                    sendMessageWithInlineKeyboard("Arizalar yoq",
                                            inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);
                                } else {
                                    deleteMessage(chatId, message.getMessageId());
                                    for (AdminTmp adminTmp : adminList) {
                                        String txt = "Ismi : " + adminTmp.getName() + "\nFoydalanuvchi nomi : @" + adminTmp.getUserName();
                                        sendMessageWithInlineKeyboard(txt, inlineKeyboardMarkup(adminTmp.getChatId()), chatId);
                                    }
                                    userService.changeStep(chatId, "EDIT_ADMIN_TMP");
                                }
                            }
                            default -> {
                                switch (data.substring(0, 1)) {
                                    case "+" -> {
                                        DeleteMessage deleteMessage = new DeleteMessage();
                                        deleteMessage.setChatId(chatId);
                                        deleteMessage.setMessageId(message.getMessageId());

                                        try {
                                            execute(deleteMessage);
                                        } catch (TelegramApiException e) {
                                            throw new RuntimeException(e);
                                        }
                                        sendMessageWithInlineKeyboard(adminTmpService.findByChatId(Long.parseLong(data.substring(1))).getName() + " adminlarga Qoshildi",
                                                inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);
                                        userService.changeStep(chatId, "START");
                                        userService.changeRole(Long.parseLong(data.substring(1)), "ADMIN");
                                        adminTmpService.deleteTmp(Long.parseLong(data.substring(1)));
                                    }
                                    case "-" -> {
                                        deleteMessage(chatId, message.getMessageId());
                                        sendMessageWithInlineKeyboard("O'chirildi",
                                                inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);
                                        userService.changeStep(chatId, "START");
                                        adminTmpService.deleteTmp(Long.parseLong(data.substring(1)));
                                    }
                                }
                            }
                        }
                    }
                    case "EDIT_ADMIN" -> {
                        switch (data.substring(0, 1)) {
                            case "-" -> {
                                deleteMessage(chatId, message.getMessageId());
                                System.out.println(data.substring(1));
                                userService.changeRole(Long.parseLong(data.substring(1)), "USER");
                                sendMessageWithInlineKeyboard("Admin o'chirildi",
                                        inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);

                                userService.changeStep(chatId, "START");

                            }
                            case "b" -> {
                                deleteMessage(chatId, message.getMessageId());
                                sendMessageWithInlineKeyboard("Amalni tanlang",
                                        inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);
                                userService.changeStep(chatId, "START");
                            }
                        }
                    }
                    case "EDIT_ADMIN_TMP" -> {
                        switch (data.substring(0, 1)) {
                            case "+" -> {
                                deleteMessage(chatId, message.getMessageId());
                                sendMessageWithInlineKeyboard(adminTmpService
                                                .findByChatId(Long.parseLong(data.substring(1))).getName() + " adminlarga Qoshildi",
                                        inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);
                                userService.changeStep(chatId, "START");
                                userService.changeRole(Long.parseLong(data.substring(1)), "ADMIN");
                                adminTmpService.deleteTmp(Long.parseLong(data.substring(1)));
                            }
                            case "-" -> {
                                deleteMessage(chatId, message.getMessageId());
                                sendMessageWithInlineKeyboard(adminTmpService
                                                .findByChatId(Long.parseLong(data.substring(1))).getName() + "O'chirildi",
                                        inlineKeyboardMarkup("Adminlar ro'yxati", "Admin qo'shish"), chatId);
                                adminTmpService.deleteTmp(Long.parseLong(data.substring(1)));
                                userService.changeStep(chatId, "START");
                            }
                        }
                    }
                }
            }
            //Admin service
            else if (user.getRole().equals("ADMIN")) {
                System.out.println(data);
                switch (user.getStep()) {
                    case "START" -> {
                        switch (data) {
                            case "firstButton" -> {
                                if (courseService.getAllCourse().isEmpty()) {
                                    sendMessageWithInlineKeyboard("Hozirda kurslar mavjud emas.",
                                            inlineKeyboardMarkup("Kurs qo'shish"), chatId);
                                    userService.changeStep(chatId, "VIEW_COURSE");
                                } else {
                                    List<Courses> list = courseService.getAllCourse();
                                    System.out.println(list);
                                    sendMessageWithInlineKeyboard("Kurslar", sendListCourse(list), chatId);
                                    userService.changeStep(chatId,"VIEW_COURSE");
                                }
                            }
                            case "secondButton" -> {

                            }
                        }
                    }
                    case "VIEW_COURSE"->{
                        switch (data){
                            case "+"->{
                                //add course
                            }
                            case "b"->{
                                String txt =user.getName() + ".\nAmallardan birini tanlang.";
                                sendMessageWithInlineKeyboard(txt, inlineKeyboardMarkup("Video dars joylash", "E'lon berish"), chatId);
                                userService.changeStep(chatId,"START");
                            }
                            default -> {
                                Courses course=courseService.findById(Long.parseLong(data));
                                if (course!=null){
                                    String txt="Kurs nomi: "+course.getName()+
                                            "\nQo'shimcha ma'lumot: "+course.getDescription();
                                    sendMessageWithInlineKeyboard(txt,CRUD_Course("Kursni o'chirish","Video Yukash",data),chatId);
                                    userService.changeStep(chatId,"EDIT_COURSE");
                                }
                            }
                        }
                    }
                    case "EDIT_COURSE"->{
                        switch (data.substring(0,1)){
                            case "b"->{
                                List<Courses> list = courseService.getAllCourse();
                                System.out.println(list);
                                sendMessageWithInlineKeyboard("Kurslar", sendListCourse(list), chatId);
                                userService.changeStep(chatId,"VIEW_COURSE");
                            }
                            case "d"->{
                                Courses course=courseService.findById(Long.parseLong(data.substring(1)));
                                for (Lessons lessons : course.getLessonList()) {
                                    Videos video = lessons.getVideo();
                                    videoService.delete(video);
                                }
                                courseService.delete(course.getId());
                                List<Courses> list = courseService.getAllCourse();
                                System.out.println(list);
                                sendMessageWithInlineKeyboard("Kurs o'chirildi", sendListCourse(list), chatId);
                                userService.changeStep(chatId,"VIEW_COURSE");
                            }
                            case "u"->{
                                sendMessage("Marhamat yangi video yuklashingiz mumkin iltimos video tagida izoh " +
                                        "yozish yodingizdan chiqmasin",chatId,data);
                                userService.changeStep(chatId,"UPLOAD_VIDEO");
                            }
                        }
                    }

                }
            }

        }

    }

    //SendMessageWithInlineKeyboard
    public void sendMessageWithInlineKeyboard(String message, InlineKeyboardMarkup markup, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    //send message with Reply keyboard button
    public void sendMessageWithReplyKeyboard(String message, ReplyKeyboardMarkup markup, Long chatId) {
        SendMessage sendMessage = new SendMessage();
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
    public InlineKeyboardMarkup inlineKeyboardMarkup(String firstButton, String secondText) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        //First button
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(firstButton);
        button.setCallbackData("firstButton");
        row.add(button);
        //Second button
        button = new InlineKeyboardButton();
        button.setText(secondText);
        button.setCallbackData("secondButton");
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    // 1 InlineKeyboard button
    public InlineKeyboardMarkup inlineKeyboardMarkup(String firstButton) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        //First button
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(firstButton);
        button.setCallbackData("+");
        row.add(button);
        keyboard.add(row);
        //second button
        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText("Orqaga");
        button.setCallbackData("b");
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    //send list courses
    public InlineKeyboardMarkup sendListCourse(List<Courses> courses) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            InlineKeyboardButton button=new InlineKeyboardButton();
            button.setText(courses.get(i).getName());
            button.setCallbackData(courses.get(i).getId().toString());
            row.add(button);
            if ((i+1)%2==0){
                keyboard.add(row);
                row=new ArrayList<>();
            }
        }
        row=new ArrayList<>();
        if (courses.size()%2==1){
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData(courses.get(courses.size()-1).getId().toString());
            button.setText(courses.get(courses.size()-1).getName());
            row.add(button);

            button = new InlineKeyboardButton();
            button.setCallbackData("b");
            button.setText("Orqaga");
            row.add(button);
            keyboard.add(row);

            row = new ArrayList<>();
            button = new InlineKeyboardButton();
            button.setCallbackData("+");
            button.setText("Kurs qo'shish");
            row.add(button);
            keyboard.add(row);


        }else {
            row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("b");
            button.setText("Orqaga");
            row.add(button);

            button = new InlineKeyboardButton();
            button.setCallbackData("+");
            button.setText("Kurs qo'shish");
            row.add(button);
            keyboard.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    //for delete end save admins
    public InlineKeyboardMarkup inlineKeyboardMarkup(String firstButton, Long data) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        //First button
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(firstButton);
        button.setCallbackData("-" + data);
        row.add(button);
        keyboard.add(row);
        //second button
        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText("Orqaga");
        button.setCallbackData("b");
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup inlineKeyboardMarkup(Long data) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        //First button
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Qo'shish");
        button.setCallbackData("+" + data);
        row.add(button);
        // second button
        button = new InlineKeyboardButton();
        button.setText("O'chirish");
        button.setCallbackData("-" + data);
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    // send user Reply markup
    public ReplyKeyboardMarkup markup() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("Yuborish");
        keyboard.add(keyboardRow);
        markup.setKeyboard(keyboard);
        return markup;
    }

    public InlineKeyboardMarkup sendOwner(Long chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Qo'shish");
        button.setCallbackData("+" + chatId);
        row.add(button);
        button = new InlineKeyboardButton();
        button.setText("O'chirish");
        button.setCallbackData("-" + chatId);
        row.add(button);
        keyboard.add(row);
        markup.setKeyboard(keyboard);
        return markup;
    }

    //check is contain
    public boolean checkAdmin(List<AdminTmp> adminTmpList, AdminTmp adminTmp) {
        for (AdminTmp tmp : adminTmpList) {
            if (tmp.getChatId().equals(adminTmp.getChatId())) {
                return true;
            }
        }
        return false;
    }

    //delete messeges
    public void deleteMessage(Long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    //send only text
    public void sendMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    //WITH BACK BUTTON
    public InlineKeyboardMarkup CRUD_Course(String firstButton, String secondText,String data) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        //First button
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(firstButton);
        button.setCallbackData("d"+data);
        row.add(button);
        //Second button
        button = new InlineKeyboardButton();
        button.setText(secondText);
        button.setCallbackData("u"+data);
        row.add(button);
        keyboard.add(row);
        //back button
        row=new ArrayList<>();
        button=new InlineKeyboardButton();
        button.setCallbackData("b");
        button.setText("Orqaga");
        row.add(button);
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
    //for upload video
    public String sendMessage(String text, Long chatId,String data) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return data;
    }


}
