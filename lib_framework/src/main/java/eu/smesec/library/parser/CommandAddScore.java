package eu.smesec.library.parser;

import eu.smesec.library.Score;
import eu.smesec.library.skills.ScoreFactory;

import java.util.List;

/**
 * This command adds/subtracts a value to a given skill
 *
 * <p>The name can be arbitrary but must be unique within the coach context. The platform might only support a skills with a certain name prefix. The sign of the value determines whether
 * the score should be added (+) or subtracted (-). Note that the value returned by
 * {@link eu.smesec.library.parser.CySeCExecutorContextFactory.CySeCExecutorContext#getScore(String)}
 * won't change if a cap has previously been applied to that score</p>
 *
 * <p>Syntax: addScore("skill", "value");</p>
 * <p>Example: addScore("strength", 50);</p>
 * <p>Example: addScore("knowhow", -10);</p>
 * @see eu.smesec.library.parser.CommandCapScore
 */
public class CommandAddScore extends CommandAbstractScore {

  @Override
  void score(String scoreId, String questionId, double value, ExecutorContext context) {
    context.getScore(scoreId).add(questionId, value);

    // TODO: Decide if all questions should contribute to parent score
    /*if(context.getParent() != null) {
      context.getParent().getScore(scoreId).add(questionId, value);
    }*/
  }

}

