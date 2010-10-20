import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 9, 2010
 * Time: 12:30:09 AM
 */
public class PlanSelectionWithSumAllScorerTest {
    private List<Plan> plans = null;

    @Before
    public void setup() {
        plans = new LinkedList<Plan>();
        for (int i = 0; i < 50; ++i) {
            SquareMatrix tr = new SquareMatrix(2);
            tr.set(1, 0, i);
            Plan plan = new Plan();
            plan.addTransitions(tr);
            plans.add(plan);
        }
    }

    @Test
    public void test() {
        PlanSelection selection;
        selection = new PlanSelection();
        MyBot.start = System.currentTimeMillis();
        selection.setScorer(new SumAllScorer());

        selection.doPlanSelection(null, new LinkedList<Plan>(plans));

        LinkedList<Plan> plans = new LinkedList<Plan>(this.plans);
        selection = new PlanSelection();
        selection.setScorer(new SumAllScorer());
        long time = System.currentTimeMillis();
        MyBot.start = time;
        selection.doPlanSelection(null, plans);
        time = System.currentTimeMillis() - time;
        System.out.println(time);
        
        Assert.assertEquals(100, selection.getBestPlan().transitions().iterator().next().get(1, 0));
    }
}
