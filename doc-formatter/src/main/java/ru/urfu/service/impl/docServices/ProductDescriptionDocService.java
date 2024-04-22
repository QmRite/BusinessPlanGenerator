package ru.urfu.service.impl.docServices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.urfu.entity.enums.PlanChapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j
public class ProductDescriptionDocService extends AbstractDocService{
    @Override
    public String getPlanChapter() {
        return PlanChapter.PRODUCT_DESCRIPTION.toString();
    }

    @Override
    public byte[] createPlanChapterDoc(JSONObject content) throws InvalidFormatException, IOException {
        var products = content.getString("Products");
        var tableInfo = content.getJSONArray("Table").toList().stream()
                .map(o -> new ObjectMapper().convertValue(o, new TypeReference<Map<String, String>>() {}))
                .map(h -> new ArrayList<>(h.values()))
                .collect(Collectors.toCollection(ArrayList::new));

        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph tmpParagraph = doc.createParagraph();
        fillTitle(tmpParagraph, "2. Описание продукта:");
        fillQuestionAndAnswer(tmpParagraph, "Какие товары (услуги) планируется реализовывать (их ключевые характеристики)", products);

        fillQuestion(tmpParagraph, "В чём их ценность для потребителя (уникальное торговое предложение");
        XWPFTable table = doc.createTable(tableInfo.size(), 2);
        fillTable(table, tableInfo);
        table.setWidth("100%");

        ByteArrayOutputStream binOut = new ByteArrayOutputStream();
        doc.write(binOut);

        return binOut.toByteArray();

    }
}
