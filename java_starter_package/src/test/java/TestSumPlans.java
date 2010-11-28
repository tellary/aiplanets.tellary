import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 21, 2010
 * Time: 12:13:26 AM
 */
public class TestSumPlans {
    public Plan plan1 = new Plan(2, StaticPlanetsData.maxDistance);
    public Plan plan2 = new Plan(2, StaticPlanetsData.maxDistance);

    @Before
    public void before() {
        SquareMatrix sm;
        sm = new SquareMatrix(2);
        sm.set(0, 1, 1);
        plan1.addTransitions(sm);
        sm = new SquareMatrix(2);
        sm.set(0, 1, 3);
        plan1.addTransitions(sm);

        sm = new SquareMatrix(2);
        sm.set(0, 1, 10);
        plan2.addTransitions(sm);
    }

    @Test
    public void test() {
        Plan result = Plan.sum(plan1, plan2);
        Iterator<SquareMatrix> iter = result.transitions().iterator();
        SquareMatrix sm = iter.next();
        Assert.assertEquals(11, sm.get(0, 1));
        sm = iter.next();
        Assert.assertEquals(3, sm.get(0, 1));
        Assert.assertFalse(iter.hasNext());

        Plan result1 = Plan.sum(plan2, plan1);
        iter = result1.transitions().iterator();
        sm = iter.next();
        Assert.assertEquals(11, sm.get(0, 1));
        sm = iter.next();
        Assert.assertEquals(3, sm.get(0, 1));
        Assert.assertFalse(iter.hasNext());

        Assert.assertTrue(result.equals(result1));
        Assert.assertTrue(result1.equals(result));
    }

    @Test
    public void testDifferentSizeBeginning() {
        Plan plan = new Plan(2, StaticPlanetsData.maxDistance);
        SquareMatrix sm = new SquareMatrix(3);
        plan.addTransitions(sm);
        try {
            Plan.sum(plan1, plan);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            //ok
        }
    }

    @Test
    public void testDifferentSizeTail() {
        Plan plan = new Plan(3, StaticPlanetsData.maxDistance);
        SquareMatrix sm = new SquareMatrix(2);
        plan.addTransitions(sm);
        sm = new SquareMatrix(3);
        plan.addTransitions(sm);
        try {
            Plan.sum(plan2, plan);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            //ok
        }
        try {
            Plan.sum(plan, plan2);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            //ok
        }
    }
}
