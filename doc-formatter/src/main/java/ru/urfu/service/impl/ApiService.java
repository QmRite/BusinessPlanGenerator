package ru.urfu.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.urfu.entity.enums.PlanChapter;

import java.io.IOException;

@Service
@Log4j
public class ApiService {
    public JSONObject getContentByPlanChapter(PlanChapter planChapter, String body) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8086/api/new_plan_content/" + planChapter.toString());
        HttpEntity entity = new StringEntity(body, "UTF-8");
        httpPost.setEntity(entity);

        var response = httpClient.execute(httpPost);
        String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

        return new JSONObject(responseBody);
    }


}
