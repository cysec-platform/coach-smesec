package eu.smesec.library.user_training;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.library.AbstractLib;

public class UtrainingLib extends AbstractLib {


    @Override
    protected void initHook(String id, Questionnaire questionnaire, ILibCal libCal) {
        getLogger().info("Running initializing Utraining lib");
    }

    protected void onBeginHook() {
        getLogger().info("Running onBeginHook of Utraining lib");

    }

    @Override
    protected void onResumeHook(String questionId) {
        getLogger().info("Resuming from question " + questionId);

    }

    @Override
    protected void onResponseChangeHook(Question question, Answer response) {
        getLogger().info("Done performing onResponseChange hook");
    }
}
