package eu.smesec.library.parser;

import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.library.skills.BadgeEventListener;
import eu.smesec.library.skills.BadgeFactory;
import eu.smesec.library.skills.BadgeFactory.Badge;
import eu.smesec.library.skills.RecommendationEventListener;
import eu.smesec.library.skills.RecommendationFactory.Recommendation;
import eu.smesec.library.skills.RecommendationFactory;
import eu.smesec.library.skills.ScoreFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eu.smesec.library.parser.Atom.NULL_ATOM;

public class CySeCExecutorContextFactory {

  private static class Variable {
    private Map<String, Atom> var = new HashMap<>();
    private Atom lastval = null;

    public Atom setVariable(String context, Atom value) {
      Atom ret = getVariable(context);
      if (context != null) {
        var.put(context, value);
      }
      lastval = value;
      return ret;
    }

    public Atom getVariable(String context) {
      return var.get(context) == null ? lastval : var.get(context);
    }

    public Collection<Atom> getAll() {
      return var.values();
    }


  }

  /**
   * Exists once per coach library and holds all relevant information about the context.
   *
   * <p>This includes all created badges and badge classes, recommendations, scores and variables.</p>
   * <p>In addition the context is linked to its parent context and may perform manipulation on
   * the parent instead of itw own context.</p>
   */
  public static class CySeCExecutorContext implements ExecutorContext, Serializable {
    private static final long serialVersionUID = 2365600923596387762L;
    private List<String> executedNames = new Vector<>();
    private Logger logger = Logger.getLogger(
            (new Throwable()).getStackTrace()[0].getClassName());
    private ScoreFactory scores = new ScoreFactory();
    private final Object executorLock = new Object();
    private Map<String, Variable> variables = new HashMap<>();
    private ExecutorContext parent = null;
    private RecommendationFactory recommendations = new RecommendationFactory();
    private BadgeFactory badges = new BadgeFactory();
    private String contextId;

    public CySeCExecutorContext(String contextId, Logger log) {
      if (log != null) {
        this.contextId = contextId;
        logger = log;
      }
    }

    public void printVariables(Logger logger) {
      variables.forEach((key, value) -> logger.info(String.format("%s : %s", key, Arrays.toString(value.getAll().toArray()))));
    }

    public void reset() {
      variables.clear();
      executedNames.clear();
      scores.reset();
      badges.reset();
      recommendations.reset();
      //contextMap.clear();
    }

    public ScoreFactory.Score getScore(String scoreId) {
      return scores.getIntScore(scoreId);
    }

    @Override
    public List<ScoreFactory.Score> getScores() {
      ScoreFactory.Score[] scoreArray = scores.getScoreList(false);
      if (scoreArray != null && scoreArray.length > 0) {
        return Arrays.asList(scoreArray);
      } else {
        return Collections.emptyList();
      }
    }

    @Override
    public String getContextId() {
      return contextId;
    }

    public Recommendation getRecommendation (String id){
      return recommendations.getRecommendation(id);
    }

    public Recommendation[] getRecommendationList(){
      return recommendations.getRecommendationList();
    }

    public void addRecommendation (Recommendation r){
      recommendations.addRecommendation(r);
    }

    public Recommendation removeRecommendation (String id){
      return recommendations.removeRecommendation(id);
    }

    public void setRecommendationListener(RecommendationEventListener listener) {
      recommendations.setListener(listener);
    }

    public BadgeFactory.Badge getBadge(String id){
      return badges.getBadge(id);
    }

    public BadgeFactory.Badge[] getBadgeList(){
      return badges.getBadgeList();
    }

    public void setBadge(Badge b){
      badges.setBadge(b);
    }

    public void setBadgeListener(BadgeEventListener listener) {
      badges.setListener(listener);
    }

    public double resetScore(String scoreId) {
      double ret = scores.getScore(scoreId);
      scores.removeScore(scoreId);
      return ret;
    }

    public void revertQuestionScore(String questionId) {
      for (ScoreFactory.Score s : scores.getScoreList(true)) {
        s.revertQuestion(questionId);
      }
    }

    @Override
    public Atom getVariable(String name, String context) {
      synchronized (variables) {
        return variables.get(name) == null ? NULL_ATOM : variables.get(name).getVariable(context);
      }
    }

    @Override
    public Atom setVariable(String name, Atom value, String context) {
      synchronized (variables) {
        if (variables.get(name) == null) {
          variables.put(name, new Variable());
        }
        if (value == null) {
          value = NULL_ATOM;
        }
        Atom ret = getVariable(name, context);
        variables.get(name).setVariable(context, value);
        return ret;
      }
    }

    @Override
    public int executeQuestion(List<CySeCLineAtom> atomList, CoachContext coachContext) throws ExecutorException {
      CySeCExecutorContext ec = (CySeCExecutorContext) (coachContext.getContext());
      synchronized (ec.executorLock) {
        ec.executedNames.clear();
        ec.scores.revertQuestion(coachContext.getQuestionContext().getId());

        // this should clear previously set variables from that question
        //ec.variables.remove(coachContext.getQuestionContext().getId());
        for(Variable variable : ec.variables.values()) {
          variable.var.remove(coachContext.getQuestionContext().getId());
          variable.lastval = null;
        }

        //
        if (ec.getParent() != null) {
          // One idea could be adding the subcoach id as a prefix when adding to a parent context
          ((CySeCExecutorContext) (ec.getParent())).scores.revertQuestion(coachContext.getQuestionContext().getId());
          // todo clear variables from this question too
          // todo clear executedNames
        }
        return ec.execute(atomList, coachContext);
      }

    }

    @Override
    public ExecutorContext getParent() {
      return parent;
    }

    @Override
    public ExecutorContext setParent(ExecutorContext context) {
      ExecutorContext ret = parent;
      this.parent = context;
      return ret;
    }

    public int execute(List<CySeCLineAtom> atomList, CoachContext coachContext) throws ExecutorException {
      synchronized (executorLock) {
        int ret = 0;
        executedNames.clear();
        ExecutorException retException = null;
        for (CySeCLineAtom la : atomList) {
          try {
            Atom condResult = la.getCond().execute(coachContext);
            if (condResult.isTrue(coachContext) && !executedNames.contains(la.getName())) {
              la.execute(coachContext);
              ret++;
              executedNames.add(la.getName());
            }
          } catch (ExecutorException ee) {
            logger.log(Level.WARNING, "Exception during execution", ee);
            retException = new ExecutorException(ee.getReason(), retException);
          }
        }
        if (retException != null) {
          throw retException;
        }
        return ret;
      }
    }
  }

  // Since the context exists once per classloader, the map isn't necessary as there will always be a 1:1 relation
  // of library and context. This is an implementation detail of the platform though, therefore we keep the hashmap
  // to remain independant of future change
  private static final Map<String, CySeCExecutorContext> contextMap = new HashMap<>();

  public static CySeCExecutorContext getExecutorContext(String contextId) {
    return getExecutorContext(contextId, null);
  }

  public static CySeCExecutorContext getExecutorContext(String contextId, Logger log) {
    synchronized (contextMap) {
      if (contextMap.get(contextId.toLowerCase()) == null) {
        contextMap.put(contextId.toLowerCase(), new CySeCExecutorContext(contextId, log));
      }
      return contextMap.get(contextId.toLowerCase());
    }
  }

}
