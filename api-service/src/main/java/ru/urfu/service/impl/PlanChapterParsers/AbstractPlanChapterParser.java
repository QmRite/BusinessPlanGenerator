package ru.urfu.service.impl.PlanChapterParsers;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public abstract class AbstractPlanChapterParser {
    public abstract String getPlanChapter();
    public abstract String getParsedContentJSON(String rawContent);

    protected static String[] parseTable(String rawTable){
        var responseRows = rawTable.split("\n");
        var answersArray = new String[responseRows.length - 2];

        for (var i = 2; i < responseRows.length; i++){
            answersArray[i - 2] = responseRows[i].split("\\|")[2];
        }

        return answersArray;
    }


    //TODO вынести в отдельный класс
    public String getResponseText(HttpResponse response) throws IOException {
        String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

        JSONObject jsonObject = new JSONObject(responseBody);

        return jsonObject.getJSONObject("result")
                .getJSONArray("alternatives")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("text");
    }
}
