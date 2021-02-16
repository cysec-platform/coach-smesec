package eu.smesec.library.backup;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.MetadataBuilder;


import static eu.smesec.bridge.md.MetadataUtils.MD_SKILLS;
import static eu.smesec.bridge.md.MetadataUtils.MV_IMAGE;

public class FirstLibrary extends AbstractLib {


    @Override
    @SuppressWarnings("unchecked")
    public void initHook(String id, Questionnaire questionnaire, ILibCal libCal) {
        getLogger().info("Inside FirstLibrary");
    }

    @Override
    protected void onResumeHook(String qId) {
        getLogger().info("Done performing onResume hook");

    }

    @Override
    protected void onResponseChangeHook(Question question, Answer response) {
        getLogger().info("Done performing onResponseChange hook");

        // Ignored for the moment, pick up again after October 10th
     /*   getPersistanceManager().createMetadata(questionnaire.getId(), MetadataBuilder.newInstance(this)
                .setMvalue(MV_IMAGE, "/sign-check-icon.png")
                .buildCustom(MD_SKILLS));
*/
    }

    @Override
    protected void onBeginHook() {
        getLogger().info("Done performing onBeginHook hook");


    }
}
