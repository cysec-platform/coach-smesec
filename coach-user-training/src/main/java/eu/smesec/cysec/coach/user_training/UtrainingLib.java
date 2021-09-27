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
package eu.smesec.cysec.coach.user_training;

import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;
import eu.smesec.cysec.csl.AbstractLib;

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
