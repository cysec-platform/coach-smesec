/*
 * Copyright (C) 2020 - 2021 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smesec.cysec.coach.demo;

import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.*;
import eu.smesec.cysec.csl.utils.Utils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TestDemoLib {
    private DemoLib library;
    private Questionnaire coach;
    private ILibCal libcal;
    private String libId = "eu.smesec.cysec.coach.demo.DemoLib";
    private String xmlFile = "/lib-demo-sub.xml";
    private int maxScore = 450;
    private Logger logger;
    private Unmarshaller unmarshaller;
    private FQCN fqcn = FQCN.fromString("lib-demo-sub");

    @Before
    public void setup() {
        library = new DemoLib();
        libcal = mock(ILibCal.class);
        coach = mock(Questionnaire.class);
        logger = Logger.getGlobal();
        Blocks blocks = new Blocks();
        Block b1 = new Block();
        b1.setId("b1");

        blocks.getBlock().add(b1);

        when(coach.getId()).thenReturn("DemoLib");
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
        int expectedCount = 6;
        assertEquals(expectedCount, library.getQuestions().size()  );
    }

    @Test
    @Ignore
    public void testBranchingQ30() throws CacheException {

        library.init(libId, coach, libcal, logger);
        library.onBegin(fqcn);
        Question question = Utils.findById(coach, "q30");
        Answer answer = new Answer();
        answer.setQid("q30");
        answer.setText("q30o2");
        when(libcal.getAnswer("q30")).thenReturn(answer);
        Block block = new Block();

        library.onResponseChange(question, answer, fqcn);

        List<String> actualQuestions = library.getQuestions();
        List<String> expectedQuestions = Arrays.asList("q10","q20","q30", "q40","q50","q60","q70");


        Assert.assertEquals("Size of arrays must be correct", expectedQuestions.size(), actualQuestions.size());
        Assert.assertThat("Elements should be in same order", actualQuestions, is(expectedQuestions));
    }

    @After
    public void tearDown() {
        library = null;
    }
}


