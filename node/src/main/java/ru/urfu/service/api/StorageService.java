package ru.urfu.service.api;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.urfu.dao.AppDocumentDAO;
import ru.urfu.dao.AppUserDAO;
import ru.urfu.entity.AppUser;

import java.io.ByteArrayInputStream;

import static ru.urfu.entity.enums.UserState.MAIN_MENU_STATE;

@Service
@Log4j
public class StorageService {
    private final AppUserDAO appUserDAO;
    private final AppDocumentDAO appDocumentDAO;
    private final ProduceService produceService;

    public StorageService(AppUserDAO appUserDAO, AppDocumentDAO appDocumentDAO, ProduceService produceService) {
        this.appUserDAO = appUserDAO;
        this.appDocumentDAO = appDocumentDAO;
        this.produceService = produceService;
    }

    public void GetLastDocument(Long chatId, AppUser appUser){
        var userDocuments = appUser.getDocuments();

        if (userDocuments.isEmpty()){
            produceService.produceMessage(chatId, "Вы еще не создали ни одного файла!");
            appUser.setState(MAIN_MENU_STATE);
            appUserDAO.saveAndFlush(appUser);
            return;
        }

        var lastDocument = userDocuments.get(userDocuments.size() - 1);
        var lastDocumentStream = new ByteArrayInputStream(lastDocument.getFileContent());

        produceService.produceDocument(chatId, lastDocumentStream, lastDocument.getDocName());

        appUser.setState(MAIN_MENU_STATE);
        appUserDAO.saveAndFlush(appUser);
    }
}
