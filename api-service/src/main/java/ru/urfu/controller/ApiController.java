package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.entity.enums.PlanChapter;
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

    @RequestMapping(method = RequestMethod.POST, value = "/new_plan_content/{plan_chapter}")
    public ResponseEntity<?> PlanContentByChapter(@PathVariable("plan_chapter") String planChapter,
                                                  @RequestBody String requestText) {
        //TODO для формирования badRequest добавить ControllerAdvice

        HttpResponse response = null;
        try {
            response = ApiService.sendRequestByPlanChapter(PlanChapter.valueOf(planChapter.toUpperCase()), requestText);
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

        var res = planChapterParser.getParsedContentJSON(responseText);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(res);
    }

}
