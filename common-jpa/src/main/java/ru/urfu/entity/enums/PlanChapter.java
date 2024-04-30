package ru.urfu.entity.enums;

public enum PlanChapter {
    RESUME("resume"),
    DESCRIPTION("description"),
    PRODUCT_DESCRIPTION("product_description"),
    MARKETING("marketing"),
    INDUSTRY("industry"),
    WORKERS("workers"),
    RISKS("risks"),
    PROSPECTS("prospects"),;



    private final String value;

    PlanChapter(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
