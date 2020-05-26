package eu.smesec.library.parser;

import java.util.List;

public class CommandConcat extends Command {

  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list == null || list.size() < 1) {
      throw new ExecutorException("concat operations require at least one argument");
    }
    StringBuilder sb = new StringBuilder();
    for (Atom a : list) {
      if (a.getType() == Atom.AtomType.METHODE) {
        a = a.execute(coachContext);
      }
      sb.append(a.getType() == Atom.AtomType.STRING ? a.getId() : a.toString());
    }
    return new Atom(Atom.AtomType.STRING, sb.toString(), null);
  }

}
