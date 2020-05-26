package eu.smesec.library.parser;

import eu.smesec.library.skills.BadgeFactory;

import java.util.List;

/**
 * Awards a badge class to a badge instance in {@link BadgeFactory}. This method may only be executed after a Badge is created using {@link CommandAddBadge}
 * and a class created using {@link CommandAddBadgeClass}
 * <p>Syntax: awardBadge( badgeName, className );</p>
 *  <p>Example: awardBadge("ServerSavior", "Bronze");</p>
 *
 * @see eu.smesec.library.parser.CommandAddBadge
 * @see eu.smesec.library.parser.CommandAwardBadge
 * @see eu.smesec.library.parser.CommandRevokeBadge
 *
 */
public class CommandAwardBadge extends Command {
  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list.size() != 2) {
      throw new ExecutorException("Invalid number of arguments. Expected 2 parameters.");
    }

    // evaluate parameters
    Atom badgeName = list.get(0).execute(coachContext);
    Atom badgeClassName = list.get(1).execute(coachContext);

    // verify type of parameters
    if (badgeName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Badge name must be of type STRING");
    }
    if (badgeClassName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Badge class name must be of type STRING");
    }

    // execute command
    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    BadgeFactory.Badge b = c.getBadge(badgeName.getId());
    if (b == null) {
      throw new ExecutorException("Badge id "+badgeName.getId()+" doesn't exist");
    }
    b.awardBadgeClass(badgeClassName.getId());

    return Atom.NULL_ATOM;
  }

}
