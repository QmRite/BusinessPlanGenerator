package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Component
public class MarketingParser extends AbstractPlanChapterParser {

    @Override
    public String getPlanChapter() {
        return PlanChapter.MARKETING.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public String getParsedContentJSON(String rawContent, String requestText) {
        var result = new HashMap<String, Object>();

        var rawUnorderedContentJson = getJsonFromResponseText(rawContent);
        var rawOrderedContentJson = rawUnorderedContentJson.replaceAll("marketing", "1marketing");
        rawOrderedContentJson = rawOrderedContentJson.replaceAll("cost", "2cost");
        var marketingTable = getMarketingTable(rawOrderedContentJson);
        result.put("MarketingTable", marketingTable);

        var competitorsTable = getCompetitors(requestText);
        result.put("CompetitorsTable", competitorsTable);

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private static List<TreeMap<String, String>> getMarketingTable(String rawContent) {
        ObjectMapper mapper = new ObjectMapper();

        List<TreeMap<String, String>> rawTable = null;
        try {
            rawTable = mapper.readValue(
                    rawContent, new TypeReference<>() {
                    }
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var costSum = 0;
        for(var row : rawTable){
            var costInt = Integer.parseInt(row.get("2cost"));
            costSum+= costInt;
        }
        var footer = new TreeMap<String, String>();
        footer.put("1marketing", "Итоговая стоимость ");
        footer.put("2cost", String.valueOf(costSum));
        rawTable.add(footer);

        var header = new TreeMap<String, String>();
        header.put("1marketing", "Что вы собираетесь делать?");
        header.put("2cost", "Сколько это будет стоить?");
        rawTable.add(0, header);

        return rawTable;
    }

    private List<TreeMap<String, String>> getCompetitors(String requestText) {
        var companies = getCompaniesStrings(requestText);
        var companyInfoByName = getCompanyInfoByName(companies);
        var competitorsTable = getCompetitorsTable(companyInfoByName);
        return competitorsTable;
    }

    private static String[] getCompaniesStrings(String requestText) {
        var companiesStringLeft = requestText.indexOf("Конкуренты: ") + 12;
        var companiesStringRight = requestText.indexOf(" Максимальный бюджет:");
        var companiesString = requestText.substring(companiesStringLeft, companiesStringRight);
        var companies = companiesString.split(", ");
        return companies;
    }

    private List<TreeMap<String, String>> getCompetitorsTable(HashMap<String, JSONObject> companyInfoByName) {
        List<TreeMap<String, String>> rawTable = new ArrayList<TreeMap<String, String>>();
        var headerRow = new TreeMap<String, String>();
        headerRow.put("1elements", "Элементы сравнения");
        headerRow.put("2myCompany", "Моя организация");
        for (var companyName : companyInfoByName.keySet()){
            headerRow.put(companyName, companyName);
        }
        rawTable.add(headerRow);

        var okvedRow = new TreeMap<String, String>();
        okvedRow.put("1elements", "Вид деятельности");
        okvedRow.put("2myCompany", "");
        for (var companyName : companyInfoByName.keySet()){
            okvedRow.put(companyName, getOkved(companyInfoByName.get(companyName)));
        }
        rawTable.add(okvedRow);

        var placeRow = new TreeMap<String, String>();
        placeRow.put("1elements", "Расположение");
        placeRow.put("2myCompany", "");
        for (var companyName : companyInfoByName.keySet()){
            placeRow.put(companyName, getPlace(companyInfoByName.get(companyName)));
        }
        rawTable.add(placeRow);

        var capitalRow = new TreeMap<String, String>();
        capitalRow.put("1elements", "Уставной капитал");
        capitalRow.put("2myCompany", "");
        for (var companyName : companyInfoByName.keySet()){
            capitalRow.put(companyName, getCapital(companyInfoByName.get(companyName)));
        }
        rawTable.add(capitalRow);

        return rawTable;
    }

    private HashMap<String, JSONObject> getCompanyInfoByName(String[] companies) {
        var companyInfoByName = new HashMap<String, JSONObject>();
        for (var company : companies){
            var inn = getInnByCompanyName(company);
            var companyInfoJson = getCompanyInfoByInn(inn);
            companyInfoByName.put(company, companyInfoJson);
        }
        return companyInfoByName;
    }

    private String getInnByCompanyName(String companyName){
        Document doc = null;
        try {
            doc = Jsoup.connect("https://egrul.itsoft.ru/s/?" +
                "region=" +
                "&short_name=" + companyName +
                "&full_name=" +
                "&type=NATURAL+LANGUAGE").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var innString = doc.select("td")
                .first()
                .child(0)
                .textNodes()
                .toString();


        return innString.substring(1, innString.length() - 1);
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
                .getJSONObject("СвМНЮЛ");

        if (placeJson.has("НаселенПункт")){
            placeJson = placeJson.getJSONObject("НаселенПункт")
                    .getJSONObject("@attributes");

            var type = placeJson.getString("Вид");
            var name = placeJson.getString("Наим");

            return type + " " + name;
        }
        var res = placeJson.getString("НаимРегион");
        return res;
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
