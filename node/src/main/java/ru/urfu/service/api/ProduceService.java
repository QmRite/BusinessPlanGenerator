package ru.urfu.service.api;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.utils.MessageUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

@Service
public class ProduceService {
    private final MessageUtils messageUtils;

    public ProduceService(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public void produceMessage(Long chatId, String text) {
        var sendMessage = messageUtils.createSendMessage(chatId, text);

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8084/dispatcher/message/");

        httpPost.addHeader("content-type", "application/json");
        JSONObject sendMessageJson = new JSONObject(sendMessage);
        var sendMessageString = sendMessageJson.toString();
        sendMessageString = sendMessageString.replace("chatId", "chat_id");
        var params = new StringEntity(sendMessageString, "UTF-8");
        httpPost.setEntity(params);

        try {
            httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void produceMessage(Long chatId, String text, ArrayList<String> rowsString) {
        var sendMessage = messageUtils.createSendMessage(chatId, text, rowsString);

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8084/dispatcher/message/");

        httpPost.addHeader("content-type", "application/json");
        JSONObject sendMessageJson = new JSONObject(sendMessage);
        var sendMessageString = sendMessageJson.toString();
        sendMessageString = sendMessageString.replace("chatId", "chat_id");

        sendMessageString = sendMessageString.replace("replyMarkup", "reply_markup");

        var params = new StringEntity(sendMessageString, "UTF-8");
        httpPost.setEntity(params);

        try {
            httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
