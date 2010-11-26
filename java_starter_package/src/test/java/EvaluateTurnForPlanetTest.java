import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 25, 2010
 * Time: 1:22:21 AM
 */
public class EvaluateTurnForPlanetTest {
    @Test
    public void testMyPlanetWon() throws IOException {
        PlanetState state = new PlanetState().
                setEnemyArrivals(4).
                setNumShips(5).
                setGrowth(2).
                setOwner(PlanetWarsState.ME);
        PlanetWarsState.evaluateTurnForPlanet(state);
        Assert.assertEquals(PlanetWarsState.ME, state.getOwner());
        Assert.assertEquals(3, state.getNumShips());
    }
    @Test
    public void testMyPlanetWonByGrowth() throws IOException {
        PlanetState state = new PlanetState().
                setEnemyArrivals(4).
                setNumShips(2).
                setGrowth(2).
                setOwner(PlanetWarsState.ME);
        PlanetWarsState.evaluateTurnForPlanet(state);
        Assert.assertEquals(PlanetWarsState.ME, state.getOwner());
        Assert.assertEquals(0, state.getNumShips());
    }
    @Test
    public void testEnemyPlanetWonByGrowth() throws IOException {
        PlanetState state = new PlanetState().
                setMyArrivals(4).
                setNumShips(2).
                setGrowth(2).
                setOwner(PlanetWarsState.ENEMY);
        PlanetWarsState.evaluateTurnForPlanet(state);
        Assert.assertEquals(PlanetWarsState.ENEMY, state.getOwner());
        Assert.assertEquals(0, state.getNumShips());
    }
    @Test
    public void testTripleBattleIWon() throws IOException {
        PlanetState state = new PlanetState().
                setMyArrivals(4).
                setEnemyArrivals(3).
                setNumShips(3).
                setGrowth(2).
                setOwner(PlanetWarsState.NEUTRAL);
        PlanetWarsState.evaluateTurnForPlanet(state);
        Assert.assertEquals(PlanetWarsState.ME, state.getOwner());
        Assert.assertEquals(1, state.getNumShips());
    }
    @Test
    public void testDepartures() {
        Map<Integer, Integer> departures = new HashMap<Integer, Integer>();
        departures.put(2, 2);
        Arrivals arrivals = new Arrivals(3);
        int[][] distances = new int[][] {
                new int[] {0 , 5, 10},
                new int[] {5 , 0, 4 },
                new int[] {10, 4, 0 },
        };
        PlanetState state = new PlanetState().
                setPlanetId(1).
                setMyArrivals(4).
                setEnemyArrivals(3).
                setNumShips(3).
                setGrowth(2).
                setOwner(PlanetWarsState.ME).
                setDepartures(departures).
                setArrivals(arrivals).
                setDistances(distances);
        PlanetWarsState.evaluateTurnForPlanet(state);
        Assert.assertEquals(PlanetWarsState.ME, state.getOwner());
        Assert.assertEquals(4, state.getNumShips());
        Assert.assertEquals(2, arrivals.get(distances[1][2] - 1, 2));
    }
}
