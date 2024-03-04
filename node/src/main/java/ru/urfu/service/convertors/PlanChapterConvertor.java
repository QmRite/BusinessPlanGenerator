package ru.urfu.service.convertors;

import ru.urfu.service.enums.PlanChapter;

import java.util.Map;

public class PlanChapterConvertor {

    public static Map<String, PlanChapter> PlanChapterByText = Map.of(
            "1. Описание бизнес-идеи", PlanChapter.DESCRIPTION,
            "2. Описание продукта", PlanChapter.PRODUCT_DESCRIPTION
    );
}
