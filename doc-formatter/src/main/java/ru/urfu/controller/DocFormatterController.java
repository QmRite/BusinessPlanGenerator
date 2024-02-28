package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.service.enums.PlanChapter;
import ru.urfu.service.impl.ApiService;
import ru.urfu.service.impl.docServices.DescriptionDocService;
import ru.urfu.service.impl.docServices.factory.DocServiceFactory;

import java.io.IOException;

@Log4j
@RequestMapping("/docFormatter")
@RestController
public class DocFormatterController {
    private final ApiService apiService;

    public DocFormatterController(ApiService apiService, DescriptionDocService docService) {
        this.apiService = apiService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get_new_plan_chapter/{plan_chapter}")
    public ResponseEntity<?> getPlanContentByChapter(@PathVariable("plan_chapter") String planChapterString) {
        //TODO для формирования badRequest добавить ControllerAdvice

        //получение JSON-контента
        var planChapter = PlanChapter.valueOf(planChapterString.toUpperCase());

        JSONObject content = null;
        try {
            content = apiService.getContentByPlanChapter(planChapter);
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
        var fileSystemResource = docService.getFileSystemResource(planChapterBinary);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .header("Content-disposition", "attachment; filename=output.docx")
                .body(fileSystemResource);
    }
}
