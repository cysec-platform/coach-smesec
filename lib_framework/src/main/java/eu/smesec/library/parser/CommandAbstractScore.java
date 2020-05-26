package eu.smesec.library.parser;

import java.util.List;

public abstract class CommandAbstractScore extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: origin question id, score name and value
    if (aList.size() != 2) {
      throw new ExecutorException("Invalid number of arguments. Expected 2 parameters.");
    }

    // evaluate parameters
    Atom scoreName = aList.get(0).execute(coachContext);
    Atom scoreValue = aList.get(1).execute(coachContext);

    // assert type of parameters
    if (scoreName.getType() != Atom.AtomType.STRING
            || (scoreValue.getType() != Atom.AtomType.FLOAT && scoreValue.getType() != Atom.AtomType.INTEGER)) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String, [1] String and [2] Numeric value");
    }

    // set the score
    score(scoreName.getId(), coachContext.getQuestionContext().getId(), Double.valueOf(scoreValue.getId()), coachContext.getContext());
    coachContext.getLogger().info(String.format("Adding %s to score %s in context %s", scoreValue.getId(), scoreName.getId(), coachContext.getContext()));

    return Atom.NULL_ATOM;
  }

  abstract void score(String scoreId, String questionId, double value, ExecutorContext context);

}
