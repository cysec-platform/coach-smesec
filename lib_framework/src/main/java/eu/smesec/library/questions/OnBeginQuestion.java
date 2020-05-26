package eu.smesec.library.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;

import java.util.Optional;

public class OnBeginQuestion extends LibQuestion {
    public OnBeginQuestion(Question question, AbstractLib lib) {
        super(question, lib);
    }

    @Override
    protected Modifier updateState(String data, String qid) {
        return Modifier.SELECTED;
    }

    @Override
    public boolean isAnswered() {
        return true;
    }

    @Override
    public Optional<String> getSuccessor() {
        return Optional.empty();
    }
}
