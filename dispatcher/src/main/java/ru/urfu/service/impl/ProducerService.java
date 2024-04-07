package ru.urfu.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

//Отправляет текст пользователя в Node
@Service
@Log4j
public class ProducerService {
    //TODO добавить реакцию на ошибки
    public void produceUpdate(Update update){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8085/node/UserUpdate/");

        httpPost.addHeader("content-type", "application/json");
        JSONObject updateJson = new JSONObject(update);
        StringEntity params = null;
        params = new StringEntity(updateJson.toString(), "UTF-8");
        httpPost.setEntity(params);

        try {
            var response = httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
    }
}
