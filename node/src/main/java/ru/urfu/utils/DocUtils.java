package ru.urfu.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Component;
import ru.urfu.dao.AppDocumentDAO;
import ru.urfu.entity.AppDocument;
import ru.urfu.entity.enums.PlanChapter;
import ru.urfu.service.api.DialogService;

import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.io.InputStream;

@Component
public class DocUtils {


    public InputStream getPlanChapterInputStream(PlanChapter planChapter, String body) throws IOException, BadRequestException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8087/docFormatter/new_plan_chapter/" + planChapter);
        HttpEntity entity = new StringEntity(body, "UTF-8");
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);

        if (response.getStatusLine().getStatusCode() == 400){
            throw new BadRequestException();
        }

        return response.getEntity().getContent();
    }

}
