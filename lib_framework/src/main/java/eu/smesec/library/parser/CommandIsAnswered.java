package eu.smesec.library.parser;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.execptions.CacheException;
import eu.smesec.bridge.generated.Answer;

import java.util.List;

public class CommandIsAnswered extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    if (aList.size() != 1) {
      throw new ExecutorException("Invalid number of arguments. Expected 1 parameter.");
    }

    // evaluate parameters
    Atom varContent = aList.get(0).execute(coachContext);

    // assert type of parameters
    if (varContent.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String");
    }

    // determine provided option is selected
    ILibCal cal = coachContext.getCal();
    Answer answer = null;
    try {
      // Attention: Use question ID instead of question! getAnswer accepts Object.
      // Answer object in CoachContext is answer of evaluated question, isAnswered may be executed for another question
      // which is not in the current context.
      answer = cal.getAnswer(coachContext.getFqcn().toString(), varContent.getId());
    } catch (CacheException e) {
        throw new NullPointerException();
    }
    String boolResult;
    if(answer != null) {
      boolResult = "TRUE";
    } else {
      boolResult = "FALSE";
    }

    return new Atom(Atom.AtomType.BOOL, boolResult, null);
  }

}
