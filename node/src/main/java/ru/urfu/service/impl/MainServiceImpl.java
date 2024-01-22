package ru.urfu.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.dao.AppUserDAO;
import ru.urfu.dao.RawDataDAO;
import ru.urfu.entity.AppUser;
import ru.urfu.entity.RawData;
import ru.urfu.service.MainService;
import ru.urfu.service.ProducerService;
import ru.urfu.service.enums.PlanChapter;
import ru.urfu.service.enums.ServiceCommand;
import ru.urfu.utils.DocUtils;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static ru.urfu.entity.enums.UserState.BASIC_STATE;
import static ru.urfu.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.urfu.service.enums.ServiceCommand.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final DocUtils docUtils;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, DocUtils docUtils, AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.docUtils = docUtils;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var output = "";


        var serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)){
            output = processServiceCommand(chatId, appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)){
            //TODO добавить обработку мейла
        } else{
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова.";
        }

        sendAnswer(output, chatId);

    }

    private void sendDoc(Long chatId, InputStream docStream, String caption) {
        var docInfo = new HashMap<String, Object>();
        docInfo.put("chatId", chatId);
        try {
            docInfo.put("docStream", docStream.readAllBytes());
        } catch (IOException e) {
            log.error("Не удалось перевести файл в массив байтов" + e);
        }
/*        byte[] docData = new byte[0];
        try {
            docData = docStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        producerService.produceDoc(docInfo);
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceMessage(sendMessage);
    }

    private String processServiceCommand(Long chatId, AppUser appUser, String cmd) {
        var serviceCommand = ServiceCommand.fromValue(cmd);

        if (REGISTRATION.equals(serviceCommand)) {
            //TODO добавить регистрацию
            return "Временно недоступно.";
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (START.equals(serviceCommand)) {
            //return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
            return start(chatId);
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String start(Long chatId) {
        InputStream docStream;
        try {
            docStream =  docUtils.getPlanChapterInputStream(PlanChapter.RESUME);
        } catch (IOException e) {
            log.error("Ошибка получения документа " + e);
            return "Ошибка. Попробуйте снова";
        }
        sendDoc(chatId, docStream, "Caption");
        return "стартрт";
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);

        return "Команда отменена.";
    }

    private AppUser findOrSaveAppUser(Update update){
        var telegramUser = update.getMessage().getFrom();
        var persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null){
            var transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }

        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        var rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
