package ru.urfu.service.impl.docServices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Log4j
public abstract class AbstractDocService {
    public abstract String getPlanChapter();
    public abstract byte[] createPlanChapterDoc(JSONObject content) throws InvalidFormatException, IOException;

    public FileSystemResource getFileSystemResource(byte[] binaryContent) {
        try {
            //TODO добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent);
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }

    protected void fillTable(XWPFTable table, ArrayList<ArrayList<String>> valuesList) {
        var tableRows = table.getRows();
        for (var i = 0; i < valuesList.size(); i++){
            fillRow(tableRows.get(i), valuesList.get(i));
        }
    }

    protected void fillRow(XWPFTableRow row, ArrayList<String> values) {
        List<XWPFTableCell> cellsList = row.getTableCells();

        for (var i = 0; i < values.size(); i++){
            var currentCel = cellsList.get(i);
            currentCel.removeParagraph(0);
            fillAnswer(currentCel.addParagraph(), values.get(i), false);
        }
        row.setHeightRule(TableRowHeightRule.AUTO);
    }

    protected void fillTitle(XWPFParagraph paragraph, String value) {
        fillTitle(paragraph, value, true);
    }

    protected void fillTitle(XWPFParagraph paragraph, String value, boolean addBreak) {
        XWPFRun runQuestion = paragraph.createRun();
        setDefaultFilling(runQuestion);
        runQuestion.setBold(true);
        runQuestion.setText(value);
        if (addBreak)
            runQuestion.addBreak();
    }


    protected void fillQuestionAndAnswer(XWPFParagraph paragraph, String questionText, String answerText) {
        fillQuestion(paragraph, questionText);
        fillAnswer(paragraph, answerText);
    }

    protected void fillQuestion(XWPFParagraph paragraph, String value) {
        fillQuestion(paragraph, value, true);
    }

    protected void fillQuestion(XWPFParagraph paragraph, String value, Boolean addBreak) {
        XWPFRun runQuestion = paragraph.createRun();
        setDefaultFilling(runQuestion);
        runQuestion.setItalic(true);
        runQuestion.setText(value);
        if (addBreak)
            runQuestion.addBreak();
    }

    protected void fillAnswer(XWPFParagraph paragraph, String value) {
        fillAnswer(paragraph, value, true);
    }

    protected void fillAnswer(XWPFParagraph paragraph, String value, Boolean addBreak) {
        XWPFRun runQuestion = paragraph.createRun();
        setDefaultFilling(runQuestion);
        runQuestion.setText(value);
        if (addBreak)
            runQuestion.addBreak();
    }

    protected void setDefaultFilling(XWPFRun run){
        run.setFontSize(12);
        run.setFontFamily("Liberation Serif");
    }


    protected void createTableWithTitleByJson(XWPFDocument doc, JSONObject content, String tableName, String tableTitle) {
        XWPFParagraph tableParagraph = doc.createParagraph();
        tableParagraph.setAlignment(ParagraphAlignment.CENTER);
        fillTitle(tableParagraph, tableTitle, false);
        createTableByJson(doc, content, tableName);
    }

    protected void createTableByJson(XWPFDocument doc, JSONObject content, String tableName){
        var marketingTableInfo = content.getJSONArray(tableName).toList().stream()
                .map(o -> new ObjectMapper().convertValue(o, new TypeReference<TreeMap<String, String>>() {}))
                .map(h -> new ArrayList<>(h.values()))
                .collect(Collectors.toCollection(ArrayList::new));

        XWPFTable marketingTable = doc.createTable(marketingTableInfo.size(), marketingTableInfo.get(0).size());
        fillTable(marketingTable, marketingTableInfo);
        marketingTable.setWidth("100%");
    }
}
