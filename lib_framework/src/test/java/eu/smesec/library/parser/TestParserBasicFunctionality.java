package eu.smesec.library.parser;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Question;
import eu.smesec.library.questions.LibQuestion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.logging.Logger;

import static eu.smesec.library.parser.Atom.NULL_ATOM;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class TestParserBasicFunctionality {
  private CoachContext coachContext;
  private ExecutorContext context;

  @Before
  public void setup() {
    ILibCal cal = Mockito.mock(ILibCal.class);
    Question question = Mockito.mock(Question.class);
    when(question.getId()).thenReturn("user-q10");
    coachContext = new CoachContext(CySeCExecutorContextFactory.getExecutorContext("test"), cal, question, null, null, null);
    coachContext.setLogger(Logger.getGlobal());
    context = coachContext.getContext();
  }

  @After
  public void tearDown() {
    context.reset();
    coachContext = null;
  }

  @Test
  public void testBasicAddCommand() throws Exception {
    Command.registerCommand("add", new CommandAdd());
    Atom a = new ParserLine("add(5,5.5,add(-.25,-1.25));").getAtom();
    String expected = "add( 5, 5.5, add( -0.25, -1.25 ) )";
    assertTrue("String is not as expected \"" + a + "\"!=\"" + expected + "\"", expected.equals(a.toString()));
    assertTrue("Execution result is not as expected (" + a.execute(coachContext) + ")", "9.0".equals(a.execute(coachContext).toString()));
  }

  @Test
  public void testCommentRobustness() {
    Command.registerCommand("add", new CommandAdd());
    try {
      String s = "// a comment\r\nadd(//\r\n3//\r\n//\r\n//\r\n)//\r\n : //\r\nBla//\r\n//\r\n ://\r {//\nadd(3);\r\n}\r\n;";
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s).getCySeCListing();
    } catch (ParserException pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testSeriousCysec() {
    Command.registerCommand("add", new CommandAdd());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {\r\n");
      s.append("                 addScore(\"myScore\",100);\r\n");
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testSegmentRetrieval() {
    try {
      ParserLine p = new ParserLine("\"a\"");
      Atom a = p.getAtom();
      assertTrue("content missmatch for string", "a".equals(a.getId()));
      assertTrue("type missmatch for string", a.getType() == Atom.AtomType.STRING);
    } catch (ParserException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

  @Test
  public void testIfEvaluation() {
    try {
      context.reset();
      String s = "if( TRUE, 5, 10)";
      Atom a = new ParserLine(s).getAtom().execute(coachContext);
      assertTrue("content missmatch for string (true condition not as expected)", "5".equals(a.getId()));
      assertTrue("type missmatch for string", a.getType() == Atom.AtomType.INTEGER);
      context.reset();
      s = "if( FALSE, 5, 10)";
      a = new ParserLine(s).getAtom().execute(coachContext);
      assertTrue("content missmatch for string (false condition not as expected)", "10".equals(a.getId()));
      context.reset();
      s = "if( and(FALSE,FALSE), 5, 10)";
      a = new ParserLine(s).getAtom().execute(coachContext);
      assertTrue("content missmatch for string (condition not evaluated properly? a=" + a + ")", "10".equals(a.getId()));
      context.reset();
      s = "if( and(FALSE,FALSE), addScore(\"test\",5), addScore(\"test\",10))";
      a = new ParserLine(s).getAtom().execute(coachContext);
      assertTrue("content missmatch for string (eval test 1; score=" + context.getScore("test").getValue() + ")", context.getScore("test").getValue() == 10);
      context.reset();
      s = "if( and(TRUE,TRUE), addScore(\"test\",5), addScore(\"test\",10))";
      a = new ParserLine(s).getAtom().execute(coachContext);
      assertTrue("content missmatch for string (eval test 2; score=" + context.getScore("test").getValue() + ")", context.getScore("test").getValue() == 5);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

  @Test
  public void testCySeCEvalOrder() {
    StringBuilder sb = new StringBuilder();
    sb.append("TRUE : default : {" + System.lineSeparator());
    sb.append("  addScore(\"test\", 1);" + System.lineSeparator());
    sb.append("  addScore(\"test\", 1);" + System.lineSeparator());
    sb.append("};" + System.lineSeparator());
    sb.append("or(FALSE,FALSE) :q30o1: {" + System.lineSeparator());
    sb.append("  addScore(\"test\", 3);" + System.lineSeparator());
    sb.append("  addScore(\"test\", 3);" + System.lineSeparator());
    sb.append("};" + System.lineSeparator());
    sb.append("and(FALSE,FALSE) :q30o1: {" + System.lineSeparator());
    sb.append("  addScore(\"test\", 11);" + System.lineSeparator());
    sb.append("  addScore(\"test\", 11);" + System.lineSeparator());
    sb.append("};" + System.lineSeparator());
    try {
      context.executeQuestion(new ParserLine(sb.toString()).getCySeCListing(), coachContext);
      assertTrue("content missmatch for string (eval test 1; score=" + context.getScore("test").getValue() + ")", context.getScore("test").getValue() == 2);
      context.executeQuestion(new ParserLine(sb.toString().replaceAll("or\\(FALSE,FALSE\\)", "TRUE")).getCySeCListing(), coachContext);
      assertTrue("content missmatch for string (eval test 2; score=" + context.getScore("test").getValue() + ")", context.getScore("test").getValue() == 8);
      context.reset();
      context.executeQuestion(new ParserLine(sb.toString().replaceAll("and\\(FALSE,FALSE\\)",
              "TRUE")).getCySeCListing(), coachContext);
      assertTrue("content missmatch for string (eval test 3; score=" + context.getScore("test").getValue() + ")", context.getScore("test").getValue() == 24);
      context.executeQuestion(new ParserLine(sb.toString().replaceAll("or\\(FALSE,FALSE\\)", "TRUE").replaceAll("and\\(FALSE,FALSE\\)", "TRUE")).getCySeCListing(), coachContext);
      assertTrue("content missmatch for string (eval test 4; score=" + context.getScore("test").getValue() + ")", context.getScore("test").getValue() == 8);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

  @Test
  public void testBooleanEvaluation() {
    try {
      ParserLine p = new ParserLine("TRUE");
      Atom a = p.getAtom();
      assertTrue("content missmatch for bool", a.isTrue(coachContext));

      p = new ParserLine("and(TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for and test 1", a.isTrue(coachContext));
      p = new ParserLine("and(TRUE,TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for and test 2", a.isTrue(coachContext));
      p = new ParserLine("and(TRUE,TRUE,TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for and test 3", a.isTrue(coachContext));
      p = new ParserLine("and(TRUE,TRUE,FALSE)");
      a = p.getAtom();
      assertTrue("content missmatch for and test 4", !a.isTrue(coachContext));
      p = new ParserLine("and(TRUE,FALSE,TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for and test 5", !a.isTrue(coachContext));
      p = new ParserLine("and(TRUE,FALSE,TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for and test 6", !a.isTrue(coachContext));

      p = new ParserLine("or(TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for or test 1", a.isTrue(coachContext));
      p = new ParserLine("or(TRUE,TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for or test 2", a.isTrue(coachContext));
      p = new ParserLine("or(FALSE,TRUE,TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for ox test 3", a.isTrue(coachContext));
      p = new ParserLine("or(TRUE,TRUE,FALSE)");
      a = p.getAtom();
      assertTrue("content missmatch for or test 4", a.isTrue(coachContext));
      p = new ParserLine("or(TRUE,FALSE,FALSE)");
      a = p.getAtom();
      assertTrue("content missmatch for or test 5", a.isTrue(coachContext));
      p = new ParserLine("or(FALSE,FALSE,FALSE)");
      a = p.getAtom();
      assertTrue("content missmatch for or test 6", !a.isTrue(coachContext));

      p = new ParserLine("xor(TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for xor test 1", a.isTrue(coachContext));
      p = new ParserLine("xor(TRUE,TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for xor test 2", !a.isTrue(coachContext));
      p = new ParserLine("xor(FALSE,TRUE,TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for xor test 3", !a.isTrue(coachContext));
      p = new ParserLine("xor(TRUE,TRUE,FALSE)");
      a = p.getAtom();
      assertTrue("content missmatch for xor test 4", !a.isTrue(coachContext));
      p = new ParserLine("xor(FALSE, TRUE, FALSE)");
      a = p.getAtom();
      assertTrue("content missmatch for xor test 5", a.isTrue(coachContext));
      p = new ParserLine("xor(FALSE,FALSE,FALSE)");
      a = p.getAtom();
      assertTrue("content missmatch for xor test 6", !a.isTrue(coachContext));

      p = new ParserLine("not(TRUE)");
      a = p.getAtom();
      assertTrue("content missmatch for not test 1", !a.isTrue(coachContext));
      p = new ParserLine("not(FALSE)");
      a = p.getAtom();
      assertTrue("content missmatch for not test 2", a.isTrue(coachContext));

    } catch (ParserException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    } catch (ExecutorException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }

    // boolean commands do require at least one parameter or they will fail
    try {
      ParserLine p = new ParserLine("and()");
      Atom a = p.getAtom();
      a.isTrue(coachContext);
      fail("got unexpected result when testing and without a parameter");
    } catch (ExecutorException pe) {
      // this is expected
    } catch (ParserException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }

    // NOT may not have more than one parameter
    try {
      ParserLine p = new ParserLine("not(TRUE,FALSE)");
      Atom a = p.getAtom();
      a.isTrue(coachContext);
      fail("got unexpected result when testing not");
    } catch (ExecutorException pe) {
      // this is expected
    } catch (ParserException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

  @Test
  public void testNull() {
    try {
      Atom a = NULL_ATOM;
      a.execute(coachContext);
    } catch (ExecutorException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

  @Test
  public void testConcatenation() {
    try {
      ParserLine p = new ParserLine("concat(\"a\")");
      Atom a = p.getAtom().execute(coachContext);
      assertTrue("type missmatch for CONCAT(STRING) [" + a.getType() + "]", a.getType() == Atom.AtomType.STRING);
      assertTrue("content missmatch for CONCAT(STRING) [" + a.getType() + "=" + a.getId() + "]", "a".equals(a.getId()));

      p = new ParserLine("concat(3,\"a\")");
      a = p.getAtom().execute(coachContext);
      assertTrue("type missmatch for CONCAT(INT,STRING)", a.getType() == Atom.AtomType.STRING);
      assertTrue("content missmatch for CONCAT(INT,STRING) [" + a.getType() + "]", "3a".equals(a.getId()));

      p = new ParserLine("concat(NULL)");
      a = p.getAtom().execute(coachContext);
      assertTrue("type missmatch for CONCAT(NULL)", a != NULL_ATOM);

    } catch (ParserException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    } catch (ExecutorException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

  @Test
  public void testVariables() {
    try {
      assertTrue("illegal var content (1) [" + context.getVariable("A", null) + "]", context.getVariable("A", null) == NULL_ATOM);
      Atom a = new ParserLine("set(\"A\",\"A\",\"A\")").getAtom();
      a.execute(coachContext);
      assertTrue("illegal var content (2) [" + context.getVariable("A", null) + "]", "A".equals(context.getVariable("A", null).getId()));
      a = new ParserLine("set(\"A\",\"B\",\"B\")").getAtom();
      a.execute(coachContext);
      assertTrue("illegal var content (3) [" + context.getVariable("A", null) + "]", "B".equals(context.getVariable("A", null).getId()));
      assertTrue("illegal var content (4) [" + context.getVariable("A", "A") + "]", "A".equals(context.getVariable("A", "A").getId()));
      assertTrue("illegal var content (5) [" + context.getVariable("A", "B") + "]", "B".equals(context.getVariable("A", "B").getId()));
      a = new ParserLine("set(\"A\",NULL,\"C\")").getAtom();
      a.execute(coachContext);
      assertTrue("illegal var content (6) [" + context.getVariable("A", null) + "]", "C".equals(context.getVariable("A", null).getId()));
      assertTrue("illegal var content (7) [" + context.getVariable("A", "A") + "]", "A".equals(context.getVariable("A", "A").getId()));
      assertTrue("illegal var content (8) [" + context.getVariable("A", "B") + "]", "B".equals(context.getVariable("A", "B").getId()));
      a = new ParserLine("set(\"A\",\"A\",\"D\")").getAtom();
      a.execute(coachContext);
      assertTrue("illegal var content (9) [" + context.getVariable("A", null) + "]", "D".equals(context.getVariable("A", null).getId()));
      assertTrue("illegal var content (10) [" + context.getVariable("A", "A") + "]", "D".equals(context.getVariable("A", "A").getId()));
      assertTrue("illegal var content (11) [" + context.getVariable("A", "B") + "]", "B".equals(context.getVariable("A", "B").getId()));
    } catch (ParserException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    } catch (ExecutorException pe) {
      pe.printStackTrace();
      fail("got unexpected exception");
    }
  }

}
