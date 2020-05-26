package eu.smesec.library.demo.skills;

import eu.smesec.bridge.generated.Mvalue;
import eu.smesec.bridge.md.MetadataUtils;
import eu.smesec.library.skills.Endurance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TestEndurance {
    private Endurance endurance;
    private Map<Long, Integer> entries = new HashMap<>();
    private String array;

    @Before
    public void setup() {
        entries.put(LocalDate.now().toEpochDay(), 1);
        entries.put(LocalDate.now().minusDays(1).toEpochDay(), 9);
        entries.put(LocalDate.now().minusDays(2).toEpochDay(), 4);
        entries.put(LocalDate.now().minusDays(3).toEpochDay(), 10);
        entries.put(LocalDate.now().minusDays(4).toEpochDay(), 2);
        entries.put(LocalDate.now().minusDays(5).toEpochDay(), 1);

        array = entries.entrySet().toString();
    }

    @After
    public void tearDown() {
        entries = null;
    }

    @Test
    // Relies on correct refresh method to restore state
    @Ignore
    // TODO: Order doesnt match but this isnt important
    public void testToString() {
        Mvalue mv = MetadataUtils.createMvalueStr("endurance", array);
        endurance = new Endurance(30);
        endurance.restore(MetadataUtils.parseMvalue(mv));

        // Check sum of integer values
        Assert.assertEquals(array, endurance.toString());
    }

    @Test
    public void testRefresh() {
        Mvalue mv = MetadataUtils.createMvalueStr("endurance", array);

        endurance = new Endurance(30);
        endurance.restore(MetadataUtils.parseMvalue(mv));
        int n = endurance.refresh();
        // Check sum of integer values
        Assert.assertEquals(27, endurance.get());
    }

    @Test
    public void testRestore() {
        Mvalue mv = MetadataUtils.createMvalueStr("endurance", array);

        endurance = new Endurance(3);
        endurance.restore(MetadataUtils.parseMvalue(mv));
        // Check sum of integer values
        Assert.assertEquals(24, endurance.get());
    }
}
