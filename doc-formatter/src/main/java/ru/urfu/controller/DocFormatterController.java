package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.service.enums.PlanChapter;
import ru.urfu.service.impl.ApiService;
import ru.urfu.service.impl.DocService;

import java.io.IOException;

@Log4j
@RequestMapping("/docFormatter")
@RestController
public class DocFormatterController {
    private final ApiService apiService;
    private final DocService docService;

    public DocFormatterController(ApiService apiService, DocService docService) {
        this.apiService = apiService;
        this.docService = docService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get_new_plan_chapter")
    public ResponseEntity<?> getPlanContentByChapter() {
        //TODO для формирования badRequest добавить ControllerAdvice

        JSONObject content = null;
        try {
            content = apiService.getContentByPlanChapter(PlanChapter.DESCRIPTION);
        } catch (IOException e) {
            log.error("Ошибка при выполнении запроса к Api-Service: " + e);
            return ResponseEntity.internalServerError().build();
        }

        byte[] planChapterBinary = null;
        try {
            planChapterBinary = docService.createPlanChapterDoc(PlanChapter.DESCRIPTION, content);
        } catch (IOException | InvalidFormatException e) {
            log.error("Ошибка при открытии файла шаблона: " + e);
            return ResponseEntity.internalServerError().build();
        }

        var fileSystemResource = docService.getFileSystemResource(planChapterBinary);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .header("Content-disposition", "attachment; filename=output.docx")
                .body(fileSystemResource);
    }
}
