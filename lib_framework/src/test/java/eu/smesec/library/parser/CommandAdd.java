package eu.smesec.library.parser;

import java.util.List;

public class CommandAdd extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    double d =0;
    boolean isFloat = false;
    for(Atom a :aList) {
      if (a.getType()!= Atom.AtomType.FLOAT && a.getType()!= Atom.AtomType.INTEGER) {
        throw new ExecutorException("Error while adding values: unsuitable type for adding ("+a.getType()+")");
      }
      isFloat |= a.getType()== Atom.AtomType.FLOAT;
      d+=Double.valueOf(a.getId());
    }
    return new Atom(isFloat? Atom.AtomType.FLOAT: Atom.AtomType.INTEGER,""+d,null);
  }

}
