import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

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

    @Test
    public void testGrowthNumShips() throws IOException {
        PlanetWarsState state;
        state = PlanetsParserTest.parseMap("map23with100arrivals.txt");

        Plan plan;
        SquareMatrix sm;

        plan = new Plan();
        sm = new SquareMatrix(StaticPlanetsData.growth.length);
        //close planet
        sm.set(1, 19, 20);
        plan.addTransitions(sm);
        state = state.setPlans(plan.transitions().iterator(), Collections.<SquareMatrix>emptyList().iterator());
        for (int t = 0; t < StaticPlanetsData.maxDistance; ++t) {
            state.evaluateTurn();
        }

        long closeScore = MyBot.scoreNumShipsAndEnemyCoulomb(state);
        long closeScoreJustNum = MyBot.scoreNumShips(state);

        
        plan = new Plan();
        sm = new SquareMatrix(StaticPlanetsData.growth.length);
        //close planet
        sm.set(1, 20, 20);
        plan.addTransitions(sm);
        state = state.setPlans(plan.transitions().iterator(), Collections.<SquareMatrix>emptyList().iterator());
        for (int t = 0; t < StaticPlanetsData.maxDistance; ++t) {
            state.evaluateTurn();
        }

        long distantScore = MyBot.scoreNumShipsAndEnemyCoulomb(state);
        long distantScoreJustNum = MyBot.scoreNumShips(state);


        Assert.assertThat(distantScoreJustNum, Matchers.lessThan(closeScoreJustNum));
        Assert.assertThat(distantScore, Matchers.lessThan(closeScore));
    }
}
