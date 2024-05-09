package ru.urfu.service.impl.PlanChapterParsers;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public abstract class AbstractPlanChapterParser {
    public abstract String getPlanChapter();
    public abstract String getParsedContentJSON(String rawContent);

    public  String getParsedContentJSON(String rawContent, String requestText){
        return getParsedContentJSON(rawContent);
    };

    protected static String[] parseTable(String rawTable){
        var responseRows = rawTable.split("\n");
        var answersArray = new String[responseRows.length - 2];

        for (var i = 2; i < responseRows.length; i++){
            answersArray[i - 2] = responseRows[i].split("\\|")[2];
        }

        return answersArray;
    }


    //TODO вынести в отдельный класс
    public String getResponseText(HttpResponse response) {
        String responseBody = null;
        try {
            responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = new JSONObject(responseBody);

        return jsonObject.getJSONObject("result")
                .getJSONArray("alternatives")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("text");
    }

    protected String getJsonFromResponseText(String responseText){
        var jsonBeginIndex = responseText.indexOf("[");
        var jsonEndIndex = responseText.indexOf("]") + 1;
        return responseText.substring(jsonBeginIndex, jsonEndIndex);
    }


    protected String getSubstring(String sourceText, String startText, String endText){
        var startTextIndex = sourceText.indexOf(startText + " ") + startText.length() + 1;
        var endTextIndex = sourceText.indexOf(" " + endText);
        return sourceText.substring(startTextIndex, endTextIndex);
    }

    protected String getSubstring(String sourceText, String startText){
        var startTextIndex = sourceText.indexOf(startText + " ") + startText.length() + 1;
        return sourceText.substring(startTextIndex);
    }
}
