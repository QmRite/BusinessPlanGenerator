package ru.urfu.service.api;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.urfu.utils.MessageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import static ru.urfu.model.RabbitQueue.ANSWER_DOC;

@Service
@Log4j
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

    public void produceDocument(Long chatId, InputStream docStream, String filename) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8084/dispatcher/document/");

        InputStreamEntity fileEntity = new InputStreamEntity(docStream);
        fileEntity.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        httpPost.addHeader("Content-disposition", "attachment; filename=" + filename);
        httpPost.addHeader("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        httpPost.addHeader("Chat-Id", chatId.toString());
        httpPost.setEntity(fileEntity);

        HttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            var a = 2;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
