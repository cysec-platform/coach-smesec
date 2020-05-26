package eu.smesec.library.demo;

import eu.smesec.bridge.ILibCal;
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
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestLogicRunner {
    private ILibCal libcal;
    private Questionnaire coach;
    private AbstractLib library;
    private String libId = "eu.smesec.library.demo.MockLibrary";
    private Logger logger;
    private Unmarshaller unmarshaller;
    private ExecutorContext context;

    @Before
    public void setup() {
        library = new MockLibrary();
        libcal = mock(ILibCal.class);
        coach = mock(Questionnaire.class);
        logger = Logger.getGlobal();

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

    }

    @After
    public void tearDown() {
        library = null;
        context.reset();
        context = null;
    }

    // Test creation

    // Test onBegin

    // Test runLogic
}
