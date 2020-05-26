package eu.smesec.library.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class CySeCLineAtom {

  Atom cond;
  String name;
  Atom[] statements;

  public CySeCLineAtom(Atom cond, String name, Atom[] a) {
    this.cond = cond;
    this.name = name;
    this.statements = a;
  }

  public Atom getCond() {
    return cond;
  }

  public String getName() {
    return name;
  }

  public List<Atom> getStatements() {
    return Arrays.asList(statements);
  }

  public Atom execute(CoachContext coachContext) throws ExecutorException {
    Atom lastResult = new Atom(Atom.AtomType.BOOL, "TRUE", new Vector<>());
    for (Atom a : getStatements()) {
      lastResult = a.execute(coachContext);
    }
    return lastResult;
  }
}
