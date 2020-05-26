package eu.smesec.library.demo;

import eu.smesec.bridge.FQCN;
import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.execptions.CacheException;
import eu.smesec.bridge.generated.Answer;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.PersistanceManager;
import eu.smesec.library.parser.CySeCExecutorContextFactory;
import eu.smesec.library.parser.ExecutorContext;
import eu.smesec.library.skills.ChangeType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestIntegrateParser {
    private ILibCal libcal;
    private Questionnaire coach;
    private AbstractLib library;
    private String libId = "eu.smesec.library.demo.MockLibrary";
    private Logger logger;
    private Unmarshaller unmarshaller;
    private ExecutorContext context;
    private PersistanceManager pm;
    private Answer answer;
    private FQCN fqcn;
    @Before
    public void setup() {
        library = new MockLibrary();
        libcal = mock(ILibCal.class);
        coach = mock(Questionnaire.class);
        logger = Logger.getGlobal();
        fqcn = FQCN.fromString("lib-demo");

        try (BufferedInputStream is = new BufferedInputStream(getClass().getResourceAsStream("/user_training.xml"))) {
            JAXBContext jc = JAXBContext.newInstance(Questionnaire.class);
            unmarshaller = jc.createUnmarshaller();
            coach = (Questionnaire) unmarshaller.unmarshal(is);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
            fail();
        }
        library.init(libId, coach, libcal, logger);

        context = CySeCExecutorContextFactory.getExecutorContext(coach.getId());
        PersistanceManager persistanceManager = new PersistanceManager(libcal, logger, library);
        pm = spy(persistanceManager);
        // Inject spy object in context
        ((CySeCExecutorContextFactory.CySeCExecutorContext) context).setBadgeListener(pm);
        ((CySeCExecutorContextFactory.CySeCExecutorContext) context).setRecommendationListener(pm);


    }

    @After
    public void tearDown() {
        library = null;
        context.reset();
        context = null;
    }

    // Test integration of Parser module for a simple logic entry
    // Annotated exception can't be thrown as libcal is mocked!
    /*@Test
    public void testRunLogic() throws Exception {
        Question question = new Question();
        question.setId("q20");
        Answer answer = new Answer();
        answer.setText("q20o1");
        answer.setQid(question.getId());
        answer.setQid(question.getId());

        when(libcal.getAnswer(question.getId())).thenReturn(answer);

        LibQuestion libQuestion = library.getQuestion(question.getId());
        try {// pass empty pre and post object
            library.runLogic(libQuestion, Optional.empty(), libQuestion.getLogic("_cysec.logic.default"), Optional.empty());
        } catch (ParserException | ExecutorException e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(context.getScore("myScore").getValue() == 0);
    }

    @Test
    public void testRunPrePostLogic() throws Exception {
        Question question = new Question();
        question.setId("q20");
        Answer answer = new Answer();
        answer.setText("q20o1");
        answer.setQid(question.getId());
        answer.setQid(question.getId());

        Metadata logicMetadata = MetadataBuilder.newInstance(library)
                .setId("_cysec.logic")
                .setMvalue("preQuestion", "TRUE : pre : addScore(\"preScore\", 10);")
                .setMvalue("postQuestion", "TRUE : post : addScore(\"postScore\", 20);")
                .buildCustom("cysec.logic");
        Map<String, MetadataUtils.SimpleMvalue> logicMap = MetadataUtils.parseMvalues(logicMetadata.getMvalue());
        Optional<MetadataUtils.SimpleMvalue> logicPre = Optional.ofNullable(logicMap.get("preQuestion"));
        Optional<MetadataUtils.SimpleMvalue> logicPost = Optional.ofNullable(logicMap.get("postQuestion"));

        when(libcal.getAnswer(question.getId())).thenReturn(answer);

        LibQuestion libQuestion = library.getQuestion(question.getId());
        try {
            library.runLogic(libQuestion, logicPre, libQuestion.getLogic("_cysec.logic.default"), logicPost);
        } catch (ParserException | ExecutorException e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(context.getScore("preScore").getValue() == 10);
        assertTrue(context.getScore("strength").getValue() == 30);
        assertTrue(context.getScore("postScore").getValue() == 20);
    }*/

    @Test
    public void testRunBeginLogic() {
        try {
            library.onBegin(fqcn);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(context.getScore("myBeginScore").getValue() == 99);

    }

    @Test
    public void testAwardBadge() {
        Question question = coach.getQuestions().getQuestion().stream().filter(question1 -> question1.getId().equals("user-q30")).findFirst().get();
        question.setId("user-q30");
        Answer answer = new Answer();
        answer.setText("user-q30o1");
        answer.setQid(question.getId());

        library.onBegin(fqcn);
        library.onResponseChange(question, answer, fqcn);

        // Make sure badge was assigned
        Assert.assertEquals(
                ((CySeCExecutorContextFactory.CySeCExecutorContext) context).getBadgeList().length,
                1
        );

        // verify notification was sent
        verify(pm, times(1)).badgeChanged(anyString(), anyString(), any(ChangeType.class));
    }

    @Test
    public void testAwardRecommendation() {
        Question question = coach.getQuestions().getQuestion().stream().filter(question1 -> question1.getId().equals("user-q40")).findFirst().get();

        Answer answer = new Answer();
        answer.setText("user-q40o1");
        answer.setQid(question.getId());

        library.onBegin(fqcn);
        library.onResponseChange(question, answer, fqcn);

        // Make sure badge was assigned
        Assert.assertEquals(
                ((CySeCExecutorContextFactory.CySeCExecutorContext) context).getBadgeList().length,
                1
        );

        // verify notification was sent
        verify(pm, times(1)).recommendationChanged(anyString(), any(ChangeType.class));
    }

    @Test
    public void testSetNext() throws CacheException {
        Question question = coach.getQuestions().getQuestion().stream().filter(question1 -> question1.getId().equals("user-q11")).findFirst().get();
        answer = new Answer();
        answer.setText("user-q11o2");
        answer.setQid(question.getId());
        when(libcal.getAnswer(fqcn.toString(), "user-q11")).thenReturn(answer);

        library.onBegin(fqcn);
        library.onResponseChange(question, answer, fqcn);

        // verify variable was set
        assertEquals("user-q160", context.getVariable("_coach.nextPage", question.getId()).getId());

        // modify selection
        answer.setText("user-q11o1");
        library.onResponseChange(question, answer, fqcn);

        assertEquals("user-q30", context.getVariable("_coach.nextPage", question.getId()).getId());
    }


}
