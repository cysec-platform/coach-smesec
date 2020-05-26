package eu.smesec.library.parser;

import eu.smesec.bridge.FQCN;
import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.Library;
import eu.smesec.bridge.execptions.CacheException;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.bridge.generated.Questions;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.demo.MockLibrary;
import eu.smesec.library.questions.LibQuestion;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

public class TestCommands {
  private CoachContext coachContext;
  private CySeCExecutorContextFactory.CySeCExecutorContext context;
  private Questionnaire coach;
  private ILibCal cal;
  private Question question;
  private Answer answer;
  private FQCN fqcn = FQCN.fromString("lib-user");

  @Before
  public void setup() throws Exception{
    cal = Mockito.mock(ILibCal.class);
    context = CySeCExecutorContextFactory.getExecutorContext("test");
    answer = new Answer();
    answer.setQid("user-q20");
    answer.setText("user-q20o1");
    question = new Question();
    question.setId("user-q20");
    Questions questions = new Questions();
    questions.getQuestion().add(question);
    coach = Mockito.mock(Questionnaire.class);
    when(coach.getQuestions()).thenReturn(questions);
    coachContext = new CoachContext(context, cal, question, Optional.ofNullable(answer), coach, fqcn);
    // pass global logger
    coachContext.setLogger(Logger.getGlobal());
  }

  @After
  public void tearDown() {
    context.reset();
    coachContext = null;

  }

  @Test
  public void testBasicAddCommand() throws Exception {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("add", new CommandAdd());
    Atom a = new ParserLine("add(5,5.5,add(-.25,-1.25));").getAtom();
    String expected = "add( 5, 5.5, add( -0.25, -1.25 ) )";
    assertTrue("String is not as expected \"" + a + "\"!=\"" + expected + "\"", expected.equals(a.toString()));
    assertTrue("Execution result is not as expected (" + a.execute(coachContext) + ")", "9.0".equals(a.execute(coachContext).toString()));
  }

