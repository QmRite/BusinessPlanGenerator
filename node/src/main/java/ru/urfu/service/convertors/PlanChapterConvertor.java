package ru.urfu.service.convertors;

import ru.urfu.entity.enums.PlanChapter;

import java.util.Map;

public class PlanChapterConvertor {

    public static Map<String, PlanChapter> PlanChapterByText = Map.of(
            "1. Описание бизнес-идеи", PlanChapter.DESCRIPTION,
            "2. Описание продукта", PlanChapter.PRODUCT_DESCRIPTION,
            "3. Маркетинговый план", PlanChapter.MARKETING,
            "4. Производственный план", PlanChapter.INDUSTRY,
            "5. Человеческие ресурсы", PlanChapter.WORKERS,
            "7. Оценка рисков", PlanChapter.RISKS,
            "8. Перспективы", PlanChapter.PROSPECTS
    );
}
