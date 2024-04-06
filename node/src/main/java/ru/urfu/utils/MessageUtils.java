package ru.urfu.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageUtils {
    public SendMessage createSendMessage(Long chatId, String output) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        return sendMessage;
    }

    public SendMessage createSendMessage(Long chatId, String output, ArrayList<String> rowsString){
        var sendMessage = createSendMessage(chatId, output);
        sendMessage.setReplyMarkup(createReplyMarkup(rowsString));
        return sendMessage;
    }

    private ReplyKeyboardMarkup createReplyMarkup(ArrayList<String> rowsString){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (String s : rowsString) {
            KeyboardRow row = new KeyboardRow();
            row.add(s);
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
