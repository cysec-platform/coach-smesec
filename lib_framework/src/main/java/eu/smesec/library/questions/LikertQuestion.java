package eu.smesec.library.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;

import java.util.Collection;
import java.util.Optional;

/**
 * This question represents the a likert type questions. out of multiple numeric options only one be may selected.
 */
public class LikertQuestion extends TypeAQuestion {

    /**
     * Constructor for question creation.
     * @param question The question object from xml
     * @param lib The actual library that handles the coach
     */
    public LikertQuestion(Question question, AbstractLib lib) {
        super(question, lib);
    }

    /**
     * Needs to overwrite super method as likert answer only return number value 1-5.
     * @param optionId the selected likert scale value
     * @param qid The id of the question
     * @return The question id with "o" prefix and selected value
     */
    @Override
    LibSelectOption findOption(String optionId, String qid) {
        return getOptions().get(qid + "o" + optionId);
    }
}
