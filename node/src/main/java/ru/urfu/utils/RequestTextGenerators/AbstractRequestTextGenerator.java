package ru.urfu.utils.RequestTextGenerators;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequestTextGenerator {

    final List<String> questions;

    AbstractRequestTextGenerator(List<String> questions) {
        this.questions = questions;
    }

    public abstract String getPlanChapter();
    public abstract String getRequestText(ArrayList<String> answers);
}
