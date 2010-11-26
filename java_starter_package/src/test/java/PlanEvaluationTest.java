import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 26, 2010
 * Time: 4:49:51 PM
 */
public class PlanEvaluationTest {
    @Test
    public void test() throws IOException {
        PlanetWarsState state = PlanetsParserTest.parseMap("map23with100arrivals.txt");

        int t;
        for (t = 0; t < StaticPlanetsData.distances[1][2]; ++t) {
            state.evaluateTurn();
        }

        System.out.println(state.getCurrentTurn());
        System.out.println(state.getNumShipsOnTurn(t)[1]);
        System.out.println(state.getOwnersOnTurn(t)[1]);
        Assert.assertEquals(75, state.getNumShipsOnTurn(t)[1]);
        Assert.assertEquals(1, state.getOwnersOnTurn(t)[1]);
    }

    @Test
    public void testWithMyDepartures() throws IOException {
        PlanetWarsState state = PlanetsParserTest.parseMap("map23with100arrivals.txt");
        Plan plan = new Plan();
        SquareMatrix sm = new SquareMatrix(StaticPlanetsData.growth.length);
        sm.set(1, 22, 37);
        plan.addTransitions(sm);
        state = state.setPlans(plan.transitions().iterator(), Collections.<SquareMatrix>emptyList().iterator());
        int t;
        for (t = 0; t < StaticPlanetsData.distances[1][22]; ++t) {
            state.evaluateTurn();
        }

        System.out.println(state.getCurrentTurn());
        System.out.println(state.getNumShipsOnTurn(t)[1]);
        System.out.println(state.getOwnersOnTurn(t)[1]);
        Assert.assertEquals(78, state.getNumShipsOnTurn(t)[1]);
        Assert.assertEquals(1, state.getOwnersOnTurn(t)[1]);
        Assert.assertEquals(1, state.getNumShipsOnTurn(t)[22]);
        Assert.assertEquals(1, state.getOwnersOnTurn(t)[22]);
        Assert.assertEquals(0, state.getOwnersOnTurn(t - 1)[22]);
    }
}
