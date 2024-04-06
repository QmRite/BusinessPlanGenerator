package ru.urfu.utils;

import ru.urfu.entity.enums.PlanChapter;

import java.util.Map;

public class PlanChapterNameConvertor {
    public static Map<PlanChapter, String> nameByPlanChapter = Map.of(
            PlanChapter.DESCRIPTION,"Описание бизнес-идеи",
            PlanChapter.PRODUCT_DESCRIPTION, "Описание продукта"
    );
}

