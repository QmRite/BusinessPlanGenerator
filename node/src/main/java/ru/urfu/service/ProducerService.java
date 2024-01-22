package ru.urfu.service;

import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.InputStream;
import java.util.HashMap;

public interface ProducerService {
    void produceMessage(SendMessage sendMessage);
    void produceDoc(HashMap<String, Object> docData);
}
