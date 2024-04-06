package ru.urfu.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.dao.AnswerDAO;
import ru.urfu.dao.AppUserDAO;
import ru.urfu.dao.RawDataDAO;
import ru.urfu.entity.Answer;
import ru.urfu.entity.AppUser;
import ru.urfu.entity.RawData;
import ru.urfu.entity.enums.UserState;
import ru.urfu.service.MainService;
import ru.urfu.service.ProducerService;
import ru.urfu.service.convertors.PlanChapterConvertor;
import ru.urfu.entity.enums.PlanChapter;
import ru.urfu.service.enums.ServiceCommand;
import ru.urfu.utils.DocUtils;
import ru.urfu.utils.RequestTextGenerators.AbstractRequestTextGenerator;
import ru.urfu.utils.RequestTextGenerators.Factory.RequestTextFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static ru.urfu.entity.enums.UserState.*;
import static ru.urfu.service.enums.ServiceCommand.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final DocUtils docUtils;
    private final AppUserDAO appUserDAO;

    private final AnswerDAO answerDAO;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, DocUtils docUtils, AppUserDAO appUserDAO, AnswerDAO answerDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.docUtils = docUtils;
        this.appUserDAO = appUserDAO;
        this.answerDAO = answerDAO;
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
        } else if (MAIN_MENU_STATE.equals(userState)){
            output = processServiceCommand(chatId, appUser, text);
        } else if (CHAPTER_SELECTION_STATE.equals(userState)){
            output = startDialog(chatId, text, appUser);
        } else if (DIALOG_STATE.equals(userState)){
            output = startDialog(chatId, text, appUser);
        }else{
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
            appUser.setState(UserState.CHAPTER_SELECTION_STATE);
            appUserDAO.saveAndFlush(appUser);
            return "Выберете главу бизнес-плана";
        } else {

            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String startDialog(Long chatId, String userAnswer, AppUser appUser) {
        appUser.setState(DIALOG_STATE);

        var currentStatePosition = appUser.getUserStatePosition();

        PlanChapter planChapter;
        if (currentStatePosition == 0){
            planChapter = PlanChapterConvertor.PlanChapterByText.get(userAnswer);
            appUser.setPlanChapter(planChapter);
        }

        planChapter = appUser.getPlanChapter();
        var requestTextGenerator = RequestTextFactory.getRequestText(planChapter.toString());

        if (currentStatePosition == 1) {
            var answers = new ArrayList<Answer>();
            appUser.setAnswers(answers);
        }

        var userAnswers = appUser.getAnswers();
        if (currentStatePosition > 0){
            var answer = new Answer();
            answer.setUser(appUser);
            answer.setAnswer(userAnswer);
            userAnswers.add(answer);
            appUser.setAnswers(userAnswers);
            answerDAO.saveAndFlush(answer);
        }

        appUser.setUserStatePosition(currentStatePosition + 1);
        appUserDAO.saveAndFlush(appUser);

        var questionsCount = requestTextGenerator.getQuestions().size();
        if (!(currentStatePosition > questionsCount)){
            return requestTextGenerator.getQuestions().get(currentStatePosition);
        }

        appUser.setState(WAITING_FOR_DOCUMENT_STATE);
        appUserDAO.saveAndFlush(appUser);

        var answers = userAnswers.stream().map(Answer::getAnswer).toArray(String[]::new);

        return createPlanChapter(chatId, planChapter, answers, requestTextGenerator, appUser);
    }

    private String createPlanChapter(Long chatId, PlanChapter planChapter, String[] answers,
                                     AbstractRequestTextGenerator requestTextGenerator, AppUser appUser) {
        var requestText = requestTextGenerator.getRequestText(answers);

        InputStream docStream;
        try {
            docStream =  docUtils.getPlanChapterInputStream(planChapter, requestText);
        } catch (IOException e) {
            log.error("Ошибка получения документа " + e);

            appUser.setState(MAIN_MENU_STATE);
            appUser.getAnswers().clear();
            appUserDAO.saveAndFlush(appUser);

            return "Ошибка. Попробуйте снова";
        }
        sendDoc(chatId, docStream, "Caption");

        appUser.setState(MAIN_MENU_STATE);
        appUser.getAnswers().clear();
        appUserDAO.saveAndFlush(appUser);

        return "Файл успешно создан";
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(MAIN_MENU_STATE);
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
                    .state(MAIN_MENU_STATE)
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
