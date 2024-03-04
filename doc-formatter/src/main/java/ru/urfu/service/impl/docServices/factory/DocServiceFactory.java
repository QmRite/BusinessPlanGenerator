package ru.urfu.service.impl.docServices.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.urfu.service.impl.docServices.AbstractDocService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocServiceFactory {

    @Autowired
    private List<AbstractDocService> services;

    private static final Map<String, AbstractDocService> serviceCache = new HashMap<>();

    @PostConstruct
    public void initServiceCache() {
        for(AbstractDocService service : services) {
            serviceCache.put(service.getPlanChapter(), service);
        }
    }

    public static AbstractDocService getDocService(String type) {
        AbstractDocService service = serviceCache.get(type.toLowerCase());
        if(service == null)
            throw new RuntimeException("Unknown doc service type: " + type);
        return service;
    }
}
