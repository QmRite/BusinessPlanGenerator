package ru.urfu.service.impl.docServices;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Service;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.json.JSONObject;
import ru.urfu.entity.enums.PlanChapter;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j
public class DescriptionDocService extends AbstractDocService{
    @Override
    public String getPlanChapter() {
        return PlanChapter.DESCRIPTION.toString();
    }

    @Override
    public byte[] createPlanChapterDoc(JSONObject content) throws InvalidFormatException, IOException {

        Map<String, String> contentByPlaceHolder = new HashMap(content.toMap());

        var templateFile = "C:\\Users\\user\\IdeaProjects\\BusinessPlanGenerator\\doc-formatter\\src\\main\\resources\\DescriptionTemplate.docx";

        XWPFDocument doc = new XWPFDocument(OPCPackage.open(templateFile));
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                var a = runs.toString();
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    var expression = Pattern.compile("\\[(.*?)\\]");

                    if (text != null){
                        Matcher expressionMatcher = expression.matcher(text);
                        if (expressionMatcher.find() && expressionMatcher.groupCount() > 0) {
                            var PlaceHolderName = expressionMatcher.group(1);
                            if (contentByPlaceHolder.containsKey(PlaceHolderName)) {
                                text = text.replace(text, contentByPlaceHolder.get(PlaceHolderName));
                                r.setText(text, 0);
                            }
                        }
                    }
                }
            }
        }

        ByteArrayOutputStream binOut = new ByteArrayOutputStream();
        doc.write(binOut);

        return binOut.toByteArray();
    }

}
