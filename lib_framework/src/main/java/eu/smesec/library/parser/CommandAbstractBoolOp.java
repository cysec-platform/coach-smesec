package eu.smesec.library.parser;

import java.util.List;
import java.util.Vector;

public abstract class CommandAbstractBoolOp extends Command {

  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list == null || list.size() < 1) {
      throw new ExecutorException("boolean operations require at least one argument");
    }
    List<Boolean> blist = new Vector<>();
    for (Atom a : list) {
      if (a.getType() == Atom.AtomType.METHODE) {
        a = a.execute(coachContext);
      }
      blist.add(a.isTrue(coachContext));
    }
    if (evaluate(blist, coachContext.getContext())) {
      return new Atom(Atom.AtomType.BOOL, "TRUE", null);
    } else {
      return new Atom(Atom.AtomType.BOOL, "FALSE", null);
    }
  }

  abstract boolean evaluate(List<Boolean> list, ExecutorContext context) throws ExecutorException;
}
