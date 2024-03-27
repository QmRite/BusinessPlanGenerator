package ru.urfu.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.urfu.service.enums.PlanChapter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

@Service
@Log4j
public class ApiService {
    @Value("${yandex.iam.token}")
    private String yandexIamToken;

    @Value("${yandex.api.key}")
    private String yandexApiKey;

    @Value("${yandex.uri}")
    private String yandexUri;

    private static Map<PlanChapter, String> RequestNameByPlanChapter = Map.ofEntries(
            entry(PlanChapter.DESCRIPTION, "request.json"),
            entry(PlanChapter.PRODUCT_DESCRIPTION, "request2.json")
    );

    public static HttpResponse sendRequestByPlanChapter(PlanChapter planChapter, String requestText) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://llm.api.cloud.yandex.net/foundationModels/v1/completion");

        //TODO Сделать запрос в зависимости от раздела бизнес-плана
        String jsonRequest = null;
        try {
            //TODO поменять на относительный путь
            jsonRequest = FileUtils.readFileToString(
                    new File("C:\\Users\\user\\IdeaProjects\\BusinessPlanGenerator\\api-service\\src\\main\\resources\\"
                            + RequestNameByPlanChapter.get(planChapter)),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Невозможно прочитать файл запроса: " + e);
        }

        jsonRequest = jsonRequest.replace("[1]", requestText);
        StringEntity jsonBody = new StringEntity(jsonRequest, ContentType.APPLICATION_JSON);
        httpPost.setEntity(jsonBody);

        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Api-Key AQVN26iSjSRxgPpdcltKoaqeKeWPoCSP6cvzRGCD");

        return httpClient.execute(httpPost);
    }
}
