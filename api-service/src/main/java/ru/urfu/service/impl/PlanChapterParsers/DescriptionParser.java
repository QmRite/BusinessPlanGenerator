package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.HashMap;

@Component
public class DescriptionParser extends AbstractPlanChapterParser {
    @Override
    public String getPlanChapter() {
        return PlanChapter.DESCRIPTION.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {

        //TODO поменять. Скорее всего сделать фабрику
        var textRows = parseTable(rawContent);

        var placeHolders = new String[] {"LINE_OF_BUSINESS", "REALISTIC_AND_SUCCESSFUL", "TASKS", "NEED", "ORG_FORM_TAXES", "PROBLEMS"};

        var answersByPlaceHolder = new HashMap<String, String>();
        for (var i = 0; i < placeHolders.length; i++){
            answersByPlaceHolder.put(placeHolders[i], textRows[i]);
        }

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(answersByPlaceHolder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}
