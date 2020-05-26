package eu.smesec.library.demo.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;
import eu.smesec.library.questions.Modifier;
import eu.smesec.library.questions.SelectQuestion;

import java.util.Arrays;
import java.util.Collections;

public class Q100 extends SelectQuestion {

    public Q100(Question question, AbstractLib lib) {
        super(question, lib);

        init((modifier, optionId) -> { // Enable if q10o1 selected
            if (optionId.equals("q70o1")) {
                if(modifier.equals(Modifier.SELECTED)) {
                    lib.getLogger().fine(String.format("enabling %s", question.getId()));
                    setHide(false);
                } else {
                    setHide(true);
                    lib.getLogger().fine(String.format("disabling %s", question.getId()));

                }
            }
        }, Arrays.asList(("q70")));
    }

}
