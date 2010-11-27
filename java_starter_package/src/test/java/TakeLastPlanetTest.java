import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 27, 2010
 * Time: 7:51:06 AM
 */
public class TakeLastPlanetTest {
    @Test
    public void testWithMyDepartures() throws IOException {
        PlanetWarsState state = PlanetsParserTest.parseMap("map23_small.txt");
        int t;
        for (t = 0; t < 2*StaticPlanetsData.distances[0][2]; ++t) {
            state.evaluateTurn();
        }

        System.out.println(MyBot.scoreComplex(state));
        
        Plan plan = new Plan();
        SquareMatrix sm = new SquareMatrix(StaticPlanetsData.growth.length);
        sm.set(2, 0, 120);
        plan.addTransitions(sm);
        state = state.setPlans(plan.transitions().iterator(), Collections.<SquareMatrix>emptyList().iterator());
        for (t = 0; t < 2*StaticPlanetsData.distances[0][2]; ++t) {
            state.evaluateTurn();
        }
        System.out.println(MyBot.scoreComplex(state));
    }
    @Test
    public void test() throws IOException {
        PlanetWarsState state = PlanetsParserTest.parseMap("map23_small.txt");
        LinkedList<Plan> plans = new LinkedList<Plan>();
        MyBot.addTakeOnePlanetPlans(state, 0, plans);

        boolean found = false;
        for (Plan plan : plans) {
            for (SquareMatrix sm : plan.transitions()) {
                if (!found) found = sm.get(2, 0) != 0;
            }
        }
        Assert.assertTrue(found);
    }
}
