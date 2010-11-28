import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 20, 2010
 * Time: 11:59:40 PM
 */
public class TestPlan {
    @Test
    public void testEmpty() {
        Plan plan = new Plan(StaticPlanetsData.growth.length, StaticPlanetsData.maxDistance);
        Assert.assertTrue(plan.isEmpty());

        plan.addTransitions(new SquareMatrix(5));
        Assert.assertTrue(plan.isEmpty());
    }

    @Test
    public void testSet() {
        Set<Plan> set = new HashSet<Plan>();

        Plan plan;
        SquareMatrix sm;
        
        sm = new SquareMatrix(2);
        sm.set(0, 1, 2);
        plan = new Plan(2, StaticPlanetsData.maxDistance);
        plan.addTransitions(sm);
        Assert.assertTrue(set.add(plan));
        //Attempt to add same plan twice
        Assert.assertFalse(set.add(plan));

        sm = new SquareMatrix(2);
        sm.set(0, 1, 3);
        plan = new Plan(2, StaticPlanetsData.maxDistance);
        plan.addTransitions(sm);
        Assert.assertTrue(set.add(plan));

        sm = new SquareMatrix(2);
        sm.set(0, 1, 2);
        plan = new Plan(2, StaticPlanetsData.maxDistance);
        plan.addTransitions(sm);
        Assert.assertFalse(set.add(plan));

        Iterator<SquareMatrix> iter = plan.transitions().iterator();
        iter.next();
        Assert.assertFalse(iter.hasNext());

        //Checking equals works on same object for coverage
        Assert.assertTrue(plan.equals(plan));
        //Checking different type works for coverage
        //noinspection EqualsBetweenInconvertibleTypes
        Assert.assertFalse(plan.equals(1));
    }
}
