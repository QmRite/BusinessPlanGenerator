package ru.urfu.service.api;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.dao.AppUserDAO;
import ru.urfu.entity.AppUser;
import ru.urfu.entity.enums.PlanChapter;
import ru.urfu.entity.enums.UserState;
import ru.urfu.utils.RequestTextGenerators.Factory.RequestTextFactory;

import java.util.ArrayList;

import static ru.urfu.entity.enums.UserState.*;

@Service
@Log4j
public class UserStateService {
    private final AppUserDAO appUserDAO;

    private final ProduceService produceService;
    private final DialogService dialogService;


    public UserStateService(AppUserDAO appUserDAO, ProduceService produceService, DialogService dialogService) {
        this.appUserDAO = appUserDAO;
        this.produceService = produceService;
        this.dialogService = dialogService;
    }

    public void processTextMessage(Update update) {

        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        var output = "";
        ArrayList<String> rowsString = new ArrayList<>();

        //состояния
        if (CHAPTER_SELECTION_STATE.equals(appUser.getState())){
            dialogService.startDialog(chatId, text, appUser);

/*            var requestTextGenerator = RequestTextFactory.getRequestText(PlanChapter.DESCRIPTION.toString());
            String[] answers = {"Продажа цветов", "Екатеринбург", "продажа", "21"};
            dialogService.createPlanChapter(chatId, PlanChapter.DESCRIPTION,
                    answers, requestTextGenerator, appUser);*/
            return;
        }
        else if (DIALOG_STATE.equals(appUser.getState())){
            output = "Вы в диалоге";

            dialogService.continueDialog(chatId, text, appUser);
/*            appUser.setState(RECEIVED_DOCUMENT_STATE);
            appUserDAO.saveAndFlush(appUser);*/

            return;
        }

        //команды
        var inputUserState = fromValue(text);
        if (CHAPTER_SELECTION_STATE.equals(inputUserState)){
            appUser.setState(CHAPTER_SELECTION_STATE);
            appUserDAO.saveAndFlush(appUser);

            output = "Выберите главу бизнес-плана";

            rowsString.add("1. Описание бизнес-идеи");
            rowsString.add("2. Описание продукта");
            rowsString.add("3. Маркетинговый план");
            rowsString.add("4. Производственный план");
            rowsString.add("5. Человеческие ресурсы");
            rowsString.add("6. Финансы и инвестиции");
            rowsString.add("7. Оценка рисков");
            rowsString.add("8. Перспективы");

        } else if (MAIN_MENU_STATE.equals(inputUserState) || text.equals("/start")){
            output = "Главное меню:";

            rowsString.add(STORAGE_VIEWING_STATE.toString());
            rowsString.add(CHAPTER_SELECTION_STATE.toString());

        }
        else if (STORAGE_VIEWING_STATE.equals(inputUserState)){
            output = "Топор хранилище ранилище";
        }
        else if (HELP_STATE.equals(inputUserState) || text.equals("/help")){
            output = "Помощь";
        }

        else{
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова.";
        }

        produceService.produceMessage(chatId, output, rowsString);
    }

    //TODO вынести
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
}
