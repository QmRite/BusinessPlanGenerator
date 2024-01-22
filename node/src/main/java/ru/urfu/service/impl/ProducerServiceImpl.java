package ru.urfu.service.impl;

import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.service.ProducerService;

import java.io.InputStream;
import java.util.HashMap;

import static ru.urfu.model.RabbitQueue.ANSWER_DOC;
import static ru.urfu.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produceMessage(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void produceDoc(HashMap<String, Object> docData) {
        rabbitTemplate.convertAndSend(ANSWER_DOC, docData);
    }
}
