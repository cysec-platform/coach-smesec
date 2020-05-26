package eu.smesec.library.parser;

import eu.smesec.library.skills.ScoreFactory;

/**
 * This command locks the value of a certain skill to a given value.
 *
 * <p>The library will keep recording contributions to said skill internally but will return the capped value upon access.
 * There is no corresponding command "removeCap" because coach logic is reevaluated when a question changes. That means
 * the cap on a skill is removed, once the condition that implied the cap does not hold anymore</p>
 *
 * <p>Syntax: capScore("skill", "value");</p>
 * <p>Example: capScore("strength", 50);</p>
 * <p>If q10o3 is selected, knowhow will be limited to 100. Once that condition fails, the cap won't be added again.</p>
 * <pre>
 *     <code>
 *         isSelected("q10o3") : q10o3 : {
 *             capScore("knowhow", 100);
 *          };
 *     </code>
 * </pre>
 *
 * @see eu.smesec.library.parser.CommandAddScore
 */
public class CommandCapScore extends CommandAbstractScore {

  @Override
  void score(String scoreId, String questionId, double value, ExecutorContext context) {
    context.getScore(scoreId).cap(questionId, value);
  }


}

