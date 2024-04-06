package ru.urfu.entity.enums;

public enum UserState {
    MAIN_MENU_STATE("Главное меню"),

    HELP_STATE("Помощь"),

    PREVIOUS_FILE_SELECTION_STATE("--"),

    PREVIOUS_FILE_VIEWING_STATE("---"),

    CHAPTER_SELECTION_STATE("Создание главу"),

    DIALOG_STATE("Выбор конкретной главы"),

    WAITING_FOR_DOCUMENT_STATE(""),

    RECEIVED_DOCUMENT_STATE("Файл получен");

    private final String value;

    UserState(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static UserState fromValue(String v) {
        for (UserState s: UserState.values()) {
            if (s.value.equals(v)) {
                return s;
            }
        }
        return null;
    }
}
