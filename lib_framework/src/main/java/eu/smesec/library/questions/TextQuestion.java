package eu.smesec.library.questions;

import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.md.MetadataUtils;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;

import java.util.Optional;

public class TextQuestion extends LibQuestion {
    private String content;
    private int scoreValue;

    public TextQuestion(Question question, AbstractLib lib) {
        super(question, lib);
        Optional<MetadataUtils.SimpleMvalue> scoreMeta = Optional.ofNullable(searchMvalue("scores.default"));

        if (scoreMeta.isPresent()) {
            scoreValue = Integer.valueOf(scoreMeta.get().getValue());
        } else {
            lib.getLogger().info("No score value available for question " + question.getId());
        }
    }

    @Override
    protected Modifier updateState(String data, String qid) {
        // When either text is added or removed, the answer is modified
        // Select/Unselect does not apply
        content = data;
        return Modifier.MODIFIED;
    }

    @Override
    public boolean isAnswered() {
        return (content != null && !content.isEmpty());
    }

    @Override
    public Optional<String> getSuccessor() {
        return Optional.ofNullable(getNextQid());
    }
}
