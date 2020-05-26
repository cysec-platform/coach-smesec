package eu.smesec.library.demo.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;
import eu.smesec.library.questions.SelectQuestion;
import eu.smesec.library.questions.TypeAQuestion;

import java.util.Collections;

public class Q30 extends SelectQuestion {

    public Q30(Question question, AbstractLib lib) {
        super(question, lib);

        init((modifier, optionId) -> {}, Collections.EMPTY_LIST);
    }

}
