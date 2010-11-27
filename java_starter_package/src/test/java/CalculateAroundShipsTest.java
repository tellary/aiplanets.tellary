import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

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

//        int aroundShips = MyBot.calculateAroundShips(state, 1, 0, PlanetWarsState.ENEMY);
//        Assert.assertEquals(505, aroundShips);

        int aroundShips = MyBot.calculateAroundShips(state, 1, 14, PlanetWarsState.ME);
        Assert.assertEquals(65, aroundShips);
    }
}
