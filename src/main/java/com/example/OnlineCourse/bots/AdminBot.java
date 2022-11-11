package com.example.OnlineCourse.bots;

import com.example.OnlineCourse.entity.AdminTmp;
import com.example.OnlineCourse.entity.Courses;
import com.example.OnlineCourse.entity.User;
import com.example.OnlineCourse.service.impl.AdminTmpServiceImp;
import com.example.OnlineCourse.service.impl.CourseServiceImpl;
import com.example.OnlineCourse.service.impl.UserServiceImpl;
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

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminBot extends TelegramLongPollingBot {
    @Value("${spring.owner.chatId}")
    private Long ownerId;
    private final UserServiceImpl userService;
    private final AdminTmpServiceImp adminTmpService;
    private final CourseServiceImpl courseService;

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
                                if (!checkAdmin(adminTmpService.getAll(),adminTmp)) {
                                    String txt = "Arizangiz moderatorga yuborildi.Arizangiz qabul qilinishi bilan sizga habar beramiz;)";
                                    sendMessage(txt, chatId);
                                    adminTmpService.saveAdminTmp(adminTmp.getName(), adminTmp.getUserName(), chatId);
                                    txt = "Yangi ariza!\nAdmin ismi : " + user.getName() + "\nAdmin foydalanuvchi nomi : @" + message.getChat().getUserName();
                                    sendMessageWithInlineKeyboard(txt, sendOwner(chatId), ownerId);
                                }else {
                                    sendMessage("Siz ariza yuborgansiz iltimos javobni kuting",chatId);
                                }
                        }
                        default -> {
                            sendMessage("Iltimos berilgan tugmalar yoki belgilardan foydalaning",chatId);
                        }
                    }
                }
            }
            //Admin service
            else {
                if (message.hasText()){
                    String text= message.getText();
                    if (text.equals("/start")){
                        String txt="\nAssalomu alaykum "+user.getName()+".\nAmallardan birini tanlang.";
                        sendMessageWithInlineKeyboard(txt,inlineKeyboardMarkup("Video dars joylash","E'lon berish"),chatId);
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
                                        sendMessageWithInlineKeyboard(user1.getName()
                                                , inlineKeyboardMarkup("O'chirish", user1.getChatId())
                                                , chatId);
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
                        }
                    }
                }
            }
            //Admin service
            else {
                switch (user.getStep()){
                    case "START"->{
                        switch (data){
                            case "firstButton"->{
                                if (courseService.getAllCourse().isEmpty()){
                                    sendMessageWithInlineKeyboard("Hozirda kurslar mavjud emas.",
                                            inlineKeyboardMarkup("Kurs qo'shish"),chatId);
                                    userService.changeStep(chatId,"ADD_COURSE");
                                }else {
                                    for (Courses courses : courseService.getAllCourse()) {
                                        sendMessageWithInlineKeyboard("Kurslar ro'yxati",
                                                sendListCourse(courseService.getAllCourse()),
                                                chatId);
                                    }
                                }
                            }
                            case "secondButton"->{

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
    public InlineKeyboardMarkup sendListCourse(List<Courses> courses)
    {
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row;
        InlineKeyboardButton button;
        //buttons
        for (Courses cours : courses) {
            row = new ArrayList<>();
            button=new InlineKeyboardButton();
            button.setCallbackData(cours.getId().toString());
            button.setText(cours.getName());
            row.add(button);
            keyboard.add(row);
        }
        row=new ArrayList<>();
        button=new InlineKeyboardButton();
        button.setText("Orqaga");
        button.setText("b");
        row.add(button);
        keyboard.add(row);

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
    public void sendMessage(String text,Long chatId){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
