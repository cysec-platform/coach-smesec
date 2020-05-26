package eu.smesec.library.parser;

import eu.smesec.library.skills.BadgeFactory;

import java.util.List;

/**
 * Revokes the current badgeClass of a badge class in {@link BadgeFactory}. This method may only be executed after a Badge is awarded using {@link CommandAwardBadge}.
 * If no Badge with the given ID or class exist, Executor throws an exception.
 *
 * <p>Syntax: revokeBadge( badgeName);</p>
 *  <p>Example: revokeBadge("ServerSavior");</p>
 *
 * @see eu.smesec.library.parser.CommandAddBadge
 * @see eu.smesec.library.parser.CommandAwardBadge
 * @see eu.smesec.library.parser.CommandAddBadgeClass
 *
 */
public class CommandRevokeBadge extends Command {
  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list.size() != 1) {
      throw new ExecutorException("Invalid number of arguments. Expected 1 parameters.");
    }

    // evaluate parameters
    Atom badgeName = list.get(0).execute(coachContext);

    // verify type of parameters
    if (badgeName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Badge name must be of type STRING");
    }

    // execute command
    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    BadgeFactory.Badge badge = c.getBadge(badgeName.getId());
    if (badge == null) {
      //throw new ExecutorException("Badge id "+badgeName.getId()+" doesn't exist");
    } else {
      badge.revokeAwardedBadge();
    }

    return Atom.NULL_ATOM;
  }

}
