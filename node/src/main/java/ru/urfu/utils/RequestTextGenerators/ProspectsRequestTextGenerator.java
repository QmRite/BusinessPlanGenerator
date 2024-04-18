package ru.urfu.utils.RequestTextGenerators;

import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.List;

@Component
public class ProspectsRequestTextGenerator extends AbstractRequestTextGenerator {
    ProspectsRequestTextGenerator() {
        super(List.of("Сфера деятельности бизнеса",
                "Место реализации",
                "Планируемое количество сотрудников",
                "Планируемый доход в первый год деятельности",
                "Планируемая маркетинговая стратегия"));
    }

    @Override
    public String getPlanChapter() {
        return PlanChapter.PROSPECTS.toString();
    }

    @Override
    public String getRequestText(String[] answers) {
        var template = "Ты — эксперт по составлению бизнес-планов. Ответ должен быть менее 700 символов. Ответ в виде текста. Учитывай тенденции развития рынка. Можешь предлагать повышение квалификации. Изучи различные точки роста. Информация о бизнесе: Сфера деятельности: [1] Место реализации: [2] Кол-во сотрудников: [3] Годовой  доход компании на данный момент: [4] Рекламная стратегия: [5] Ответ должен быть менее 700 символов";
        var requestText = template;
        for(var i = 0; i < answers.length; i++){
            var oldChar = String.format("[%d]", i + 1);
            requestText = requestText.replace(oldChar, answers[i]);
        }
        return requestText;
    }

}
