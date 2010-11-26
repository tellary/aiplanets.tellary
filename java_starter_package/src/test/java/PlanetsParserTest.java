import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 25, 2010
 * Time: 2:28:46 AM
 */
public class PlanetsParserTest {
    public static PlanetWarsState parseMap(String name) throws IOException {
        InputStream is = PlanetsParserTest.class.getResourceAsStream(name);
        InputStreamReader fr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(fr);
        PlanetParser planetParser = new FirstTurnPlanetParser();
        return planetParser.parsePlanets(br).state;
    }
    @Test
    public void test() throws IOException {
        PlanetWarsState state = parseMap("KickOutOvertake_on_faraway_planet.txt");

        int planets[] = state.getNumShipsOnTurn(0);
        Assert.assertEquals(3, planets.length);
        Assert.assertEquals(33, planets[0]);
        Assert.assertEquals(10, planets[1]);
        Assert.assertEquals(15, planets[2]);

        int owners[] = state.getOwnersOnTurn(0);
        Assert.assertEquals(3, owners.length);
        Assert.assertEquals(PlanetWarsState.ME, owners[0]);
        Assert.assertEquals(PlanetWarsState.ENEMY, owners[1]);
        Assert.assertEquals(PlanetWarsState.NEUTRAL, owners[2]);

        Arrivals enemyArrivals = state.getEnemyArrivals();
        int distance = StaticPlanetsData.distances[1][2];
        Assert.assertEquals(distance, enemyArrivals.getMaxTurn());
        Assert.assertEquals(16, enemyArrivals.get(distance, 2));
    }
}
