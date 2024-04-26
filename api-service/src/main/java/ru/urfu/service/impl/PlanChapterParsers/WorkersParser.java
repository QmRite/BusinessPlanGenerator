package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Component
public class WorkersParser extends AbstractPlanChapterParser {
    public static final String DEDUCTIONS = "4deductions";
    public static final String VACANCY = "1vacancy";
    public static final String QUANTITY = "2quantity";
    public static final String SALARY = "3salary";
    public static final String SUM = "5sum";

    @Override
    public String getPlanChapter() {
        return PlanChapter.WORKERS.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {
        var rawUnorderedContentJson = getJsonFromResponseText(rawContent);
        var rawOrderedContentJson = rawUnorderedContentJson.replaceAll("vacancy", VACANCY);
        rawOrderedContentJson = rawOrderedContentJson.replaceAll("quantity", QUANTITY);
        rawOrderedContentJson = rawOrderedContentJson.replaceAll("salary", SALARY);
        var risksTable = getWorkersTable(rawOrderedContentJson);

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

    private static List<TreeMap<String, String>> getWorkersTable(String rawContent) {
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

        for(var row : rawTable){
            var salaryInt = Integer.parseInt(row.get(SALARY));
            var deductions = (int) Math.round(salaryInt * 0.3);

            var quantityInt = Integer.parseInt(row.get(QUANTITY));
            var sum = (salaryInt + deductions) * quantityInt;

            row.put(DEDUCTIONS, Integer.toString(deductions));
            row.put(SUM, Integer.toString(sum));
        }

        setFooter(rawTable);
        setHeader(rawTable);

        return rawTable;
    }

    private static void setFooter(List<TreeMap<String, String>> rawTable) {
        var footer = new TreeMap<String, String>();
        var quantitySum = 0;
        var salarySum = 0;
        var deductionsSum = 0;
        var sumSum = 0;
        for(var row : rawTable){
            salarySum += Integer.parseInt(row.get(SALARY));
            deductionsSum += Integer.parseInt(row.get(DEDUCTIONS));
            quantitySum += Integer.parseInt(row.get(QUANTITY));
            sumSum += Integer.parseInt(row.get(SUM));
        }

        footer.put(VACANCY, "ИТОГО");
        footer.put(QUANTITY, Integer.toString(quantitySum));
        footer.put(SALARY, Integer.toString(salarySum));
        footer.put(DEDUCTIONS, Integer.toString(deductionsSum));
        footer.put(SUM, Integer.toString(sumSum));
        rawTable.add(footer);
    }

    private static void setHeader(List<TreeMap<String, String>> rawTable) {
        var header = new TreeMap<String, String>();
        header.put(VACANCY, "Должность");
        header.put(QUANTITY, "Количество");
        header.put(SALARY, "Оклад, руб");
        header.put(DEDUCTIONS, "Отчисления");
        header.put(SUM, "Итого, руб.");
        rawTable.add(0, header);
    }

}
