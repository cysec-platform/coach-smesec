package eu.smesec.library.parser;

import java.util.List;

public class CommandBlank extends Command {

  public Atom execute(List<Atom> a, CoachContext coachContext) {
    return new Atom(Atom.AtomType.STRING,"",null);
  }

}
