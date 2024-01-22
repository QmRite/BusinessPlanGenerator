package ru.urfu.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.InputStream;
import java.util.HashMap;

import static ru.urfu.model.RabbitQueue.ANSWER_DOC;

public interface AnswerConsumer {
    void consumeMessage(SendMessage sendMessage);

    void consumeDocument(HashMap<String, Object> docData);
}
