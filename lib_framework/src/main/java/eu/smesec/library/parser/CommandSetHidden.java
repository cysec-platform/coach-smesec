package eu.smesec.library.parser;

import eu.smesec.bridge.generated.Question;
import eu.smesec.library.questions.LibQuestion;
import eu.smesec.library.skills.BadgeFactory;

import java.util.List;

/**
 * This command modifies the display status of a question from hidden to show.
 *
 * <p>Remember to add a condition to hide a question again, if it should not be displayed all the time once it is unhidden</p>
 *
 * <p>Syntax: setHidden(id, hidden);</p>
 * <p>Example: setHidden("q20", FALSE);</p>
 */
public class CommandSetHidden extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    if (aList.size() != 2) {
      throw new ExecutorException("Invalid number of arguments. Expected 2 parameters.");
    }

    // evaluate parameters
    Atom varContent = aList.get(0).execute(coachContext);

    // assert type of parameters
    if (varContent.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String.");
    }

    Atom varContentBool = aList.get(1).execute(coachContext);

    // assert type of parameters
    if (varContentBool.getType() != Atom.AtomType.BOOL) {
      throw new ExecutorException("Invalid types for parameters: Provide [1] Boolean.");
    }

    // Update question hidden status
    Question question = coachContext.getCoach().getQuestions().getQuestion().stream()
            .filter(question1 -> question1.getId().equals(varContent.getId()))
            .findFirst()
            .orElseThrow(() -> new ExecutorException("Question id " + varContent.getId() +" doesn't exist"));
    coachContext.getLogger().info(String.format("Set question %s to %s", varContent.getId(), varContentBool.getId()));
    question.setHidden(Boolean.valueOf(varContentBool.getId()));

    return new Atom(Atom.AtomType.NULL, null, null);
  }

}
