package ru.urfu.utils.RequestTextGenerators;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequestTextGenerator {

    final List<String> questions;

    public List<String> getQuestions(){
        return questions;
    };

    AbstractRequestTextGenerator(List<String> questions) {
        this.questions = questions;
    }

    public abstract String getPlanChapter();
    public abstract String getRequestText(String[] answers);
}
