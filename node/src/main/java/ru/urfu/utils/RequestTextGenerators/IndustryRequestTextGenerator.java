package ru.urfu.utils.RequestTextGenerators;

import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.List;

@Component
public class IndustryRequestTextGenerator extends AbstractRequestTextGenerator {
    IndustryRequestTextGenerator() {
        super(List.of("Суть проекта (чем будете заниматься)",
                "Сфера деятельности бизнеса",
                "Какие товары (услуги) планируется реализовывать?",
                "Место реализации",
                "Цели и стратегические планы компании",
                "Как будет реализовываться товар, производиться услуга или продукция",
                "Кто целевая аудитория",
                "Какая сумма и понадобится на старте (в ответе только число)"));
    }

    @Override
    public String getPlanChapter() {
        return PlanChapter.INDUSTRY.toString();
    }

    @Override
    public String getRequestText(String[] answers) {
        var template = "Информация о бизнесе: Суть проекта: [1] Сфера бизнеса: [2] Товары: [3] Место реализации: [4] Цель: [5] Способ реализации товара: [6] Целевая аудитория: [7] Максимальный бюджет: [8]";
        var requestText = template;
        for(var i = 0; i < answers.length; i++){
            var oldChar = String.format("[%d]", i + 1);
            requestText = requestText.replace(oldChar, answers[i]);
        }
        return requestText;
    }

}
