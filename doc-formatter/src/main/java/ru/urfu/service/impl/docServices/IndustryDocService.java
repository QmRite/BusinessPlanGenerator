package ru.urfu.service.impl.docServices;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.urfu.entity.enums.PlanChapter;

import java.io.IOException;

@Service
@Log4j
public class IndustryDocService extends AbstractDocService{
    @Override
    public String getPlanChapter() {
        return PlanChapter.INDUSTRY.toString();
    }

    @Override
    public byte[] createPlanChapterDoc(JSONObject content) throws InvalidFormatException, IOException {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph processParagraph = doc.createParagraph();
        fillTitle(processParagraph, "4. Производственный план:");

        fillQuestion(processParagraph,"Каков технологический процесс производства товара (оказания услуги), какие ресурсы необходимы для организации бизнеса (помещение, сырьё, материалы, поставщики, технологии, ПО и т.д.)",
                false);
        createTableByJson(doc, content, "ProcessTable");
        doc.createParagraph().createRun().addBreak();

        var expensesParagraph = doc.createParagraph();
        fillQuestion(expensesParagraph, "Какое необходимо приобрести оборудование (в том числе за счёт субсидии)",
                false);
        createTableWithTitleByJson(doc, content,
                "ExpensesTable",
                "Направления расходования средств");
        doc.createParagraph().createRun().addBreak();

        var calendarParagraph = doc.createParagraph();
        fillQuestion(calendarParagraph, "Какие действия на старте необходимо предпринять для достижения поставленной цели (организационные планы)");
        fillQuestion(calendarParagraph, "Организационные планы можно представить в таблице.",
                false);
        createTableWithTitleByJson(doc, content,
                "CalendarTable",
                "Организационный план");

        ByteArrayOutputStream binOut = new ByteArrayOutputStream();
        doc.write(binOut);

        return binOut.toByteArray();
    }
}
