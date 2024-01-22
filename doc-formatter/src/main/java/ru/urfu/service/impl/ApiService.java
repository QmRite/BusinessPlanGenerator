package ru.urfu.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.urfu.service.enums.PlanChapter;

import java.io.IOException;

@Service
@Log4j
public class ApiService {
    public JSONObject getContentByPlanChapter(PlanChapter planChapter) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8086/api/get_plan_content/" + planChapter.toString());

        var response = httpClient.execute(httpGet);
        String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

        return new JSONObject(responseBody);
    }


}
