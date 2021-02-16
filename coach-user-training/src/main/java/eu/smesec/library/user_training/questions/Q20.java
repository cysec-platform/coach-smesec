package eu.smesec.library.user_training.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;
import eu.smesec.library.questions.TypeAQuestion;

import java.util.Arrays;

public class Q20 extends TypeAQuestion {

    public Q20(Question question, AbstractLib lib) {
        super(question, lib);

        init((modifier, optionId) -> { // Enable if q10o1 selected
            if (optionId.equals("q10o2")) {
                lib.getLogger().info(String.format("enabling %s", question.getId()));
                setHide(false);
            } else {
                setHide(true);
                lib.getLogger().info(String.format("disabling %s", question.getId()));
            }
        }, Arrays.asList(("q10")));
    }

}
