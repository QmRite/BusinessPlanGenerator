package ru.urfu.service.impl.PlanChapterParsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FinanceParser extends AbstractPlanChapterParser {

    public static final String NAME = "0name";

    @Override
    public String getPlanChapter() {
        return PlanChapter.FINANCE.toString();
    }

    @Override
    public String getParsedContentJSON(String rawContent) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public String getParsedContentJSON(String rawContent, String requestText) {
        throw new NotImplementedException("not implemented");
    }


    public String getParsedContentJSON(String costContent, String investmentsContent, String requestText) {
        var rawByResponse = new HashMap<String, Object>();

        var startBudget = getSubstring(requestText, "Бюджет на старте:");
        rawByResponse.put("StartBudget", startBudget);

        var rawCostContentJson = getJsonFromResponseText(costContent);
        rawCostContentJson = rawCostContentJson.replaceAll("name", NAME);
        var costsTable = getCostsTable(rawCostContentJson);
        rawByResponse.put("CostsTable", costsTable);

        var incomeTable = getIncomeTable(requestText, costsTable);
        rawByResponse.put("IncomeTable", incomeTable);

        var rawInvestmentsContentJson = getJsonFromResponseText(investmentsContent);
        rawInvestmentsContentJson = rawInvestmentsContentJson.replaceAll("name", NAME);
        var investmentsTable = getInvestmentsTable(rawInvestmentsContentJson);
        rawByResponse.put("InvestmentsTable", investmentsTable);

        String res = null;
        try {
            res = new ObjectMapper().writeValueAsString(rawByResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private List<TreeMap<String, String>> getIncomeTable(String requestText, List<TreeMap<String, String>> costsTable) {
        var incomeTable = new ArrayList<TreeMap<String, String>>();
        incomeTable.add(getIncomeHeader());

        var revenue = getSubstring(requestText, "Выручка:", "Бюджет на старте:");
        var revenueInt = Integer.parseInt(revenue);
        var revenueRow = new TreeMap<String, String>();
        revenueRow.put("0name", "Выручка");
        for (var i = 1; i < 13; i++){
            revenueRow.put(String.valueOf(i) , revenue);
        }
        incomeTable.add(revenueRow);

        var tax = revenueInt * 0.06;
        var taxRow = new TreeMap<String, String>();
        taxRow.put("0name", "Налог на доход");
        for (var i = 1; i < 13; i++){
            taxRow.put(String.valueOf(i) , String.format("%.2f", tax));
        }
        incomeTable.add(taxRow);

        var costsSum = costsTable.get(costsTable.size() - 1);
        var costsSumInt = Integer.parseInt(costsSum.get("1"));
        var costsRow = new TreeMap<String, String>();
        costsRow.put("0name", "Издержки");
        for (var i = 1; i < 13; i++){
            costsRow.put(String.valueOf(i) , String.valueOf(costsSumInt));
        }
        incomeTable.add(costsRow);

        var income = revenueInt - tax - costsSumInt;
        var incomeRow = new TreeMap<String, String>();
        incomeRow.put("0name", "Чистая прибыль за вычетом налогов");
        for (var i = 1; i < 13; i++){
            incomeRow.put(String.valueOf(i) , String.format("%.2f", income));
        }
        incomeTable.add(incomeRow);

        var incomeGrowing = 0.0;
        var incomeGrowingRow = new TreeMap<String, String>();
        for (var key : incomeRow.keySet()){
            if (key.equalsIgnoreCase("0name")){
                incomeGrowingRow.put(key, "Чистая прибыль нарастающим итогом");
                continue;
            }
            incomeGrowing+=Double.parseDouble(incomeRow.get(key));
            incomeGrowingRow.put(key, String.format("%.2f", incomeGrowing));
        }
        incomeTable.add(incomeGrowingRow);

        return incomeTable;
    }

    private static TreeMap<String, String> getIncomeHeader() {
        var costHeader = new TreeMap<String, String>();
        costHeader.put("0name", "Месяцы");
        return getMonthsTable(costHeader);
    }

    private static TreeMap<String, String> getMonthsTable(TreeMap<String, String> costHeader) {
        costHeader.put("1", "1");
        costHeader.put("10", "2");
        costHeader.put("11", "3");
        costHeader.put("12", "4");
        costHeader.put("2", "5");
        costHeader.put("3", "6");
        costHeader.put("4", "7");
        costHeader.put("5", "8");
        costHeader.put("6", "9");
        costHeader.put("7", "10");
        costHeader.put("8", "11");
        costHeader.put("9", "12");
        return costHeader;
    }

    private static List<TreeMap<String, String>> getInvestmentsTable(String rawContent) {
        ObjectMapper mapper = new ObjectMapper();

        List<TreeMap<String, String>> rawTable = null;
        try {
            rawTable = mapper.readValue(
                    rawContent, new TypeReference<>() {
                    }
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        var totalRow = new TreeMap<String, String>();
        totalRow.put("1own", "0");
        totalRow.put("2grant", "0");
        totalRow.put("3borrowed", "0");
        totalRow.put("4sum", "0");
        totalRow.put(NAME, "Итого инвестиционных затрат ");

        var investmentsTable = new ArrayList<TreeMap<String, String>>();

        var preliminaryInvestTable = rawTable.stream()
                .filter(n -> n.get("type").equalsIgnoreCase("Предварительный"))
                .collect(Collectors.toList());
        FillInvestsTablePart(investmentsTable, preliminaryInvestTable, totalRow, "1. Предварительные расходы", "Итого предварительных расходов");

        var basicInvestTable = rawTable.stream()
                .filter(n -> n.get("type").equalsIgnoreCase("Основной"))
                .collect(Collectors.toList());
        FillInvestsTablePart(investmentsTable, basicInvestTable, totalRow, "2. Основные средства", "Итого основных средств");

        var negotiableInvestTable = rawTable.stream()
                .filter(n -> n.get("type").equalsIgnoreCase("Оборотный"))
                .collect(Collectors.toList());
        FillInvestsTablePart(investmentsTable, negotiableInvestTable, totalRow, "3. Оборотные средства", "Итого оборотных средств");

        investmentsTable.add(totalRow);
        var header = new TreeMap<String, String>();
        header.put("0name", "Статьи");
        header.put("1own", "Собственные средства");
        header.put("2grant", "Другие инвестиции");
        header.put("3borrowed", "Заемные средства");
        header.put("4sum", "Сумма");
        investmentsTable.add(0, header);

        return investmentsTable;
    }

    private static void FillInvestsTablePart(List<TreeMap<String, String>>investmentsTable, List<TreeMap<String, String>> rawTable,
                                             Map<String, String> totalRow, String headerName, String footerName){

        var header = new TreeMap<String, String>();
        header.put("0name", headerName);
        header.put("1own", "");
        header.put("2grant", "");
        header.put("3borrowed", "");
        header.put("4sum", "");
        investmentsTable.add(header);

        var ownSum = 0;
        var grantSum = 0;
        var borrowedSum = 0;
        var totalSum = 0;
        for (var rawRow : rawTable){
            var row = new TreeMap<String, String>();
            row.put(NAME, rawRow.get(NAME));

            var cost = rawRow.get("cost");
            var costInt = Integer.parseInt(cost);
            totalSum += costInt;
            var investmentType = rawRow.get("investment");

            row.put("1own", investmentType.equalsIgnoreCase("Собственные") ? cost : "");
            ownSum += investmentType.equalsIgnoreCase("Собственные") ? costInt : 0;

            row.put("2grant", investmentType.equalsIgnoreCase("Грант") ? cost : "");
            grantSum += investmentType.equalsIgnoreCase("Грант") ? costInt : 0;

            row.put("3borrowed", investmentType.equalsIgnoreCase("Заемные") ? cost : "");
            borrowedSum += investmentType.equalsIgnoreCase("Заемные") ? costInt : 0;
            row.put("4sum", cost);

            investmentsTable.add(row);
        }
        var footer = new TreeMap<String, String>();
        footer.put(NAME, footerName);
        footer.put("1own", String.valueOf(ownSum));
        footer.put("2grant", String.valueOf(grantSum));
        footer.put("3borrowed", String.valueOf(borrowedSum));
        footer.put("4sum", String.valueOf(totalSum));
        investmentsTable.add(footer);

        totalRow.put("1own", String.valueOf(ownSum + Integer.parseInt(totalRow.get("1own"))));
        totalRow.put("2grant", String.valueOf(grantSum + Integer.parseInt(totalRow.get("2grant"))));
        totalRow.put("3borrowed", String.valueOf(borrowedSum + Integer.parseInt(totalRow.get("3borrowed"))));
        totalRow.put("4sum", String.valueOf(totalSum + Integer.parseInt(totalRow.get("4sum"))));
    }

    private static List<TreeMap<String, String>> getCostsTable(String rawContent) {
        ObjectMapper mapper = new ObjectMapper();

        List<TreeMap<String, String>> rawTable = null;
        try {
           rawTable = mapper.readValue(
                   rawContent, new TypeReference<>() {
                   }
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Map<Boolean, List<TreeMap<String, String>>> isCostConst=
                rawTable.stream().collect(Collectors.partitioningBy(n -> n.get("type").equalsIgnoreCase("Постоянная")));

        ArrayList<TreeMap<String, String>> costsTable = new ArrayList<TreeMap<String, String>>();
        var costHeader = getCostHeader();
        costsTable.add(0, costHeader);

        var constHeader = getCostSubHeader("Постоянные издержки");
        costsTable.add(constHeader);
        var totalSum = 0;
        for (var rawRow : isCostConst.get(Boolean.TRUE)){
            totalSum = getTotalSum(costsTable, totalSum, rawRow);
        }

        var varHeader = getCostSubHeader("Переменные издержки");
        costsTable.add(varHeader);
        for (var rawRow : isCostConst.get(Boolean.FALSE)){
            totalSum = getTotalSum(costsTable, totalSum, rawRow);
        }

        var footer = new TreeMap<String, String>();
        footer.put("0name", "ИТОГО");
        for (var i = 1; i < 13; i++){
            footer.put(String.valueOf(i) , String.valueOf(totalSum));
        }
        costsTable.add(footer);

        return costsTable;
    }

    private static int getTotalSum(ArrayList<TreeMap<String, String>> costsTable, int totalSum, TreeMap<String, String> rawRow) {
        var costVarRow = new TreeMap<String, String>();
        costVarRow.put(NAME, rawRow.get(NAME));

        var costString = rawRow.get("cost");
        int costInt = Integer.parseInt(costString) / 2;
        totalSum += costInt;
        for (var i = 1; i < 13; i++){
            costVarRow.put(String.valueOf(i) , String.valueOf(costInt));
        }
        costsTable.add(costVarRow);
        return totalSum;
    }

    private static TreeMap<String, String> getCostHeader() {
        var costHeader = new TreeMap<String, String>();
        costHeader.put("0name", "Издержки/месяцы");
        return getMonthsTable(costHeader);
    }

    private static TreeMap<String, String> getCostSubHeader(String name) {
        var costHeader = new TreeMap<String, String>();
        costHeader.put("0name", name);
        costHeader.put("1", "");
        costHeader.put("10", "");
        costHeader.put("11", "");
        costHeader.put("12", "");
        costHeader.put("2", "");
        costHeader.put("3", "");
        costHeader.put("4", "");
        costHeader.put("5", "");
        costHeader.put("6", "");
        costHeader.put("7", "");
        costHeader.put("8", "");
        costHeader.put("9", "");
        return costHeader;
    }

}
