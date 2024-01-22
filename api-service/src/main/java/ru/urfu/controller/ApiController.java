package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.service.enums.PlanChapter;
import ru.urfu.service.impl.ApiService;
import ru.urfu.service.impl.PlanChapterParsers.Factory.PlanChapterFactory;

import java.io.IOException;

@Log4j
@RequestMapping("/api")
@RestController
public class ApiController {
    private final ApiService apiService;
    private  final PlanChapterFactory planChapterFactory;

    public ApiController(ApiService apiService, PlanChapterFactory planChapterFactory) {
        this.apiService = apiService;
        this.planChapterFactory = planChapterFactory;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get_plan_content/{plan_chapter}")
    public ResponseEntity<?> getPlanContentByChapter(@PathVariable("plan_chapter") String planChapter) {
        //TODO для формирования badRequest добавить ControllerAdvice

        HttpResponse response = null;
        try {
            response = ApiService.sendRequestByPlanChapter(PlanChapter.RESUME);
        } catch (IOException e) {
            log.error("Ошибка при выполнении запроса к YandexAPI: " + e);
            return ResponseEntity.internalServerError().build();
        }

        var planChapterParser = PlanChapterFactory.getParser(planChapter);
        String responseText = null;
        try {
            responseText = planChapterParser.getResponseText(response);
        } catch (IOException e) {
            log.error("Ошибка при получении текста запроса: " + e);
            return ResponseEntity.internalServerError().build();
        }

        var res = planChapterParser.getParsedContent(responseText);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(res);
    }

}
