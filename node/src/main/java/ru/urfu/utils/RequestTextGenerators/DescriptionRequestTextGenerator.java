package ru.urfu.utils.RequestTextGenerators;

import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.List;

@Component
public class DescriptionRequestTextGenerator extends AbstractRequestTextGenerator {
    DescriptionRequestTextGenerator() {
        super(List.of("Суть проекта (чем будете заниматься)",
                "Место реализации",
                "Сфера деятельности бизнеса",
                "Количество создаваемых рабочих мест в первый год деятельности"));
    }

    @Override
    public String getPlanChapter() {
        return PlanChapter.DESCRIPTION.toString();
    }

    @Override
    public String getRequestText(String[] answers) {
        var template = "Выступай в качестве эксперта по составлению бизнес-планов и напиши ответы на вопросы для бизнес-плана для бизнеса [1] в виде таблицы. Таблица должна содержать 2 столбика и 6 строк. Первый столбик - вопросы, второй - ответы. Информация о бизнесе: [2] Суть проекта: [3] Место реализации: [4] Вид экономической деятельности: [5] Количество создаваемых рабочих мест в первый год деятельности: [6] Отвечай коротко. Не более 200 символов на каждый пункт. Отвечай на основе информации о бизнесе. Составь таблицу. Первый столбик - вопросы, второй - ответы";
        var requestText = template;
        for(var i = 0; i < answers.length; i++){
            var oldChar = String.format("[%d]", i + 1);
            requestText = requestText.replace(oldChar, answers[i]);
        }
        return requestText;
    }

}
