package eu.smesec.library.parser;

import java.util.List;

import static eu.smesec.library.parser.Atom.NULL_ATOM;

/**
 * This command signals the platform to display a certain question instead of the next one in sequence.
 *
 * <p>Note: Don't forget to "unhide" the question beforehand, otherwise the sequence is in inconsistent state.
 * The ExecutorContext clears all variables set by the question context before each evaluation. Therefore it is not necessary
 * to "unset" a variable.</p>
 *
 * <p>Syntax: setNext(id);</p>
 * <p>Example: setNext("q20");</p>
 * @see eu.smesec.library.parser.CommandSetHidden
 */
public class CommandNext extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    if (aList.size() != 1 && aList.size() != 2) {
      throw new ExecutorException("Invalid number of arguments. Expected 1 parameter.");
    }

    // evaluate parameters
    Atom varContent = aList.get(0).execute(coachContext);

    // assert type of parameters
    if (varContent.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String");
    }

    // set the score
    coachContext.getContext().setVariable("_coach.nextPage", varContent, coachContext.getQuestionContext().getId());

    return NULL_ATOM;
  }

}
