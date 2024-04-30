package ru.urfu.controller;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.entity.enums.PlanChapter;
import ru.urfu.service.impl.ApiService;
import ru.urfu.service.impl.PlanChapterParsers.Factory.PlanChapterFactory;
import ru.urfu.service.impl.PlanChapterParsers.IndustryParser;

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
        if (PlanChapter.valueOf(planChapter.toUpperCase()) == PlanChapter.INDUSTRY){
            return getIndustryEntity(requestText);
        }

        HttpResponse response = null;
        try {
            response = ApiService.sendRequestByPlanChapter(PlanChapter.valueOf(planChapter.toUpperCase()), requestText);
        } catch (IOException e) {
            log.error("Ошибка при выполнении запроса к YandexAPI: " + e);
            return ResponseEntity.internalServerError().build();
        }

        var planChapterParser = PlanChapterFactory.getParser(planChapter);
        String responseText  = planChapterParser.getResponseText(response);

        var res = planChapterParser.getParsedContentJSON(responseText, requestText);

        //var res = planChapterParser.getParsedContentJSON(requestText);


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(res);
    }

    private static ResponseEntity<String> getIndustryEntity(String requestText) {
        HttpResponse responseTechProcess = null;
        try {
            responseTechProcess = ApiService.sendRequestByPlanChapter(requestText, "request7.1.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpResponse responseExpenses = null;
        try {
            responseExpenses = ApiService.sendRequestByPlanChapter(requestText, "request7.2.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpResponse responseCalendar = null;
        try {
            responseCalendar = ApiService.sendRequestByPlanChapter(requestText, "request7.3.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var industryChapterParser = new IndustryParser();

        String responseTechProcessText  = industryChapterParser.getResponseText(responseTechProcess);
        String responseExpensesText = industryChapterParser.getResponseText(responseExpenses);
        String responseCalendarText  = industryChapterParser.getResponseText(responseCalendar);


        var res = industryChapterParser.getParsedContentJSON(responseTechProcessText, responseExpensesText, responseCalendarText);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(res);
    }

}
