package ru.urfu.utils;

import java.util.HashMap;
import java.util.Map;

public class PlanChapterNameTranslator {
    public static Map<String, String> translator = Map.of(
            "description", "Описание бизнес-идеи",
            "product_description", "Описание продукта",
            "prospects", "Перспективы развития бизнеса",
            "risks", "Оценка рисков",
            "workers", "Человеческие ресурсы",
            "marketing", "Маркетинговый план",
            "industry", "Производственный план",
            "finance", "Финансы и инвестиции"
            );
}
