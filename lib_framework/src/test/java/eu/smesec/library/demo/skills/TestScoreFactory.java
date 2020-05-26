package eu.smesec.library.demo.skills;

import eu.smesec.library.parser.CySeCExecutorContextFactory;
import eu.smesec.library.parser.ExecutorContext;
import eu.smesec.library.skills.ScoreFactory;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestScoreFactory {

  @Test
  public void testBasicFunctionality() {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();

    // testing capsulation of scores
    context.getScore("test1").add("qid1",1);
    context.getScore("test2").add("qid1",2);
    assertTrue("value of score test1 is bad (1)",context.getScore("test1").getValue()==1);
    assertTrue("value of score test2 is bad (1)",context.getScore("test2").getValue()==2);
    context.getScore("test1").reset();
    assertTrue("value of score test1 is bad (2)",context.getScore("test1").getValue()==0);
    assertTrue("value of score test2 is bad (2; should:2; is:"+context.getScore("test1").getValue()+")",context.getScore("test2").getValue()==2);
    context.getScore("test2").reset();
    assertTrue("value of score test1 is bad (3)",context.getScore("test1").getValue()==0);
    assertTrue("value of score test2 is bad (3)",context.getScore("test2").getValue()==0);

    // testing adding of scores
    context.reset();
    context.getScore("test1").add("qid1",1);
    context.getScore("test1").add("qid2",2);
    context.getScore("test1").add("qid2",3);
    assertTrue("value of score test1 is bad (4; should:6; is:"+context.getScore("test1")+")",context.getScore("test1").getValue()==6);

    // testing capping of scores
    context.reset();
    context.getScore("test1").add("qid1",1);
    context.getScore("test1").cap("qid1",2);
    context.getScore("test1").add("qid2",2);
    context.getScore("test1").add("qid2",3);
    context.getScore("test1").cap("qid1",3);
    assertTrue("value of score test1 is bad (5; should:2; is:"+context.getScore("test1")+")",context.getScore("test1").getValue()==2);

    // testing score Reversion of question
    context.reset();
    context.getScore("test1").add("qid1",1);
    context.getScore("test1").add("qid2",2);
    context.getScore("test1").add("qid3",3);
    assertTrue("value of score before reversion test1 is bad (6; should:6; is:"+context.getScore("test1").getValue()+")",context.getScore("test1").getValue()==6);
    context.revertQuestionScore("qid2");
    assertTrue("value of score after reversion test1 is bad (6; should:4; is:"+context.getScore("test1").getValue()+")",context.getScore("test1").getValue()==4);

  }
}
