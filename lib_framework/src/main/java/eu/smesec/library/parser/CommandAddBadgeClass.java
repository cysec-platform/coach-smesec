package eu.smesec.library.parser;

import eu.smesec.library.skills.BadgeFactory;

import java.util.List;

/**
 * Adds a badge class to the {@link BadgeFactory}. This method must be executed before any Badges may be awarded with {@link CommandAwardBadge}
 * <p>Syntax: addBadgeClass( badgeName, className, order, urlImg, altImg, description,urlLink );</p>
 *  <p>Example: addBadgeClass( "ServerSavior", "Bronze", 1, "assets/images/serversaviorbronze.svg", "", "Superman of servers", "lib-backup,q10");</p>
 *
 * @see eu.smesec.library.parser.CommandAddBadge
 * @see eu.smesec.library.parser.CommandAwardBadge
 * @see eu.smesec.library.parser.CommandRevokeBadge
 *
 */
public class CommandAddBadgeClass extends Command {

  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    // expects 3 parameters: origin question id, score name and value
    if (list.size() != 7) {
      throw new ExecutorException("Invalid number of arguments. Expected 7 parameters.");
    }

    // evaluate parameters
    Atom badgeName = list.get(0).execute(coachContext);
    Atom badgeClassName = list.get(1).execute(coachContext);
    Atom order = list.get(2).execute(coachContext);
    Atom urlImg = list.get(3).execute(coachContext);
    Atom altImg = list.get(4).execute(coachContext);
    Atom description = list.get(5).execute(coachContext);
    Atom urlLink = list.get(6).execute(coachContext);

    if (badgeName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Badge name must be of type STRING");
    }
    if (badgeClassName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Badge class name must be of type STRING");
    }
    if (order.getType() != Atom.AtomType.INTEGER) {
      throw new ExecutorException("Badge class name must be of type STRING");
    }
    if (urlImg.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Image url must be of type STRING");
    }
    if (altImg.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Image alternate description must be of type STRING");
    }
    if (description.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Description must be of type STRING");
    }
    if (urlLink.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Link must be of type STRING");
    }

    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    BadgeFactory.Badge b = c.getBadge(badgeName.getId());
    if (b == null) {
      throw new ExecutorException("Badge id "+badgeName.getId()+" is not known");
    }
    b.addBadgeClass(new BadgeFactory.BadgeClass(badgeClassName.getId(), Integer.valueOf(order.getId()), urlImg.getId(), altImg.getId(), description.getId(), urlLink.getId()));

    return null;
  }

}
