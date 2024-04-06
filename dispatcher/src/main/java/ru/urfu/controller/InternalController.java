package ru.urfu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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

    @RequestMapping(method = RequestMethod.POST, value = "/document/")
    public void UserUpdate(@RequestBody SendDocument sendDocument) {

    }
}