  @Test
  public void testIsSelectedFalseCommand() throws Exception {
    //For testSelectedFalse
    when(coachContext.getCal().getAnswer(fqcn.toString(), coachContext.getQuestionContext())).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("isSelected", new CommandIsSelected());
    try {
      StringBuilder s = new StringBuilder();
      s.append("isSelected(\"company-q10o1\") : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 0);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void test() {
    String content = "company-q10oNone";
    String content2 = "company-q10o1";
    String expected = "company-q10";

    String regex = "[^0-9]*[q]\\d+";

    Pattern pattern = Pattern.compile(regex);
    Matcher match = pattern.matcher(content);
    match.find();
    Assert.assertEquals(expected, content.substring(match.start(),match.end()));
    Matcher match2 = pattern.matcher(content2);
    match2.find();
    Assert.assertEquals(expected, content2.substring(match2.start(),match2.end()));
  }

  @Test
  public void testIsSelectedTrueCommand() throws Exception {
    Answer answerQ20 = new Answer();
    answerQ20.setText("user-q20o1");
    // Override answer in coachContext
    coachContext = new CoachContext(context, cal, question, Optional.ofNullable(answerQ20), coach, fqcn);
    coachContext.setLogger(Logger.getGlobal());
    when(coachContext.getCal().getAnswer(fqcn.toString(), "user-q20")).thenReturn(answerQ20);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("isSelected", new CommandIsSelected());
    try {
      StringBuilder s = new StringBuilder();
      s.append("isSelected(\"user-q20o1\") : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testIsSelectedMultiOptionsCommand() throws Exception {
    Answer answerQ70 = new Answer();
    answerQ70.setAidList("user-q70o1 user-q70o2");
    coachContext = new CoachContext(context, cal, question, Optional.ofNullable(answerQ70), coach, fqcn);
    coachContext.setLogger(Logger.getGlobal());
    when(coachContext.getCal().getAnswer(fqcn.toString(), "user-q70")).thenReturn(answerQ70);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("isSelected", new CommandIsSelected());
    try {
      StringBuilder s = new StringBuilder();
      s.append("isSelected(\"user-q70o1\") : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }


  @Test
  public void testIsAnsweredCommand() throws Exception {
    // Needs different coachcontext (answer == null)
    coachContext = new CoachContext(context, cal, question, Optional.ofNullable(answer), coach, fqcn);
    coachContext.setLogger(Logger.getGlobal());

    when(coachContext.getCal().getAnswer(fqcn.toString(), "user-q10")).thenReturn(new Answer());
    when(coachContext.getCal().getAnswer(fqcn.toString(), "user-q50")).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("isAnswered", new CommandIsAnswered());
    try {
      StringBuilder s = new StringBuilder();
      s.append("isAnswered(\"user-q10\") : condition1 : {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\", 20);" + System.lineSeparator());
      s.append("              };" + System.lineSeparator());
      s.append("not(isAnswered(\"user-q10\")) : condition2 : {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\", 100);" + System.lineSeparator());
      s.append("              };"  + System.lineSeparator());
      s.append("isAnswered(\"user-q50\") : condition3 : {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\", 30);" + System.lineSeparator());
      s.append("              };");

      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      // first block executed, but not second and not 3rd, resulting in score == 20
      assertTrue(context.getScore("myScore").getValue() == 20);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testSetHiddenCommand() {
    Question question = coachContext.getQuestionContext();
    question.setHidden(true);
    Command.registerCommand("setHidden", new CommandSetHidden());

    assertTrue(coachContext.getQuestionContext().isHidden());
    try {
      // hide question
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 setHidden(\"user-q20\", TRUE);" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      assertTrue(coachContext.getQuestionContext().isHidden());

      // unhide question
      StringBuilder s2 = new StringBuilder();
      s2.append("TRUE : bla :  {" + System.lineSeparator());
      s2.append("                 setHidden(\"user-q20\", FALSE);" + System.lineSeparator());
      s2.append("              }; // This is a silly comment");
      System.out.println("testing " + s2);
      List<CySeCLineAtom> l2 = new ParserLine(s2.toString()).getCySeCListing();

      context.executeQuestion(l2, coachContext);
      assertFalse(coachContext.getQuestionContext().isHidden());

      //Atom result = l.get(0).execute(coachContext);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testIfCommand() {
    Command.registerCommand("if", new CommandIf());
    try {
      StringBuilder s = new StringBuilder();
            /*
               Syntax:
               if(<condition>, <true>, <false>)
             */
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 if(TRUE, addScore(\"myScore\",100), addScore(\"myScore\",200));" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testBadgeCommands() {
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 addBadge(\"Badge1\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
      s.append("                 addBadgeClass(\"Badge1\",\"gold\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
      s.append("                 addBadgeClass(\"Badge1\",\"silver\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
      s.append("                 addBadgeClass(\"Badge1\",\"bronce\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
      s.append("                 awardBadge(\"Badge1\",\"silver\");" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertTrue("bad badge is awarded (is:"+context.getBadge("Badge1").getAwardedBadgeClass()+")","silver".equals(context.getBadge("badge1").getAwardedBadgeClass().getId()));

      // award another badge
      s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 awardBadge(\"Badge1\",\"gold\");" + System.lineSeparator());
      s.append("              };");
      l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertTrue("bad Badge is awarded (is:"+context.getBadge("Badge1").getAwardedBadgeClass()+")","gold".equals(context.getBadge("Badge1").getAwardedBadgeClass().getId()));
      assertTrue("Badge list is bad (is:"+context.getBadgeList().length+")",context.getBadgeList().length==1);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

    @Test
    public void testRevokeBadgeCommand() {
        try {
            StringBuilder s = new StringBuilder();
            s.append("TRUE : bla :  {" + System.lineSeparator());
            s.append("                 addBadge(\"Badge1\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
            s.append("                 addBadgeClass(\"Badge1\",\"gold\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
            s.append("                 awardBadge(\"Badge1\",\"gold\");" + System.lineSeparator());
            s.append("              };");
            List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
            context.executeQuestion(l, coachContext);
            assertTrue("bad badge! is awarded (is:"+context.getBadge("Badge1").getAwardedBadgeClass()+")","gold".equals(context.getBadge("badge1").getAwardedBadgeClass().getId()));

            // revoke existing badeClass
            s = new StringBuilder();
            s.append("TRUE : bla :  {" + System.lineSeparator());
            s.append("                 revokeBadge(\"Badge1\");" + System.lineSeparator());
            s.append("              };");
            l = new ParserLine(s.toString()).getCySeCListing();
            context.executeQuestion(l, coachContext);
            // expect current class to be null now
            assertNull(
                    "bad Badge! is awarded (is:"+context.getBadge("Badge1").getAwardedBadgeClass()+")",
                    context.getBadge("Badge1").getAwardedBadgeClass());

            // expect badge list to still hold 1 entry
            assertTrue("Badge list is bad! (is:"+context.getBadgeList().length+")",
                    context.getBadgeList().length==1);
        } catch (Exception pe) {
            pe.printStackTrace();
            fail("got unexpected exception " + pe);
        }
    }

    @Test(expected = ExecutorException.class)
    @Ignore(value = "Pending change to remove this behavior")
    public void testRevokeNonExistingBadge() throws ExecutorException{

        try {
            // try to revoke class from non-existing badge
            StringBuilder s = new StringBuilder();
            s.append("TRUE : bla :  {" + System.lineSeparator());
            s.append("                 revokeBadge(\"Badge1\");" + System.lineSeparator());
            s.append("              };");
            List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
            context.executeQuestion(l, coachContext);

            // Satify constructor call from ParserLine and executeQuestion()
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (ExecutorException e) {
            // expect badge list to be empty
            assertTrue("Badge list is bad! (is:"+context.getBadgeList().length+")",
                    context.getBadgeList().length==0);
            throw e;
        }
    }

  @Test
  public void testRecommendationCommands() {
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 addRecommendation(\"rec1\",0,\"TestUrl\",\"TestAlt\",\"TestTitle\",\"TestDescription\",\"TextLink\",\"TestLink\");" + System.lineSeparator());
      s.append("                 addRecommendation(\"rec2\",0,\"TestUrl2\",\"TestAlt2\",\"TestTitle2\",\"TestDescription2\",\"TextLink2\",\"TestLink2\");" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertTrue("Recommendation is not (is:"+context.getRecommendation("rec1")+")",context.getRecommendation("rec1")!=null);

      assertTrue("Recommendation list is bad (is:"+context.getRecommendationList().length+")",context.getRecommendationList().length==1);
      assertTrue("Recommendation content is bad","TestTitle".equals(context.getRecommendation("rec1").getTitle()));
      assertTrue("Recommendation content is bad (2)","TestTitle2".equals(context.getRecommendation("rec2").getTitle()));
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testCreateSubcoachCommand() throws CacheException {
    Questionnaire coach = new Questionnaire();
    coach.setId("my-subcoach-one");
    coach.setParent("lib-company");
    Mockito.when(coachContext.getCal().getCoach(anyString())).thenReturn(coach);
    Mockito.doNothing().when(coachContext.getCal()).instantiateSubCoach(any(Questionnaire.class), any(Set.class));
    Library mockLibrary = new MockLibrary();
    mockLibrary.init("eu.smesec.library.MockLibrary", coach, cal, Logger.getGlobal());

    List<Library> libraries = new ArrayList<>();
    libraries.add(mockLibrary);
    Mockito.when(coachContext.getCal().getLibraries(anyString())).thenReturn(libraries);

    Command.registerCommand("createSubcoach", new CommandCreateSubcoach());
    try {
      StringBuilder s = new StringBuilder();

      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 createSubcoach(\"my-subcoach\", \"file-segment\");" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      // assert libcal called


      // assert parent context is correct
      assertEquals(((AbstractLib)mockLibrary).getExecutorContext().getParent(), context);

    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testSetNext() {
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 setNext(\"q30\");" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertEquals("q30", context.getVariable("_coach.nextPage", question.getId()).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testCreateInexistantSubcoachCommand() throws CacheException {
    Command.registerCommand("createSubcoach", new CommandCreateSubcoach());

    Mockito.when(coachContext.getCal().getCoach(anyString())).thenReturn(null);
    try {
      StringBuilder s = new StringBuilder();

      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 createSubcoach(\"my-subcoach-fail\", \"file-segment\");" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      fail();
    } catch (ParserException | ExecutorException pe) {
      assertEquals("Coach id my-subcoach-fail does not exist", pe.getMessage());
    }

  }
}
