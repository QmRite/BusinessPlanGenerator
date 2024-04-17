package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.HashMap;

@Component
public class ProspectsParser extends AbstractPlanChapterParser {
    @Override
    public String getPlanChapter() {
        return PlanChapter.PROSPECTS.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {
        var answersByPlaceHolder = new HashMap<String, String>();
        answersByPlaceHolder.put("PROSPECTS", rawContent);

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(answersByPlaceHolder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}
