package eu.smesec.library.parser;

import eu.smesec.bridge.ILibCal;
import eu.smesec.bridge.Library;
import eu.smesec.bridge.generated.Question;
import eu.smesec.bridge.generated.Questionnaire;
import eu.smesec.library.AbstractLib;
import eu.smesec.library.PersistanceManager;
import eu.smesec.library.demo.MockLibrary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({CySeCExecutorContextFactory.class})
public class TestPersistenceManager {
    private PersistanceManager persistanceManager;
    @Mock
    private Library library;
    @Mock
    private Questionnaire coach;
    private AbstractLib lib;
    @Mock
    private ILibCal cal;
    private Logger logger = Logger.getGlobal();
    @Mock
    private CySeCExecutorContextFactory.CySeCExecutorContext context;
    private String contextId = "lib-sub-A";
    @Mock
    private CySeCExecutorContextFactory.CySeCExecutorContext parent;
    private String parentContextId = "lib-top";
    @Mock
    private CySeCExecutorContextFactory.CySeCExecutorContext grandfather;
    private String grandfatherContextId = "lib-root";


    @Before
    public void setup() {
        PowerMockito.mockStatic(CySeCExecutorContextFactory.class);
        Mockito.when(CySeCExecutorContextFactory.getExecutorContext(anyString())).thenReturn(context);

        lib = new MockLibrary();
        lib.setQuestionnaire(coach);
        when(coach.getId()).thenReturn("eu.smesec.library.mock");
        persistanceManager = new PersistanceManager(cal, logger, lib);
        when(context.getParent()).thenReturn(parent);
        when(context.getContextId()).thenReturn(contextId);
        when(parent.getParent()).thenReturn(grandfather);
        when(parent.getContextId()).thenReturn(parentContextId);
        when(grandfather.getParent()).thenReturn(null);
        when(grandfather.getContextId()).thenReturn(grandfatherContextId);
    }

    @Test
    public void tearDown(){

    }

    @Test
    public void testResolveFqcn() {
        String fqcn = persistanceManager.resolveFQCN();
        String expected = grandfatherContextId + "." + parentContextId + "." + contextId;
        assertNotNull(fqcn);
        assertEquals(expected, fqcn);
    }

    @Test
    public void testResolveFqcnTrimBase() {
        String fqcn = persistanceManager.resolveFQCN(1);
        String expected = grandfatherContextId;
        assertNotNull(fqcn);
        assertEquals(expected, fqcn);
    }

    @Test
    public void testResolveFqcnTrimMiddle() {
        String fqcn = persistanceManager.resolveFQCN(2);
        String expected = grandfatherContextId + "." + parentContextId;
        assertNotNull(fqcn);
        assertEquals(expected, fqcn);
    }


}
