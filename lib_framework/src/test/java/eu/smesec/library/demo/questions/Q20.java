package eu.smesec.library.demo.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;
import eu.smesec.library.questions.TypeAQuestion;

import java.util.Arrays;

public class Q20 extends TypeAQuestion {

    public Q20(Question question, AbstractLib lib) {
        super(question, lib);

        init((modifier, optionId) -> { // Enable if q10o1 selected
            if (optionId.equals("q10o1")) {
                lib.getLogger().fine(String.format("enabling %s", question.getId()));
                setHide(false);
            } else {
                setHide(true);
                lib.getLogger().fine(String.format("disabling %s", question.getId()));
            }
        }, Arrays.asList(("q10")));
    }

}
