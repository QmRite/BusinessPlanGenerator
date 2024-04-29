package ru.urfu.service.impl.docServices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.urfu.entity.enums.PlanChapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Log4j
public class MarketingDocService extends AbstractDocService{
    @Override
    public String getPlanChapter() {
        return PlanChapter.MARKETING.toString();
    }

    @Override
    public byte[] createPlanChapterDoc(JSONObject content) throws InvalidFormatException, IOException {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph tmpParagraph = doc.createParagraph();
        fillTitle(tmpParagraph, "3. Маркетинговый план:");

        fillQuestionAndAnswer(tmpParagraph,
                "Как и кому будет реализовываться товар, производиться услуга или продукция",
                content.getString("Realisation"));

        fillQuestionAndAnswer(tmpParagraph,
                "Кто целевая аудитория",
                content.getString("Target"));

        fillQuestionAndAnswer(tmpParagraph,
                "Каков спрос на рынке на данный товар (услугу)",
                content.getString("Demand"));

        XWPFParagraph competitorsParagraph = doc.createParagraph();
        fillQuestion(competitorsParagraph, "Кто из конкурентов и как предлагает аналогичный товар (услугу) данной целевой аудитории.", false);
        createTableWithTitleByJson(doc, content, "CompetitorsTable", "Анализ конкурентов");

        doc.createParagraph().createRun().addBreak();

        XWPFParagraph marketingParagraph = doc.createParagraph();
        fillQuestion(marketingParagraph, "Как, какими средствами будет продвигаться товар (услуга) на рынке", false);
        createTableWithTitleByJson(doc, content, "MarketingTable", "Бюджет маркетинга");

        ByteArrayOutputStream binOut = new ByteArrayOutputStream();
        doc.write(binOut);

        return binOut.toByteArray();
    }
}
