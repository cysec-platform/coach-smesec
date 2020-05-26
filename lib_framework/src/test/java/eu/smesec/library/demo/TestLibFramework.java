package eu.smesec.library.demo;

import eu.smesec.bridge.Command;
import eu.smesec.bridge.FQCN;
import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.Library;
import eu.smesec.bridge.execptions.CacheException;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.bridge.generated.Questions;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.Score;
import eu.smesec.library.parser.Atom;
import eu.smesec.library.parser.CySeCExecutorContextFactory;
import eu.smesec.library.parser.ExecutorContext;
import eu.smesec.library.parser.TestCommands;
import eu.smesec.library.utils.Utils;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestLibFramework {
    private ILibCal libcal;
    private Questionnaire coach;
    private Questionnaire duplicateCoach;
    private AbstractLib library;
    private AbstractLib duplicateLibrary;
    private String libId = "eu.smesec.library.demo.MockLibrary";
    private Logger logger;
    private Unmarshaller unmarshaller;
    private ExecutorContext context;
    private FQCN fqcn = FQCN.fromString("lib-demo");

    @Before
    public void setup() {
        library = new MockLibrary();
        duplicateLibrary = new MockLibrary();
        libcal = mock(ILibCal.class);
        coach = new Questionnaire();
        duplicateCoach = new Questionnaire();
        logger = Logger.getGlobal();

        coach = readCoachFromFile();
        library.init(libId, coach, libcal, logger);

        context = CySeCExecutorContextFactory.getExecutorContext(coach.getId());
    }

    private Questionnaire readCoachFromFile() {
        try (BufferedInputStream is = new BufferedInputStream(getClass().getResourceAsStream("/user_training.xml"))) {
            JAXBContext jc = JAXBContext.newInstance(Questionnaire.class);
            unmarshaller = jc.createUnmarshaller();
            return (Questionnaire) unmarshaller.unmarshal(is);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @After
    public void tearDown() {
        library = null;
        context.reset();
        context = null;
        System.gc();
    }

    @Test
    public void testInit() {
        library = new MockLibrary();
        library.init(libId, coach, libcal, logger);
        int expectedCount = 7;
        assertTrue(library.getQuestions().size() == expectedCount);
    }

    @Test
    public void testGetQuestions() {
        String[] activeQuestions = library.getQuestions().toArray(new String[0]);
        String[] qids = new String[]{"user-q10", "user-q30", "user-q40", "user-q50", "user-q60", "user-q70", "user-q80"};

        Assert.assertEquals("Size of arrays must be correct", activeQuestions.length, qids.length);
        Assert.assertArrayEquals("Elements should be in same order", activeQuestions, qids);
    }

    // TODO: Update this test
/*    @Test
    public void testGetQuestionsWithBranches() {
        library.init(libId, coach, libcal, logger);

        // Prepare q50 state to return multiple next ids
        LibQuestion q70 = library.getQuestion("user-q70");
        q70.notify("user-q70o1", "user-q70");
        q70.notify("user-q70o2", "user-q70");
        q70.notify("user-q70o3", "user-q70");
        q70.notify("user-q70o4", "user-q70");

        String[] activeQuestions = library.getQuestions();
        String[] qids = new String[]{"user-q10", "user-q30", "user-q40", "user-q50", "user-q60", "user-q70", "user-q90", "user-q100", "user-q110", "user-q80"};



        Assert.assertEquals("Size of arrays must be correct", activeQuestions.length, qids.length);
        Assert.assertArrayEquals("Elements should be in same order", activeQuestions, qids);
    }*/

    @Test
    // Exception can't occur as CAL is mocked. only here to satisfy compiler
    public void testOnResponseChangeHook() throws CacheException {
        Question question = Utils.findById(coach, "user-q10");
        Answer answer = new Answer();
        answer.setText("user-q10o1");
        answer.setQid(question.getId());

        when(libcal.getAnswer(fqcn.toString(), "user-q10")).thenReturn(answer);

        library.onResponseChange(question, answer, fqcn);
        List<String> qs = library.getQuestions();
        Assert.assertTrue(qs.size() == 8);
    }

    @Test
    public void testOnBeginHook() {
        Question question = new Question();
        question.setId("user-q10");
        Answer answer = new Answer();
        answer.setText("user-q10o1");
        answer.setQid(question.getId());

        List<Command> commands = library.onBegin(fqcn);

        assertNotNull(commands);
    }

    @Test
    public void testSetParent() {
        Questionnaire subcoach = new Questionnaire();
        subcoach.setId("my-subcoach2");
        subcoach.setParent("lib-company");
        Library mockLibrary = new MockLibrary();
        mockLibrary.init("eu.smesec.library.MockLibrary", subcoach, libcal, Logger.getGlobal());

        mockLibrary.setParent(library.getExecutorContext());
        ((AbstractLib)mockLibrary).getExecutorContext().reset();
    }

    @Test
    public void testFailSetSameParent() {
        Questionnaire subcoach = new Questionnaire();
        subcoach.setId("my-subcoach-unique");
        subcoach.setParent("lib-company");
        Library mockLibrary = new MockLibrary();
        mockLibrary.init("eu.smesec.library.MockLibrary", subcoach, libcal, Logger.getGlobal());

        try {
            library.setParent(library.getExecutorContext());
            fail();
        } catch (IllegalStateException ise) {
            System.out.println(ise);
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @Ignore(value = "Multiple set now allowed")
    public void testFailSetMultipleParent() {
        Questionnaire subcoach = new Questionnaire();
        subcoach.setId("my-subcoach");
        subcoach.setParent("lib-company");
        Library mockLibrary = new MockLibrary();
        mockLibrary.init("eu.smesec.library.MockLibrary", subcoach, libcal, Logger.getGlobal());
        duplicateCoach = readCoachFromFile();
        // artificially change coach id, otherwise same executor context returned
        duplicateCoach.setId(duplicateCoach.getId() + "2");
        duplicateLibrary.init(libId, duplicateCoach, libcal, logger);

        try {
            mockLibrary.setParent(library.getExecutorContext());
            mockLibrary.setParent(duplicateLibrary.getExecutorContext());
            fail();
        } catch (IllegalStateException ise) {
            System.out.println(ise);
            assertTrue(true);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected exception", e);
            fail();
        }
    }

    @Test
    public void testColdStart() throws Exception {
        Question question = new Question();
        question.setId("user-q70");
        Answer answer = new Answer();
        answer.setText("user-q70o1");
        answer.setQid(question.getId());
        answer.setAidList("user-q70o1 user-q70o2 user-q70o3");
        answer.setQid(question.getId());

        List<Answer> answers = new ArrayList<>();
        answers.add(answer);

        answer = new Answer();
        answer.setText("user-q90o1");
        answer.setQid(question.getId());
        answer.setQid("user-q90");
        answers.add(answer);

        answer.setText("user-q90o1");
        answer.setQid(question.getId());
        answer.setQid("user-q90");
        answers.add(answer);

        when(libcal.getAllAnswers()).thenReturn(answers);

        List<Command> commands = library.onResume("user-q70", fqcn);
        Question questionTest = Utils.findById(coach, "user-q70");

        // test all options selected


        //assertTrue(library.getQuestion("user-q90").isAnswered());
        assertNotNull(commands);
    }

    @Test
    public void testGetResource() {
        String path = "user_training.xml";
        List<String> list = new ArrayList<>(Arrays.asList(path));
        InputStream is = library.getResource(list);

        Assert.assertNotNull(is);
    }

    @Test
    public void testSummaryPageModel() {
        // demo library does have summary page, mock CAL behaviour
        when(libcal.checkResource(anyString(), anyString(), anyString())).thenReturn(true);
        Question question = new Question();
        question.setId("user-q80");
        Answer answer = new Answer();
        answer.setText("user-q80o1");
        answer.setQid(question.getId());
        int expected = 1;


    }

    @Test
    @Ignore(value = "Test fails on Jenkins but doesnt on Windows")
    public void testSummaryPageContent() throws CacheException {
        // demo library does have summary page, mock CAL behaviour
        when(libcal.checkResource(anyString(), anyString(), anyString())).thenReturn(true);
        when(libcal.getAnswer(anyString())).thenReturn(new Answer());
        Question question = new Question();
        question.setId("user-q10");
        Answer answer = new Answer();
        answer.setText("user-q10o1");
        answer.setQid(question.getId());
        // Trigger setting of skill max values
        library.onResponseChange(question, answer, fqcn);

        int expected = 2;
        answer.setText("user-q80o1");
        answer.setQid("user-q80");
        question.setId("user-q80");
        List<Command> commands = library.onResponseChange(question, answer, fqcn);
        Assert.assertNotNull(commands);
        Assert.assertEquals(expected, commands.size());
    }

    @Test
    @Ignore(value = "Cant mock Context")
    public void testGetNextQuestionInArray() {
        Question question = new Question();
        question.setId("user-q10");
        // Depends on logic defined in test coach, might break in future
        String nextId = "user-q30";
        Question next = library.getNextQuestion(question, fqcn);
        assertNotNull(next);
        assertEquals(nextId, next.getId());
    }

    @Test
    @Ignore(value = "Cant mock Context")
    public void testGetNextQuestionInVariable() throws Exception {
        Question question = new Question();
        question.setId("user-q10");
        String nextId = "user-q50";
        String variable = "_coach.nextPage";
        Atom nextAtom = new Atom(Atom.AtomType.STRING, nextId, null);

        // Save efforts of providing cysec context mock
        // mock cysec context to enable stubbing of getVariable method
        /*context = Mockito.mock(CySeCExecutorContextFactory.CySeCExecutorContext.class);

        // mock static method getContext to inject cysec context mock
        PowerMockito.mockStatic(CySeCExecutorContextFactory.class);
        Mockito.when(CySeCExecutorContextFactory.getExecutorContext(anyString(), any(Logger.class))).thenReturn((CySeCExecutorContextFactory.CySeCExecutorContext) context);

        Atom nextAtom = new Atom(Atom.AtomType.STRING, "user-q160", null);
        when(context.getVariable("_coach.nextPage", "user-q20"))
                .thenReturn(nextAtom);*/

        Question next = library.getNextQuestion(question, fqcn);
        assertNotNull(next);
        assertEquals(nextId, next.getId());

        // Overwrite variable and make sure change is visible
        nextId = "user-q90";
        nextAtom = new Atom(Atom.AtomType.STRING, nextId, null);
        context.setVariable("_coach.nextPage", new Atom(Atom.AtomType.STRING, nextId, null), "user-q10");
        next = library.getNextQuestion(question, fqcn);
        assertNotNull(next);
        assertEquals(nextId, next.getId());

        // And back once again
        nextId = "user-q50";
        nextAtom = new Atom(Atom.AtomType.STRING, nextId, null);
        context.setVariable("_coach.nextPage", new Atom(Atom.AtomType.STRING, nextId, null), "user-q10");
        next = library.getNextQuestion(question, fqcn);
        assertNotNull(next);
        assertEquals(nextId, next.getId());
    }


    @Test
    @Ignore(value = "Read below javadoc")
    /**
     * Mockitor throws the below error even though the mocked method is not void. In addition, this code line is similarly
     * used in another test {@link TestCommands#setup()}
     *
     * org.mockito.exceptions.misusing.CannotStubVoidMethodWithReturnValue:
     * 'registerResources' is a *void method* and it *cannot* be stubbed with a *return value*!
     * Voids are usually stubbed with Throwables:
     *     doThrow(exception).when(mock).someVoidMethod();
     * If you need to set the void method to do nothing you can use:
     *     doNothing().when(mock).someVoidMethod();
     * For more information, check out the javadocs for Mockito.doNothing().
     * ***
     * If you're unsure why you're getting above error read on.
     * Due to the nature of the syntax above problem might occur because:
     * 1. The method you are trying to stub is *overloaded*. Make sure you are calling the right overloaded version.
     * 2. Somewhere in your test you are stubbing *final methods*. Sorry, Mockito does not verify/stub final methods.
     * 3. A spy is stubbed using when(spy.foo()).then() syntax. It is safer to stub spies -
     *    - with doReturn|Throw() family of methods. More in javadocs for Mockito.spy() method.
     * 4. Mocking methods declared on non-public parent classes is not supported.
     */
    public void testPeekQuestions() {
        Question question1 = new Question();
        question1.setId("user-q10");
        Question question2 = new Question();
        question1.setId("user-q20");
        Question question3 = new Question();
        question1.setId("user-q30");
        Questions questions = new Questions();
        List<Question> expectedList = Arrays.asList(question1, question2, question3);
        questions.getQuestion().addAll(expectedList);

        Mockito.when(coach.getQuestions()).thenReturn(questions);

        List<Question> sequence = library.peekQuestions(question1);
        assertNotNull(sequence);
        Assert.assertThat("Elements should be in same order", sequence, is(expectedList));
    }

    @Test
    public void testGetLastQuestion() {
        Question question = new Question();
        question.setId("user-q10");
        Question lastQuestion = library.getLastQuestion();
        assertNotNull(lastQuestion);
    }

    @Test
    public void testCreateMetadata() {
        Question question = new Question();
        question.setId("user-q10");
        Answer answer = new Answer();
        answer.setText("user-q10o1");
        answer.setQid(question.getId());

        List<Command> commands = library.onBegin(fqcn);

        assertNotNull(commands);
    }

    @Test
    @Ignore(value = "Moved here from old class, to be updated")
    public void testCaluclateGrade() {
        Score score = new Score(400);
        score.add(250);

        // TODO: Move this test into AbstractLib
        int gpa = (int) ((double) (score.getValue()) / ((double) score.getMaxValue()) * 5);
        String[] gradeLetters = {"f", "e", "d", "c", "b", "a"};
        String grade = gradeLetters[gpa];

        TestCase.assertEquals("Grade should be c", "c", grade);
    }

}