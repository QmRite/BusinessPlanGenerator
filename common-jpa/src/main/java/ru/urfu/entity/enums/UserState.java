package ru.urfu.entity.enums;

public enum UserState {
    MAIN_MENU_STATE("Главное меню"),

    HELP_STATE("Помощь"),

    STORAGE_FILE_SELECTION_STATE("Выбор ранее созданного файла"),

    STORAGE_VIEWING_STATE("Ранее созданные файлы"),

    CHAPTER_SELECTION_STATE("Создать главу бизнес-плана"),

    DIALOG_STATE("Создание главы бизнес-плана"),

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
