package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.*;

@Component
public class IndustryParser extends AbstractPlanChapterParser {
    @Override
    public String getPlanChapter() {
        return PlanChapter.INDUSTRY.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public String getParsedContentJSON(String rawContent, String requestText) {
        throw new NotImplementedException("not implemented");
    }

    public String getParsedContentJSON(String processText, String expensesText, String calendarText) {
        var rawByResponse = new HashMap<String, Object>();

        var rawCalendarTableJson = getJsonFromResponseText(calendarText);
        var calendarTable = getCalendarTable(rawCalendarTableJson);
        rawByResponse.put("CalendarTable", calendarTable);

        var rawProcessTableJson = getJsonFromResponseText(processText);
        rawProcessTableJson = rawProcessTableJson.replaceAll("process step", "1process step");
        rawProcessTableJson = rawProcessTableJson.replaceAll("resources", "2resources");
        rawProcessTableJson = rawProcessTableJson.replaceAll("cost", "3cost");
        var processTable = getProcessTable(rawProcessTableJson);
        rawByResponse.put("ProcessTable", processTable);

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(rawByResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private List<TreeMap<String, String>> getProcessTable(String rawContentJson) {
        ObjectMapper mapper = new ObjectMapper();

        List<TreeMap<String, String>> rawTable = null;
        try {
            rawTable = mapper.readValue(
                    rawContentJson, new TypeReference<>() {
                    }
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var header = new TreeMap<String, String>();
        header.put("1process step", "Шаг технологического процесса");
        header.put("2resources", "Необходимые ресурсы");
        header.put("3cost", "Затраты на ресурсы, руб");

        rawTable.add(0, header);
        return rawTable;
    }

    @Override
    protected String getJsonFromResponseText(String responseText){
        var jsonBeginIndex = responseText.indexOf("[");
        var jsonEndIndex = responseText.indexOf("}\n]") + 3;
        return responseText.substring(jsonBeginIndex, jsonEndIndex);
    }

    private static List<TreeMap<String, String>> getCalendarTable(String rawContent) {
        ObjectMapper mapper = new ObjectMapper();

        List<TreeMap<String, Object>> rawTableWithArray = null;
        try {
            rawTableWithArray = mapper.readValue(
                   rawContent, new TypeReference<>() {
                   }
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var rawTable = new ArrayList<TreeMap<String, String>>();
        var maxWeeks = 0;
        for (var row : rawTableWithArray){
            var stepsObject = row.get("steps");
            var stepsArray = (ArrayList<String>)stepsObject;

            for (var step : stepsArray){
                var rawTableRow = new TreeMap<String, String>();
                rawTableRow.put("step", step);
                var dateInt = (Integer) row.get("date");
                if (dateInt > maxWeeks)
                    maxWeeks = dateInt;
                rawTableRow.put("date", Integer.toString(dateInt));
                rawTable.add(rawTableRow);
            }
        }

        var calendarTable = new ArrayList<TreeMap<String, String>>();

        for (var rawRow : rawTable){
            var calendarTableRow = new TreeMap<String, String>();
            calendarTableRow.put("0step", rawRow.get("step"));
            for (var i = 1; i < maxWeeks + 1; i++){
                if (Integer.parseInt(rawRow.get("date")) == i){
                    calendarTableRow.put(i + "week", "X");
                }
                else
                    calendarTableRow.put(i + "week", "");
            }
            calendarTable.add(calendarTableRow);
        }

        var header = new TreeMap<String, String>();
        header.put("0step", "Действия");
        for (var i = 1; i < maxWeeks + 1; i++){
            header.put(i + "week", i + " нед.");
        }
        calendarTable.add(0, header);


        return calendarTable;
    }

}
