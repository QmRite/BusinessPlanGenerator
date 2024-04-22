package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.HashMap;
import java.util.List;

@Component
public class ProductDescriptionParser extends AbstractPlanChapterParser {
    @Override
    public String getPlanChapter() {
        return PlanChapter.PRODUCT_DESCRIPTION.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public String getParsedContentJSON(String rawContent, String requestText) {
        var rawContentJson = getJsonFromResponseText(rawContent);
        List<HashMap<String, String>> problemsTable = getProblemsTable(rawContentJson);

        //var rawByResponse = new HashMap<String, List<HashMap<String, String>>>();.
        var rawByResponse = new HashMap<String, Object>();
        rawByResponse.put("Table", problemsTable);

        var products = getProducts(requestText);

        rawByResponse.put("Products", products);
        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(rawByResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private static String getProducts(String requestText) {
        var productsBeginIndex = requestText.indexOf("Товары: ") + "Товары: ".length();
        var productsEndIndex = requestText.length();
        return requestText.substring(productsBeginIndex, productsEndIndex);
    }

    private static List<HashMap<String, String>> getProblemsTable(String rawContent) {
        ObjectMapper mapper = new ObjectMapper();

        List<HashMap<String, String>> rawTable = null;
        try {
           rawTable = mapper.readValue(
                   rawContent, new TypeReference<>() {
                   }
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var header = new HashMap<String, String>();
        header.put("problem", "Потребность (проблема)");
        header.put("solution", "Ваше УТП (решение проблемы)");

        rawTable.add(0, header);
        return rawTable;
    }

}
