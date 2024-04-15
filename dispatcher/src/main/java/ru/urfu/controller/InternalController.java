package ru.urfu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j
@RequestMapping("/dispatcher")
@RestController
public class InternalController {
    private final UpdateController updateController;

    public InternalController(UpdateController updateController) {
        this.updateController = updateController;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/message/")
    public void UserUpdate(@RequestBody SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/document/", consumes = {"*/*"})
    public void UserUpdate(@RequestHeader("Chat-Id") Long chatId,
                           InputStream docStream) {

        var rowsList = new ArrayList<KeyboardRow>();
        var row = new KeyboardRow();
        row.add("Главное меню");
        rowsList.add(row);

        updateController.setView(chatId, docStream, new ReplyKeyboardMarkup(rowsList));

    }
}
