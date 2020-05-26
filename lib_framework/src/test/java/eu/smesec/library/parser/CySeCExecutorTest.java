package eu.smesec.library.parser;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.library.questions.LibQuestion;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static eu.smesec.library.parser.Atom.NULL_ATOM;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class CySeCExecutorTest {
  private CoachContext coachContext;
  private Question question;

  @Before
  public void setup() {
    ILibCal cal = Mockito.mock(ILibCal.class);
    question = Mockito.mock(Question.class);
    when(question.getId()).thenReturn("qid1");
    ExecutorContext rc = CySeCExecutorContextFactory.getExecutorContext("rootCoach");
    ExecutorContext ic = CySeCExecutorContextFactory.getExecutorContext("intermediateCoach");
    ic.setParent(rc);
    CySeCExecutorContextFactory.getExecutorContext("testCoach").setParent(ic);
    coachContext = new CoachContext(CySeCExecutorContextFactory.getExecutorContext("testCoach"), cal, question, null, null, null);
    // just pass a random logger here for test purposes
    coachContext.setLogger(Logger.getGlobal());

    Command.registerCommand("addScore", new CommandAddScore());
  }

  @After
  public void tearDown() {
    coachContext = null;
  }


  @Test
  public void basicExecutorTest() {
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("TRUE : always1 : set(\"always1\",\"is set\"); // this line is always executed"+System.lineSeparator());
      sb.append("TRUE : always1 : set(\"always1\",\"is set for the second time\"); // this line is never executed"+System.lineSeparator());
      sb.append("FALSE : never : set(\"always2\",\"is set\"); // this line is always executed"+System.lineSeparator());
      sb.append("TRUE : stack : { // this stack is always executed"+System.lineSeparator());
      sb.append("                 setNext(\"qid2\");"+System.lineSeparator());
      sb.append("               };"+System.lineSeparator());
      List<CySeCLineAtom> lines = new ParserLine(sb.toString()).getCySeCListing();
      ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("testCoach");
      context.executeQuestion(lines, coachContext);

      assertTrue("always1 has not been executed", "is set".equals(context.getVariable("always1",null).getId()));
      assertTrue("never has been executed", context.getVariable("never",null)==NULL_ATOM);
      assertTrue("the next id is not set properly", "qid2".equals(context.getVariable("_coach.nextPage",null).getId()));
    } catch(Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

  @Test
  public void executorInheritanceTest() {
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("TRUE : always1 : root.set(\"always1\",\"is set\"); // this line is always executed"+System.lineSeparator());
      sb.append("TRUE : always1 : root.set(\"always1\",\"is set for the second time\"); // this line is never executed"+System.lineSeparator());
      sb.append("TRUE : always2 : parent.set(\"always2\",\"is set\"); // this line is always executed"+System.lineSeparator());
      sb.append("TRUE : always2 : parent.set(\"always2\",\"is set for the second time\"); // this line is never executed"+System.lineSeparator());
      sb.append("FALSE : never : root.set(\"always2\",\"is set\"); // this line is always executed"+System.lineSeparator());
      sb.append("TRUE : stack : { // this stack is always executed"+System.lineSeparator());
      sb.append("                 setNext(\"qid2\");"+System.lineSeparator());
      sb.append("               };"+System.lineSeparator());
      List<CySeCLineAtom> lines = new ParserLine(sb.toString()).getCySeCListing();
      ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("testCoach");
      context.executeQuestion(lines, coachContext);

      ExecutorContext tcontext = CySeCExecutorContextFactory.getExecutorContext("rootCoach");
      ExecutorContext icontext = CySeCExecutorContextFactory.getExecutorContext("intermediateCoach");

      assertTrue("always1 has not been executed", "is set".equals(tcontext.getVariable("always1",null).getId()));
      assertTrue("never has been executed", tcontext.getVariable("never",null)==NULL_ATOM);
      assertTrue("always2 has not been executed", "is set".equals(icontext.getVariable("always2",null).getId()));
      assertTrue("the next id is not set properly", "qid2".equals(context.getVariable("_coach.nextPage",null).getId()));
    } catch(Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

  @Test
  public void testAddScoreToSelf() {
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("TRUE : addScore : root.addScore(\"myScore\", 100);"+System.lineSeparator());

      List<CySeCLineAtom> lines = new ParserLine(sb.toString()).getCySeCListing();
      ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("testCoach");
      context.executeQuestion(lines, coachContext);

      ExecutorContext tcontext = CySeCExecutorContextFactory.getExecutorContext("rootCoach");
      tcontext.getScore("myScore").add("q10", 10);
      ExecutorContext icontext = CySeCExecutorContextFactory.getExecutorContext("intermediateCoach");
      icontext.getScore("myScore").add("q10", 20);

      // own score changed, but parent and root remain unchanged
      Assert.assertEquals(0.0, context.getScore("myScore").getValue(), 0.001);
      Assert.assertEquals(110.0, tcontext.getScore("myScore").getValue(), 0.001);
      Assert.assertEquals(20.0, icontext.getScore("myScore").getValue(), 0.001);
    } catch(Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

}
