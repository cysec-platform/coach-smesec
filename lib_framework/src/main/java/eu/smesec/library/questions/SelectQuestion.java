package eu.smesec.library.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;

import java.util.Arrays;

/**
 * Represents the type A and YesNo question types. The questions only differ in their options text property. Otherwise
 * they behave exactly the same which is why there are no separate classes for A and YesNo.
 */
public class SelectQuestion extends AstarQuestion {
    private LibSelectOption optionYes;
    private LibSelectOption optionNo;


    /**
     * New constructor for question creation. It assumes that for all Type A questions, o1 is the "Yes"
     * and o2 is the "No" option.
     * @param question The question object from xml
     * @param lib The actual library that handles the coach
     */
    public SelectQuestion(Question question, AbstractLib lib) {
        super(question, lib);
        optionYes = getOptions().get(question.getId() + "o1");
        optionNo = getOptions().get(question.getId() + "o2");

    }

    public SelectQuestion(String id, String nextQid, boolean hide, LibSelectOption yes, LibSelectOption no) {
        super(id, nextQid, hide, Arrays.asList(yes, no));
        // convenience access to both options
        optionYes = yes;
        optionNo = no;
    }

    public SelectQuestion(String id, String nextQid, LibSelectOption yes, LibSelectOption no) {
        this(id, nextQid, false, yes, no);
    }

    /**
     * Only one option may be selected at a time.
     * @param optionId the option
     * @param qid the question
     * @return the Modifier indicating what kind of change occured.
     */
    @Override
    protected Modifier updateState(String optionId, String qid) {
        LibSelectOption o = getOptions().get(optionId);
        if (o == null) {
            throw new IllegalArgumentException("Cannot find option " + optionId + " in question " + this.getId());
        }

        // 'Yes' selected
        if (optionYes.getId().equals(optionId)) {
            optionYes.setSelected(true);
            optionNo.setSelected(false);
            // remove other option score
        } else { // 'No' selected
            optionNo.setSelected(true);
            optionYes.setSelected(false);
        }

        String nqid = o.getNextQid();
        if (nqid == null) {
            nextQid = getDefaultNextQid();
        } else {
            nextQid = nqid;
        }
        return Modifier.SELECTED;
    }
}
