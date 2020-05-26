package eu.smesec.library.parser;

import eu.smesec.library.skills.BadgeFactory;
import eu.smesec.library.skills.RecommendationFactory;

import java.util.List;

/**
 * Removes a Recommendation from {@link eu.smesec.library.skills.RecommendationFactory}
 *
 * <p>Syntax: removeRecommendation( recommendationName );</p>
 * <p>Example: removeRecommendation( "TieUpLooseEnds" );</p>
 *
 * @see eu.smesec.library.parser.CommandAddRecommendation
 */
public class CommandRevokeRecommendation extends Command {
  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list.size() != 1) {
      throw new ExecutorException("Invalid number of arguments. Expected 1 parameters.");
    }

    // evaluate parameters
    Atom recommendationName = list.get(0).execute(coachContext);

    if (recommendationName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Recommendation name must be of type STRING");
    }

    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    RecommendationFactory.Recommendation recommendation = c.getRecommendation(recommendationName.getId());
    if (recommendation == null) {
        // no operation
      //throw new ExecutorException("Recommendation id "+ recommendationName.getId()+" doesn't exist");
    } else {
        c.removeRecommendation(recommendationName.getId());
    }

    return Atom.NULL_ATOM;
  }

}
