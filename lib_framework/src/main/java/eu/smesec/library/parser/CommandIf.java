package eu.smesec.library.parser;

import java.util.List;

public class CommandIf extends Command {

  public CommandIf() {
    super();
    numberOfNormalizedParams = 1;
  }


  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    /*
     At this point both the if and else branch are already evaluated. Reproduce by running the test case
     TestCommands#testIfCommand()
     Also: the if and else branch content must be swapped. Otherwise the reverse of the intended behaviour happens.
      */
    if (list.size() != 2 && list.size() != 3) {
      throw new ExecutorException("An \"if\" command always requires two or three patarmeters");
    }

    Atom cond = list.get(0);
    if (cond.isTrue(coachContext)) {
      return list.get(1).execute(coachContext);
    } else {
      Atom ret = list.get(2).execute(coachContext);
      if (ret != null) {
        return ret;
      } else {
        return new Atom(Atom.AtomType.BOOL, "FALSE", null);
      }
    }
  }
}
