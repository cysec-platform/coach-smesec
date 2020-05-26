package eu.smesec.library.utils;

import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;

public class Utils {
    public static Question findById(Questionnaire coach, String qid) {
        return coach.getQuestions().getQuestion().stream()
                .filter(q -> q.getId().equals(qid))
                .findFirst().orElse(null);

    }
}
