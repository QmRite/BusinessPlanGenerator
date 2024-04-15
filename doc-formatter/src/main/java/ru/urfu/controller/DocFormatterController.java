package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.dao.AppDocumentDAO;
import ru.urfu.entity.AppDocument;
import ru.urfu.entity.BinaryContent;
import ru.urfu.entity.enums.PlanChapter;
import ru.urfu.service.impl.ApiService;
import ru.urfu.service.impl.docServices.DescriptionDocService;
import ru.urfu.service.impl.docServices.factory.DocServiceFactory;
import ru.urfu.utils.PlanChapterNameConvertor;

import java.io.IOException;

@Log4j
@RequestMapping("/docFormatter")
@RestController
public class DocFormatterController {
    private final ApiService apiService;

    @Autowired
    AppDocumentDAO appDocumentDAO;

    public DocFormatterController(ApiService apiService, DescriptionDocService docService) {
        this.apiService = apiService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/new_plan_chapter/{plan_chapter}")
    public ResponseEntity<?> PlanContentByChapter(@PathVariable("plan_chapter") String planChapterString,
                                                  @RequestBody String requestText) {
        //TODO для формирования badRequest добавить ControllerAdvice


        //получение JSON-контента
        var planChapter = PlanChapter.valueOf(planChapterString.toUpperCase());

        JSONObject content = null;
        try {
            content = apiService.getContentByPlanChapter(planChapter, requestText);
        } catch (IOException e) {
            log.error("Ошибка при выполнении запроса к Api-Service: " + e);
            return ResponseEntity.internalServerError().build();
        }

        //Генерация ворд-файла
        var docService = DocServiceFactory.getDocService(planChapter.toString());
        byte[] planChapterBinary = null;
        try {
            planChapterBinary = docService.createPlanChapterDoc(content);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var filename = PlanChapterNameConvertor.nameByPlanChapter.get(planChapter) + ".docx";
        //saveDocument(filename, planChapterBinary);

        var fileSystemResource = docService.getFileSystemResource(planChapterBinary);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .header("Content-disposition", "attachment; filename=" + filename)
                .body(fileSystemResource);
    }

    private void saveDocument(String filename, byte[] planChapterBinary){
        var binaryContent = new BinaryContent();
        binaryContent.setFileAsArrayOfBytes(planChapterBinary);

        var document = new AppDocument();
        document.setBinaryContent(binaryContent);
        document.setDocName(filename);
        document.setMimeType(".docx");

        appDocumentDAO.saveAndFlush(document);
    }
}
