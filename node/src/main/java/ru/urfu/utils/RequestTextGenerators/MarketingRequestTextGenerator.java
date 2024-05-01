package ru.urfu.utils.RequestTextGenerators;

import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.List;

@Component
public class MarketingRequestTextGenerator extends AbstractRequestTextGenerator {
    MarketingRequestTextGenerator() {
        super(List.of("Суть проекта (чем будете заниматься)",
                "Сфера деятельности бизнеса",
                "Место реализации",
                "Как и кому будет реализовываться товар, производиться услуга или продукция",
                "Кто целевая аудитория",
                "Каков спрос на рынке на данный товар (услугу)",
                "Кто из конкурентов и как предлагает аналогичный товар (услугу) данной целевой аудитории?\nОтвет через запятую в формате: «Компания 1, компания 2»",
                "Бюджет на маркетинг"));
    }

    @Override
    public String getPlanChapter() {
        return PlanChapter.MARKETING.toString();
    }

    @Override
    public String getRequestText(String[] answers) {
        var template = "Выступай в качестве эксперта по бизнесу и напиши ответ на вопрос для для бизнеса. Отвечай развернуто. Ответ выдай в формате JSON в виде массива объектов с полями marketing, cost. Marketing - string, cost - integer. Размер массива: от 5 до 10 объектов. В ответе должны быть варианты продвижения продукта. Прочитай текст в тегах info и отвечай подробно на его основе. [info] Информация о бизнесе: Суть проекта: [1] Сфера бизнеса: [2] Место реализации: [3] Способ реализации товара: [4] Целевая аудитория: [5] Спрос на рынке на данный товар: [6] Конкуренты: [7] Максимальный бюджет: [8] [/info] cost не должна превышать максимальный бюджет.";
        var requestText = template;
        for(var i = 0; i < answers.length; i++){
            var oldChar = String.format("[%d]", i + 1);
            requestText = requestText.replace(oldChar, answers[i]);
        }
        return requestText;
    }

}
