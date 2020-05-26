package eu.smesec.library.parser;

import eu.smesec.bridge.FQCN;
import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Option;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.questions.LibQuestion;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Capsules the context information needed for a command to execute
 */
public class CoachContext {
  private ExecutorContext context;
  private ILibCal cal;
  private Question questionContext;
  private Optional<Answer> answerContext;
  private Questionnaire coach;
  private Logger logger;
  private FQCN fqcn;


  public CoachContext(ExecutorContext context, ILibCal cal, Question questionContext, Optional<Answer> answerContext, Questionnaire coach, FQCN fqcn) {
    this.context = context;
    this.cal = cal;
    this.questionContext = questionContext;
    this.answerContext = answerContext;
    this.coach = coach;
    this.fqcn = fqcn;
  }

  public FQCN getFqcn() {
    return fqcn;
  }

  public Optional<Answer> getAnswerContext() {
    return answerContext;
  }

  public ExecutorContext getContext() {
    return context;
  }

  public void setContext(ExecutorContext context) {
    this.context = context;
  }

  public ILibCal getCal() {
    return cal;
  }

  public Question getQuestionContext() {
    return questionContext;
  }

  public Logger getLogger() {
    return logger;
  }

  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  public Questionnaire getCoach() {
    return coach;
  }

  public CoachContext copy() {
    CoachContext coachContext = new CoachContext(context, cal, questionContext, answerContext, coach, fqcn);
    coachContext.setLogger(logger);
    return coachContext;
  }

}
