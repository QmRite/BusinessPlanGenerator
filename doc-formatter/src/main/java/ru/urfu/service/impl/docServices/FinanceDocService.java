package ru.urfu.service.impl.docServices;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.stereotype.Service;
import ru.urfu.entity.enums.PlanChapter;

import java.io.IOException;
import java.math.BigInteger;

@Service
@Log4j
public class FinanceDocService extends AbstractDocService{
    @Override
    public String getPlanChapter() {
        return PlanChapter.FINANCE.toString();
    }

    @Override
    public byte[] createPlanChapterDoc(JSONObject content) throws InvalidFormatException, IOException {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph processParagraph = doc.createParagraph();
        fillTitle(processParagraph, "6. Финансы и инвестиции:");

        fillQuestionAndAnswer(processParagraph,"Какая сумма и на что понадобится на старте (в том числе средства субсидии)?",
                content.getString("StartBudget"));

        fillQuestion(processParagraph, "Из чего будут складываться постоянные и переменные ежемесячные расходы",
                false);
        createTableWithTitleByJson(doc, content,
                "CostsTable",
                "Расчёт издержек помесячно в первый год деятельности, тыс. руб.",
                "75%");
        doc.createParagraph().createRun().addBreak();

        var incomeParagraph = doc.createParagraph();
        fillQuestion(incomeParagraph, "Объем чистой прибыли за год",
                false);
        createTableWithTitleByJson(doc, content,
                "IncomeTable",
                "Расчет чистой прибыли помесячно в первый год деятельности, тыс. руб.",
                "50%");
        doc.createParagraph().createRun().addBreak();

        //var investmentsParagraph = doc.createParagraph();
        createTableWithTitleByJson(doc, content,
                "InvestmentsTable",
                "Расчет инвестиционных затрат, необходимых для запуска бизнеса",
                "100%");
        doc.createParagraph().createRun().addBreak();

        ByteArrayOutputStream binOut = new ByteArrayOutputStream();
        doc.write(binOut);

        return binOut.toByteArray();
    }
}
