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

    public static final String NUM = "0num";
    public static final String EXPENSE = "1expense";
    public static final String COUNT = "2count";
    public static final String COST = "3cost";
    public static final String TOTAL = "4total";
    public static final String RESOURCES = "2resources";
    public static final String PROCESS_STEP = "1process step";

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

        var rawProcessTableJson = getJsonFromResponseText(processText);
        rawProcessTableJson = rawProcessTableJson.replaceAll("process step", PROCESS_STEP);
        rawProcessTableJson = rawProcessTableJson.replaceAll("resources", RESOURCES);
        rawProcessTableJson = rawProcessTableJson.replaceAll("cost", COST);
        var processTable = getProcessTable(rawProcessTableJson);
        rawByResponse.put("ProcessTable", processTable);

        var rawExpensesTableJson = getJsonFromResponseText(expensesText);
        rawExpensesTableJson = rawExpensesTableJson.replaceAll("expense", EXPENSE);
        rawExpensesTableJson = rawExpensesTableJson.replaceAll("count", COUNT);
        rawExpensesTableJson = rawExpensesTableJson.replaceAll("cost", COST);
        var expensesTable = getExpensesTable(rawExpensesTableJson);
        rawByResponse.put("ExpensesTable", expensesTable);

        var rawCalendarTableJson = getJsonFromResponseText(calendarText);
        var calendarTable = getCalendarTable(rawCalendarTableJson);
        rawByResponse.put("CalendarTable", calendarTable);

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(rawByResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private List<TreeMap<String, String>> getExpensesTable(String rawContentJson) {
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

        var sumTotal = 0;
        for(var i = 0; i < rawTable.size(); i++){
            var row = rawTable.get(i);
            var countInt = Integer.parseInt(row.get(COUNT));
            var costInt = Integer.parseInt(row.get(COST));
            var total = costInt * countInt;
            sumTotal += total;
            
            row.put(TOTAL, Integer.toString(total));
            row.put(NUM, Integer.toString(i + 1));
        }

        var footer = new TreeMap<String, String>();
        footer.put(NUM, "");
        footer.put(EXPENSE, "");
        footer.put(COUNT, "");
        footer.put(COST, "ИТОГО");
        footer.put(TOTAL, Integer.toString(sumTotal));
        rawTable.add(footer);
        
        var header = new TreeMap<String, String>();
        header.put(NUM, "№");
        header.put(EXPENSE, "Наименование затрат");
        header.put(COUNT, "Стоимость за единицу, руб");
        header.put(COST, "Количество единиц");
        header.put(TOTAL, "Общая стоимость");

        rawTable.add(0, header);
        return rawTable;
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
        header.put(PROCESS_STEP, "Шаг технологического процесса");
        header.put(RESOURCES, "Необходимые ресурсы");
        header.put(COST, "Затраты на ресурсы, руб");

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
