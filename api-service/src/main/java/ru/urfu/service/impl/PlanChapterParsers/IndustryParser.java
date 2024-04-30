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
        var rawContentJson = getJsonFromResponseText(rawContent);
        var risksTable = getCalendarTable(rawContentJson);

        var rawByResponse = new HashMap<String, Object>();
        rawByResponse.put("Table", risksTable);

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(rawByResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
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
