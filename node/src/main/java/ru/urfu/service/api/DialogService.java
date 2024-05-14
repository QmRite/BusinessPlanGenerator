package ru.urfu.service.api;

import lombok.extern.log4j.Log4j;
import org.apache.poi.util.IOUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.urfu.dao.AnswerDAO;
import ru.urfu.dao.AppDocumentDAO;
import ru.urfu.dao.AppUserDAO;
import ru.urfu.entity.Answer;
import ru.urfu.entity.AppDocument;
import ru.urfu.entity.AppUser;
import ru.urfu.entity.enums.PlanChapter;
import ru.urfu.service.convertors.PlanChapterConvertor;
import ru.urfu.utils.DocUtils;
import ru.urfu.utils.RequestTextGenerators.AbstractRequestTextGenerator;
import ru.urfu.utils.RequestTextGenerators.Factory.RequestTextFactory;

import javax.ws.rs.BadRequestException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.urfu.entity.enums.UserState.*;
import static ru.urfu.entity.enums.UserState.MAIN_MENU_STATE;

@Service
@Log4j
public class DialogService {

    private final AppUserDAO appUserDAO;
    private final ProduceService produceService;
    private final AnswerDAO answerDAO;

    private final DocUtils docUtils;
    private final AppDocumentDAO appDocumentDAO;


    public DialogService(AppUserDAO appUserDAO, ProduceService produceService, AnswerDAO answerDAO, DocUtils docUtils, AppDocumentDAO appDocumentDAO) {
        this.appUserDAO = appUserDAO;
        this.produceService = produceService;
        this.answerDAO = answerDAO;
        this.docUtils = docUtils;
        this.appDocumentDAO = appDocumentDAO;
    }

    public void startDialog(Long chatId, String userAnswer, AppUser appUser) {
        appUser.setState(DIALOG_STATE);
        appUser.setUserStatePosition(0);
        PlanChapter planChapter = PlanChapterConvertor.PlanChapterByText.get(userAnswer);
        appUser.setPlanChapter(planChapter);

/*        //TODO добавить вызов метода очистки ласт вопросов
        //var answers = new ArrayList<Answer>();
        appUser.removeAnswers();*/

        appUserDAO.saveAndFlush(appUser);

        var requestTextGenerator = RequestTextFactory.getRequestText(planChapter.toString());
        var questions = requestTextGenerator.getQuestions();
        var firstQuestion = questions.get(0);

        produceService.produceMessage(chatId, firstQuestion);
    }

    public void continueDialog(Long chatId, String userAnswer, AppUser appUser) {
        var planChapter = appUser.getPlanChapter();
        var currentStatePosition = appUser.getUserStatePosition();
        currentStatePosition = currentStatePosition + 1;

        var requestTextGenerator = RequestTextFactory.getRequestText(planChapter.toString());

        var userAnswers = appUser.getAnswers();
        var answer = new Answer();
        answer.setUser(appUser);
        answer.setAnswer(userAnswer);
        userAnswers.add(answer);
        appUser.setAnswers(userAnswers);
        answerDAO.saveAndFlush(answer);

        appUser.setUserStatePosition(currentStatePosition);
        appUserDAO.saveAndFlush(appUser);

        var questionsCount = requestTextGenerator.getQuestions().size();
        if (currentStatePosition < questionsCount){
            var currentQuestion = requestTextGenerator.getQuestions().get(currentStatePosition);
            produceService.produceMessage(chatId, currentQuestion);

            return;
        }

        appUser.setState(WAITING_FOR_DOCUMENT_STATE);
        appUserDAO.saveAndFlush(appUser);

        var answers = userAnswers.stream().map(Answer::getAnswer).toArray(String[]::new);

        createPlanChapter(chatId, planChapter, answers, requestTextGenerator, appUser);
    }

    public void createPlanChapter(Long chatId, PlanChapter planChapter, String[] answers,
                                     AbstractRequestTextGenerator requestTextGenerator, AppUser appUser) {
        var requestText = requestTextGenerator.getRequestText(answers);

        InputStream docStream;
        try {
            docStream =  docUtils.getPlanChapterInputStream(planChapter, requestText);
        } catch (IOException | BadRequestException e) {
            catchErrorAndBackToMainMenu(chatId, appUser, e, "Ошибка. Попробуйте снова");
            return;
        }

        byte[] docBytes;
        try {
            docBytes = IOUtils.toByteArray(docStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var docByteStream = new ByteArrayInputStream(docBytes);

        var fileName = planChapter.toString();
        //Отправление файла
        produceService.produceDocument(chatId, docByteStream, fileName);

        docByteStream.reset();
        saveDocument(appUser,fileName, docByteStream);

        //docUtils.saveDocument("TEST_NAME", docStream);

        appUser.setState(MAIN_MENU_STATE);
        appUser.removeAnswers();
        appUserDAO.saveAndFlush(appUser);

        //return "Файл успешно создан";
    }

    private void catchErrorAndBackToMainMenu(Long chatId, AppUser appUser, Exception e, String message) {
        log.error("Ошибка получения документа " + e);

        appUser.setState(MAIN_MENU_STATE);
        appUser.removeAnswers();
        appUserDAO.saveAndFlush(appUser);

        ArrayList<String> rowsString = new ArrayList<>();
        rowsString.add("Главное меню");

        produceService.produceMessage(chatId, message, rowsString);
    }


    //TODO вынести в StorageUtils
    public void saveDocument(AppUser appUser, String filename, InputStream documentInputStream){

        byte[] documentByteArray;
        try {
            documentByteArray = IOUtils.toByteArray(documentInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var document = new AppDocument();
        document.setDocName(filename);
        document.setMimeType(".docx");
        document.setFileContent(documentByteArray);
        document.setUser(appUser);

        appDocumentDAO.save(document);
    }
}
