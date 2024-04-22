package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        ObjectMapper mapper = new ObjectMapper();

        List<HashMap<String, String>> rawTable = null;
        try {
           rawTable = mapper.readValue(
                    rawContent, new TypeReference<List<HashMap<String, String>>>(){}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var header = new HashMap<String, String>();
        header.put("problem", "Потребность (проблема)");
        header.put("solution", "Ваше УТП (решение проблемы)");

        rawTable.add(0, header);

        var rawByResponse = new HashMap<String, List<HashMap<String, String>>>();
        rawByResponse.put("Response", rawTable);

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(rawByResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;

/*
        var responseArray = rawContent.split("\n");
        var answersByRows = new HashMap<Integer, String[]>();

        answersByRows.put(0, new String[] {"Потребность (проблема)", "Ваше УТП (решение проблемы)"});
        for (var i = 2; i < responseArray.length; i++){
            var curLine = responseArray[i];
            if (curLine.isEmpty())
                break;
            var splitedCurLine = curLine.split("\\|");
            var problem = splitedCurLine[1];
            var answer = splitedCurLine[2];
            answersByRows.put(i - 1, new String[] {problem, answer});
        }

        String res2 = null;
        try {
            res2 = new ObjectMapper().writeValueAsString(answersByRows);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res2;*/
    }

}
