package ru.urfu.utils.RequestTextGenerators;

import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.List;

@Component
public class FinanceRequestTextGenerator extends AbstractRequestTextGenerator {
    FinanceRequestTextGenerator() {
        super(List.of("Суть проекта (чем будете заниматься)",
                "Сфера деятельности бизнеса",
                "Место реализации",
                "Планы компании на обозримое будущее",
                "Отделы, необходимые для работы компании",
                "Ожидаемая средняя выручка (в ответе только число)",
                "Какая сумма и на что понадобится на старте"));
    }

    @Override
    public String getPlanChapter() {
        return PlanChapter.FINANCE.toString();
    }

    @Override
    public String getRequestText(String[] answers) {
        var template = "Информация о бизнесе: " +
                "Суть проекта: [1] " +
                "Сфера бизнеса: [2] " +
                "Место реализации: [3] " +
                "Планы компании: [4] " +
                "Отделы: [5] " +
                "Выручка: [6] " +
                "Бюджет на старте: [7]";
        var requestText = template;
        for(var i = 0; i < answers.length; i++){
            var oldChar = String.format("[%d]", i + 1);
            requestText = requestText.replace(oldChar, answers[i]);
        }
        return requestText;
    }

}
