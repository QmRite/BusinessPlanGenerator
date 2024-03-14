package ru.urfu.utils.RequestTextGenerators.Factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.urfu.utils.RequestTextGenerators.AbstractRequestTextGenerator;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RequestTextFactory {

    @Autowired
    private List<AbstractRequestTextGenerator> generators;

    private static final Map<String, AbstractRequestTextGenerator> generatorsCache = new HashMap<>();

    @PostConstruct
    public void initRequestTextCache() {
        for(AbstractRequestTextGenerator service : generators) {
            generatorsCache.put(service.getPlanChapter(), service);
        }
    }

    public static AbstractRequestTextGenerator getRequestText(String type) {
        AbstractRequestTextGenerator requestText = generatorsCache.get(type.toLowerCase());
        if(requestText == null)
            throw new RuntimeException("Unknown request text generator type: " + type);
        return requestText;
    }
}
