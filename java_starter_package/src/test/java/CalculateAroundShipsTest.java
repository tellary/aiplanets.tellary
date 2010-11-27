import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 27, 2010
 * Time: 4:55:45 AM
 */
public class CalculateAroundShipsTest {
    @Test
    public void test() throws IOException {
        PlanetWarsState state = PlanetsParserTest.parseMap("map23with100arrivals.txt");

        int aroundShips = MyBot.calculateAroundShips(state, 1, 22, PlanetWarsState.ME);
        Assert.assertEquals(0, aroundShips);
    }

    @Test
    public void testHasAroundShips() throws IOException {
        PlanetWarsState state = PlanetsParserTest.parseMap("map23with100arrivals.txt");

        int aroundShips = MyBot.calculateAroundShips(state, 1, 0, PlanetWarsState.ME);
        int restoreTime = (int) Math.ceil((double)100 / StaticPlanetsData.growth[0]);
        int enemyRadius = restoreTime + StaticPlanetsData.distances[1][0] + 1;
        int startTime = enemyRadius - StaticPlanetsData.distances[2][0];
        int expectedShipsAround = startTime*StaticPlanetsData.growth[2];
        Assert.assertEquals(expectedShipsAround, aroundShips);

        aroundShips = MyBot.calculateAroundShips(state, 1, 14, PlanetWarsState.ME);
        Assert.assertEquals(0, aroundShips);
    }

    @Test
    public void testAroundShipsCloseToEnemy() throws IOException {
        PlanetWarsState state = PlanetsParserTest.parseMap("map23with100arrivals.txt");

        List<Integer> sortedRow = StaticPlanetsData.sortedPlanets.get(21);
        int lastDistance = -1;
        for (Integer i : sortedRow) {
            int distance = StaticPlanetsData.distances[i][21];
            if (lastDistance > 0) {
                Assert.assertThat(distance, Matchers.greaterThanOrEqualTo(lastDistance));
            }
            lastDistance = distance;
        }

        int aroundShips = MyBot.calculateAroundShips(state, 1, 21, PlanetWarsState.ME);
        int restoreTime = (int) Math.ceil((double)36 / StaticPlanetsData.growth[21]);
        int enemyRadius = restoreTime + StaticPlanetsData.distances[1][21] + 1;
        int startTime = enemyRadius - StaticPlanetsData.distances[2][21];
        int expectedShipsAround = startTime*StaticPlanetsData.growth[2];
        Assert.assertEquals(expectedShipsAround, aroundShips);
    }

    @Test
    public void testAroundShipsCloseToEnemy2() throws IOException {
        PlanetWarsState state = PlanetsParserTest.parseMap("map23forCalcAround.txt");
        Arrivals myArrivals = state.getMyArrivals();
        Assert.assertEquals(37, myArrivals.get(StaticPlanetsData.distances[1][22] - 1, 22));


        for (int t = 0; t < StaticPlanetsData.distances[2][21]; ++t) {
            state.evaluateTurn();
        }
        int owner = state.getOwnersOnTurn(StaticPlanetsData.distances[2][21])[21];
        Assert.assertEquals(PlanetWarsState.ENEMY, owner);
        int aroundShips = MyBot.calculateAroundShips(state, 1, 21, PlanetWarsState.ME);
    }
}
