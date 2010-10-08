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
    private List<List<SquareMatrix>> plans;

    @Before
    public void setup() {
        plans = new LinkedList<List<SquareMatrix>>();
        for (int i = 0; i < 50; ++i) {
            SquareMatrix tr = new SquareMatrix(2);
            tr.set(1, 0, i);
            List<SquareMatrix> plan = new LinkedList<SquareMatrix>();
            plan.add(tr);
            plans.add(plan);
        }
    }

    @Test
    public void test() {
        PlanSelection selection = new PlanSelection();
        selection.setScorer(new SumAllScorer());

        selection.doPlanSelection(null, plans);

        long time = System.currentTimeMillis();
        selection.doPlanSelection(null, plans);
        time = System.currentTimeMillis() - time;

        System.out.println(time);
        Assert.assertEquals(100, selection.getBestPlan().get(0).get(1, 0));
    }
}
