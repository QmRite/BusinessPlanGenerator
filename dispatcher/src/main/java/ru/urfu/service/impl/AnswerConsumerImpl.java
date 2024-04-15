package ru.urfu.service.impl;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.urfu.controller.UpdateController;
import ru.urfu.service.AnswerConsumer;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.HashMap;

import static ru.urfu.model.RabbitQueue.ANSWER_DOC;
import static ru.urfu.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumeMessage(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = ANSWER_DOC)
    public void consumeDocument(HashMap<String, Object> docData) {
        updateController.setView(docData);
    }
}
