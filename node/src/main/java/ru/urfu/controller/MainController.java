package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.entity.enums.PlanChapter;
import ru.urfu.service.api.ProduceService;
import ru.urfu.service.api.UserStateService;
import ru.urfu.service.impl.ApiService;
import ru.urfu.utils.MessageUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

@Log4j
@RequestMapping("/node")
@RestController
public class MainController {

    private final ProduceService produceService;
    private final UserStateService userStateService;

    public MainController(ProduceService produceService, UserStateService userStateService) {
        this.produceService = produceService;
        this.userStateService = userStateService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/UserUpdate/")
    public void UserUpdate(@RequestBody Update update) {
        userStateService.processTextMessage(update);
    }

    private void produceMessage(Long chatId, String input){
        var rows = new ArrayList<String>();
        rows.add("1");
        rows.add("2");
        rows.add("3");
        produceService.produceMessage(chatId, input, rows);
    }


}
