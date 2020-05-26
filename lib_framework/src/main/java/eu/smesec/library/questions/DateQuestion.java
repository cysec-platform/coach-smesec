package eu.smesec.library.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;

import java.util.Optional;

public class DateQuestion extends TextQuestion {

    public DateQuestion(Question question, AbstractLib lib) {
        super(question, lib);
    }

}
