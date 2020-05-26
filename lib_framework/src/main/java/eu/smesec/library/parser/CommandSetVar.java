package eu.smesec.library.parser;

import java.util.List;

import static eu.smesec.library.parser.Atom.NULL_ATOM;

public class CommandSetVar extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    if (aList.size() != 3 && aList.size() != 2) {
      throw new ExecutorException("Invalid number of arguments. Expected 2 or 3 parameters.");
    }

    // evaluate parameters
    Atom varName = aList.get(0).execute(coachContext);
    Atom varContext = aList.get(1).execute(coachContext);
    Atom varContent = null;
    if (aList.size() == 2) {
      varContext = new Atom(Atom.AtomType.STRING, coachContext.getQuestionContext().getId(), null);
      varContent = aList.get(1).execute(coachContext);
    } else {
      varContent = aList.get(2).execute(coachContext);
    }

    // assert type of parameters
    if (varName.getType() != Atom.AtomType.STRING || (varContext.getType() != Atom.AtomType.STRING && varContext != NULL_ATOM)) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String, [1] String and [2] ANY or [0] String and [2] ANY");
    }

    // set the score
    coachContext.getContext().setVariable(varName.getId(), varContent, varContext == NULL_ATOM ? null : varContext.getId());
    coachContext.getLogger().info(String.format("Set variable %s to %s in context %s", varName.getId(), varContent.getId(), varContent.getId()));

    return NULL_ATOM;
  }

}
