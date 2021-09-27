/*
 * Copyright (C) 2020 - 2021 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smesec.cysec.coach.company;

import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;
import eu.smesec.cysec.csl.AbstractLib;

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
