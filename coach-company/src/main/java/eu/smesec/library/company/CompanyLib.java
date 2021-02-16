package eu.smesec.library.company;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.bridge.md.MetadataUtils;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.MetadataBuilder;

public class CompanyLib extends AbstractLib {

    @Override
    public void initHook(String id, Questionnaire questionnaire, ILibCal libCal) {
        getLogger().info("Inside CompanyLib");

        // Display recommendation to fill in company coach first

    }

    @Override
    protected void onBeginHook() {
//        Recommendation coachRecommendation = getRecommendation("CompanyCoach");
//        createMetadata(MetadataBuilder.newInstance()
//                .setId(coachRecommendation.getId())
//                .setName(coachRecommendation.getName())
//                .setDescription(coachRecommendation.getDescription())
//                .setOrder(coachRecommendation.getOrder())
//                .setGeneral(coachRecommendation.getGeneral())
//                .setLink(coachRecommendation.getLink()).buildRecommendation());

    }

    @Override
    protected void onResumeHook(String qId) {

    }

    @Override
    protected void onResponseChangeHook(Question question, Answer answer) {

    }

}
