package eu.smesec.library.parser;

import java.util.List;

public class CommandPrint extends Command {

    @Override
    public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {

        if(list.size() == 0) {
            coachContext.getLogger().info("");

        } else {
            coachContext.getLogger().info(list.get(0).getId());

        }

        return new Atom(Atom.AtomType.NULL, null, null);
    }

}