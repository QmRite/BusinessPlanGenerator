package ru.urfu.service.impl.docServices;

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

@Service
@Log4j
public class ProductDescriptionDocService extends AbstractDocService{
    @Override
    public String getPlanChapter() {
        return PlanChapter.PRODUCT_DESCRIPTION.toString();
    }

    @Override
    public byte[] createPlanChapterDoc(JSONObject content) throws InvalidFormatException, IOException {
        HashMap<String, ArrayList<String>> rowsContentByNumber = new HashMap(content.toMap());
        var rowsContent =  new ArrayList<>(rowsContentByNumber.values());

        XWPFDocument doc = new XWPFDocument();

        XWPFParagraph tmpParagraph = doc.createParagraph();
        fillTitle(tmpParagraph, "2. Описание продукта:");
        fillQuestionAndAnswer(tmpParagraph, "Какие товары (услуги) планируется реализовывать (их ключевые характеристики)", "оТВЕТ");
        fillQuestion(tmpParagraph, "В чём их ценность для потребителя (уникальное торговое предложение");
        XWPFTable table = doc.createTable(rowsContent.size(), 2);
        fillTable(table, rowsContent);
        table.setWidth("100%");

        ByteArrayOutputStream binOut = new ByteArrayOutputStream();
        doc.write(binOut);

        return binOut.toByteArray();

    }
}
