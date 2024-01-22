package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.service.UpdateProducer;
import ru.urfu.utils.MessageUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;

import static ru.urfu.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
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
        return sendDocument;
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}
