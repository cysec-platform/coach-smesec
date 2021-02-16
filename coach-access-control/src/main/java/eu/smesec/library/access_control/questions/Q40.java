package eu.smesec.library.access_control.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;
import eu.smesec.library.questions.TypeAQuestion;

import java.util.Collections;

public class Q40 extends TypeAQuestion {

    public Q40(Question question, AbstractLib lib) {
        super(question, lib);

        init((modifier, optionId) -> {}, Collections.EMPTY_LIST);
    }

}
