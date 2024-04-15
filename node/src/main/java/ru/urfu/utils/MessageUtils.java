package ru.urfu.utils;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.urfu.dto.SendDocumentDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.urfu.model.RabbitQueue.ANSWER_DOC;

@Component
@Log4j
public class MessageUtils {
    public SendMessage createSendMessage(Long chatId, String output) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        return sendMessage;
    }

    public HashMap<String, Object> createSendDocumentMap(Long chatId, InputStream docStream, String caption) {
        var docInfo = new HashMap<String, Object>();
        docInfo.put("chatId", chatId);
        try {
            docInfo.put("docStream", docStream.readAllBytes());
        } catch (IOException e) {
            log.error("Не удалось перевести файл в массив байтов" + e);
        }

        return docInfo;
    }


    public SendMessage createSendMessage(Long chatId, String output, ArrayList<String> rowsString){
        var sendMessage = createSendMessage(chatId, output);
        sendMessage.setReplyMarkup(createReplyMarkup(rowsString));
        return sendMessage;
    }

    public ReplyKeyboardMarkup createReplyMarkup(ArrayList<String> rowsString){
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
