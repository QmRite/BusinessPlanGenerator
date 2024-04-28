package ru.urfu.service.impl.PlanChapterParsers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.io.IOException;

@Component
public class MarketingParser extends AbstractPlanChapterParser {

    @Override
    public String getPlanChapter() {
        return PlanChapter.MARKETING.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {
        var a = getInnByCompanyName("лакру");
        var companyInfoJson = getCompanyInfoByInn(rawContent);
        var okved = getOkved(companyInfoJson);
        var place = getPlace(companyInfoJson);
        var capital = getCapital(companyInfoJson);
        return "";
    }

    private JSONObject getInnByCompanyName(String companyName){
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://egrul.itsoft.ru/s/?" +
                "region=&short_name=%D0%BB%D0%B0%D0%BA%D1%80%D1%83" +
                "&full_name=" +
                "&type=NATURAL+LANGUAGE");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String responseBody = null;
        try {
            responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new JSONObject(responseBody);
    }

    private String getCapital(JSONObject companyInfo){
        return companyInfo.getJSONObject("СвУстКап")
                .getJSONObject("@attributes")
                .getString("СумКап");
    }

    private String getOkved(JSONObject companyInfo){
        return companyInfo.getJSONObject("СвОКВЭД")
                .getJSONObject("СвОКВЭДОсн")
                .getJSONObject("@attributes")
                .getString("НаимОКВЭД");
    }

    private String getPlace(JSONObject companyInfo){
        var placeJson = companyInfo.getJSONObject("СвАдресЮЛ")
                .getJSONObject("СвМНЮЛ")
                .getJSONObject("НаселенПункт")
                .getJSONObject("@attributes");

        var type = placeJson.getString("Вид");
        var name = placeJson.getString("Наим");

        return type + " " + name;
    }

    private JSONObject getCompanyInfoByInn(String inn){
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://egrul.itsoft.ru/" + inn + ".json");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String responseBody = null;
        try {
            responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new JSONObject(responseBody).getJSONObject("СвЮЛ");
    }
}
