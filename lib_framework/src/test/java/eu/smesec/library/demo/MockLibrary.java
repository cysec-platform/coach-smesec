package eu.smesec.library.demo;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.library.AbstractLib;

/**
 * Test library for the framework. A library doesnt need any functionality, if all it does is use the frameworks
 * base features. Therefore all methods are empty implementations.
 */
public class MockLibrary extends AbstractLib {

    @Override
    protected void initHook(String id, Questionnaire questionnaire, ILibCal libCal) {

    }

    @Override
    protected void onBeginHook() {

    }

    @Override
    protected void onResumeHook(String qId) {
    }

    @Override
    protected void onResponseChangeHook(Question question, Answer answer) {

    }

}