package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.urfu.service.UpdateProducer;
import ru.urfu.service.impl.ProducerService;
import ru.urfu.utils.MessageUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import static ru.urfu.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final ProducerService producerService;

    public UpdateController(MessageUtils messageUtils, ProducerService producerService) {
        this.messageUtils = messageUtils;
        this.producerService = producerService;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if (update == null){
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage()){
            distributeMessageByType(update);
        } else{
            log.error("Unsupported message type is received " + update);
        }
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();

        if (message.hasText()) {
            processTextMessage(update);
        } else{
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {

        sendMessage.setReplyMarkup(getChapterReplyMarkup());

        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void setView(HashMap<String, Object> docData) {
        Long chatId = ((Number) docData.get("chatId")).longValue();
        var docBytes = docData.get("docStream").toString().getBytes();
        byte[] decodedString = new byte[0];
        try {
            decodedString = Base64.getDecoder().decode(new String(docBytes).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        var docStream = new ByteArrayInputStream(decodedString);
        var sendDocument = createSendDocument(chatId, docStream);
        telegramBot.sendAnswerMessage(sendDocument);
    }

    private SendDocument createSendDocument(Long chatId, InputStream docStream) {
        var sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        var inputFile = new InputFile(docStream, "output.docx");
        sendDocument.setDocument(inputFile);

        sendDocument.setReplyMarkup(getChapterReplyMarkup());

        return sendDocument;
    }

    private void processTextMessage(Update update) {
        producerService.produceUpdate(update);
    }

    private ReplyKeyboardMarkup getChapterReplyMarkup(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("1. Описание бизнес-идеи");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("2. Описание продукта");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("3. Маркетинговый план");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("4. Производственный план");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("5. Человеческие ресурсы");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("6. Финансы и инвестиции");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("7. Оценка рисков");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("8. Перспективы");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
