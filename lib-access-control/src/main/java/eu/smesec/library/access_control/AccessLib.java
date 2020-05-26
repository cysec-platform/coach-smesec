package eu.smesec.library.access_control;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.library.AbstractLib;

public class AccessLib extends AbstractLib {


    @Override
    protected void onBeginHook() {

    }

    @Override
    protected void onResumeHook(String qId) {

    }

    @Override
    protected void onResponseChangeHook(Question question, Answer answer) {

    }

    @Override
    public void initHook(String id, Questionnaire questionnaire, ILibCal libCal) {

    }

}
