package ru.urfu.utils.RequestTextGenerators;

import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.List;

@Component
public class ProductDescriptionRequestTextGenerator extends AbstractRequestTextGenerator {
    ProductDescriptionRequestTextGenerator() {
        super(List.of("Суть проекта (чем будете заниматься)",
                "Сфера деятельности бизнеса",
                "Место реализации",
                "Целевая аудитория проекта",
                "Какие товары (услуги) планируется реализовывать (их ключевые характеристики)"));
    }

    @Override
    public String getPlanChapter() {
        return PlanChapter.PRODUCT_DESCRIPTION.toString();
    }

    @Override
    public String getRequestText(String[] answers) {
        var template = "Выступай в качестве эксперта по бизнесу и напиши ответ на вопрос для для бизнеса. Отвечай развернуто. Ответ выдай в формате JSON в виде массива объектов с полями problem, solution. Не больше 10 элементов. Информация о бизнесе: Суть проекта: [1] Сфера бизнеса: [2] Место реализации: [3] Целевая аудитория: [4] Товары: [5]";
        var requestText = template;
        for(var i = 0; i < answers.length; i++){
            var oldChar = String.format("[%d]", i + 1);
            requestText = requestText.replace(oldChar, answers[i]);
        }
        return requestText;
    }

}
