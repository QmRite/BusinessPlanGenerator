package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

@Component
public class RisksParser extends AbstractPlanChapterParser {
    @Override
    public String getPlanChapter() {
        return PlanChapter.RISKS.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public String getParsedContentJSON(String rawContent, String requestText) {
        var rawUnorderedContentJson = getJsonFromResponseText(rawContent);
        var rawOrderedContentJson = rawUnorderedContentJson.replaceAll("risk", "1risk");
        rawOrderedContentJson = rawOrderedContentJson.replaceAll("minimization", "2minimization");
        var risksTable = getRisksTable(rawOrderedContentJson);

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

    private static List<TreeMap<String, String>> getRisksTable(String rawContent) {
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

        var header = new TreeMap<String, String>();
        header.put("1risks", "Описание риска");
        header.put("2minimization", "Способы его минимизировать");

        rawTable.add(0, header);
        return rawTable;
    }

}
