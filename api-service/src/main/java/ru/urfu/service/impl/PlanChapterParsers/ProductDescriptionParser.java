package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.urfu.service.enums.PlanChapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProductDescriptionParser extends AbstractPlanChapterParser {
    @Override
    public String getPlanChapter() {
        return PlanChapter.PRODUCT_DESCRIPTION.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {

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

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(answersByRows);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}
