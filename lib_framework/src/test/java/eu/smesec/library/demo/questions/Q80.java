package eu.smesec.library.demo.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;
import eu.smesec.library.questions.TextQuestion;
import eu.smesec.library.questions.TypeAQuestion;

import java.util.Collections;

public class Q80 extends TextQuestion {

    public Q80(Question question, AbstractLib lib) {
        super(question, lib);

        init((modifier, optionId) -> {}, Collections.EMPTY_LIST);
    }

}