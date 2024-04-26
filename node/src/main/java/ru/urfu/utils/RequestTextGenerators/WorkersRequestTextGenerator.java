package ru.urfu.utils.RequestTextGenerators;

import org.springframework.stereotype.Component;
import ru.urfu.entity.enums.PlanChapter;

import java.util.List;

@Component
public class WorkersRequestTextGenerator extends AbstractRequestTextGenerator {
    WorkersRequestTextGenerator() {
        super(List.of("Суть проекта (чем будете заниматься)",
                "Сфера деятельности бизнеса",
                "Место реализации",
                "Цели и стратегические планы компании",
                "Какие навыки и компетенции требуются для реализации бизнес-идеи?",
                "Какие отделы будут необходимы для эффективного функционирования бизнеса?",
                "Бюджет найма персонала"));
    }

    @Override
    public String getPlanChapter() {
        return PlanChapter.WORKERS.toString();
    }

    @Override
    public String getRequestText(String[] answers) {
        var template = "Выступай в качестве эксперта по бизнесу и напиши ответ на вопрос для для бизнеса. Отвечай развернуто. Ответ выдай в формате JSON в виде массива объектов с полями vacancy, quantity, salary. Размер массива: от 5 до 10 объектов. Формируй на основе средней зарплаты по месту, необходимому уровню компетенций. Прочитай текст в тегах info и отвечай подробно на его основе. [info]Информация о бизнесе: Суть проекта: [1] Сфера бизнеса: [2] Место реализации: [3] Планы компании: [4] Навыки и компетенции: [5] Отделы и функции: [6] Максимальный бюджет: [7] [/info] Зарплата не должна превышать максимальный бюджет.";
        var requestText = template;
        for(var i = 0; i < answers.length; i++){
            var oldChar = String.format("[%d]", i + 1);
            requestText = requestText.replace(oldChar, answers[i]);
        }
        return requestText;
    }

}
