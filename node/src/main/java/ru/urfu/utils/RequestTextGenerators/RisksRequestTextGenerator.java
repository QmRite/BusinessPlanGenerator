package ru.urfu.utils.RequestTextGenerators;

import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.List;

@Component
public class RisksRequestTextGenerator extends AbstractRequestTextGenerator {
    RisksRequestTextGenerator() {
        super(List.of("Суть проекта (чем будете заниматься)",
                "Сфера деятельности бизнеса",
                "Место реализации",
                "Какие риски и внешние условия стоит учитывать"));
    }

    @Override
    public String getPlanChapter() {
        return PlanChapter.RISKS.toString();
    }

    @Override
    public String getRequestText(String[] answers) {
        var template = "Выступай в качестве эксперта по бизнесу и напиши ответ на вопрос для для бизнеса. Отвечай развернуто. Ответ выдай в формате JSON в виде массива объектов с полями risk, minimization. Размер массива: от 5 до 10 объектов. Пример: Финансовые риски, падение спроса, проблемы с поставщиками, конкуренция, технические сбои, законодательство, безопасность, поломка оборудования. Прочитай текст в тегах info и отвечай подробно на его основе. [info] Информация о бизнесе: Суть проекта: [1] Сфера бизнеса: [2] Место реализации: [3] Потенциальные угрозы для бизнеса: [4] Подумай еще потенциальные риски [/info] Отвечай на основе информации о бизнесе. Ответ выдай в формате JSON в виде массива объектов с полями risk, minimization. Размер массива: 10 объектов.";
        var requestText = template;
        for(var i = 0; i < answers.length; i++){
            var oldChar = String.format("[%d]", i + 1);
            requestText = requestText.replace(oldChar, answers[i]);
        }
        return requestText;
    }

}
