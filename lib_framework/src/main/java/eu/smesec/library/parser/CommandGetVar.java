package eu.smesec.library.parser;

import java.util.List;

import static eu.smesec.library.parser.Atom.NULL_ATOM;

public class CommandGetVar extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: origin question id, score name and value
    if (aList.size() != 1) {
      throw new ExecutorException("Invalid number of arguments. Expected 2 parameters.");
    }

    // evaluate parameters
    Atom varName = aList.get(0).execute(coachContext);
    Atom varContext = aList.get(1).execute(coachContext);

    // assert type of parameters
    if (varName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String, [1] String and [2] Numeric value");
    }

    // set the score
    coachContext.getContext().getVariable(varName.getId(), varContext == NULL_ATOM ? null : varContext.toString());

    return NULL_ATOM;
  }

}
