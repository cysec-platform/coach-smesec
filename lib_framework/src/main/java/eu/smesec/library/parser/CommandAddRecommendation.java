package eu.smesec.library.parser;

import eu.smesec.library.skills.RecommendationFactory;

import java.util.List;

/**
 * Creates a new instance of Recommendation in {@link eu.smesec.library.skills.RecommendationFactory}.
 * Note that the ID must be unique as this string will be used to retrieve the recommendation from the context.
 * <p>Syntax: AddRecommendation( recommendationName, order, urlImg, altImg, description, textLink, urlLink );</p>
 * <p>Example: addRecommendation("TieUpLooseEnds", 1, "lib-demo-sub,q10", "TestAlt", "Tie up loose ends", "Consider answering this question", "TextLink", "lib-demo-sub,q10");</p>
 *
 * @see eu.smesec.library.parser.CommandRevokeRecommendation
 */
public class CommandAddRecommendation extends Command {
  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list.size() != 8) {
      throw new ExecutorException("Invalid number of arguments. Expected 8 parameters.");
    }

    // evaluate parameters
    Atom recommendationName = list.get(0).execute(coachContext);
    Atom order = list.get(1).execute(coachContext);
    Atom urlImg = list.get(2).execute(coachContext);
    Atom altImg = list.get(3).execute(coachContext);
    Atom title = list.get(4).execute(coachContext);
    Atom description = list.get(5).execute(coachContext);
    Atom textLink = list.get(6).execute(coachContext);
    Atom urlLink = list.get(7).execute(coachContext);

    if (recommendationName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Recommendation name must be of type STRING");
    }
    if (order.getType() != Atom.AtomType.INTEGER) {
      throw new ExecutorException("Recommendation class name must be of type STRING");
    }
    if (urlImg.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Image url must be of type STRING");
    }
    if (altImg.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Image alternate description must be of type STRING");
    }
    if (title.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Title description must be of type STRING");
    }
    if (description.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Description must be of type STRING");
    }
    if (urlLink.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Link must be of type STRING");
    }
    if (textLink.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Link text must be of type STRING");
    }

    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    c.addRecommendation(new RecommendationFactory.Recommendation(recommendationName.getId(), Integer.valueOf(order.getId()), urlImg.getId(), altImg.getId(), title.getId(), description.getId(), textLink.getId(), urlLink.getId()));

    return null;
  }

}
