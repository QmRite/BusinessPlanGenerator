package ru.urfu.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.urfu.service.enums.PlanChapter;

import java.io.IOException;
import java.io.InputStream;

@Component
public class DocUtils {
    public InputStream getPlanChapterInputStream(PlanChapter planChapter) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8087/docFormatter/get_new_plan_chapter/" + planChapter);

        HttpResponse response = httpClient.execute(httpGet);
        return response.getEntity().getContent();
    }
}
