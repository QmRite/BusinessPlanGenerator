package ru.urfu.service.impl.PlanChapterParsers.Factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.urfu.service.impl.PlanChapterParsers.AbstractPlanChapterParser;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlanChapterFactory {

    @Autowired
    private List<AbstractPlanChapterParser> parsers;

    private static final Map<String, AbstractPlanChapterParser> parserCache = new HashMap<>();

    @PostConstruct
    public void initParserCache() {
        for(AbstractPlanChapterParser service : parsers) {
            parserCache.put(service.getPlanChapter(), service);
        }
    }

    public static AbstractPlanChapterParser getParser(String type) {
        AbstractPlanChapterParser parser = parserCache.get(type.toLowerCase());
        if(parser == null)
            throw new RuntimeException("Unknown parser type: " + type);
        return parser;
    }
}
