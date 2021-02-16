package eu.smesec.library.access_control;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.generated.Block;
import eu.smesec.bridge.generated.Blocks;
import eu.smesec.bridge.generated.Questionnaire;
import org.junit.After;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAccessLib {
    private AccessLib library;
    private Questionnaire coach;
    private ILibCal libcal;
    private String xmlFile = "/lib-access-control.xml";
    private String libId = "eu.smesec.library.AccessLib";
    private int maxScore = 450;
    private Logger logger;
    private Unmarshaller unmarshaller;

    @Before
    public void setup() {
        library = new AccessLib();
        libcal = mock(ILibCal.class);
        coach = mock(Questionnaire.class);
        logger = Logger.getGlobal();
        Blocks blocks = new Blocks();
        Block b1 = new Block();
        b1.setId("b1");

        blocks.getBlock().add(b1);

        when(coach.getId()).thenReturn("AccessLib");
        when(coach.getBlocks()).thenReturn(blocks);

        try (BufferedInputStream is = new BufferedInputStream(getClass().getResourceAsStream(xmlFile))) {
            JAXBContext jc = JAXBContext.newInstance(Questionnaire.class);
            unmarshaller = jc.createUnmarshaller();
            coach = (Questionnaire) unmarshaller.unmarshal(is);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testInit() {
        library.init(libId, coach, libcal, logger);
        int expectedCount = 11;
        //assertTrue(library.getQuestions().length == expectedCount);
    }

    @After
    public void tearDown() {
        library = null;
    }
}


