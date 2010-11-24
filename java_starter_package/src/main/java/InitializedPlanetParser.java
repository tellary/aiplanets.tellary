import java.io.BufferedReader;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 23, 2010
 * Time: 5:10:11 AM
 */
public class InitializedPlanetParser extends BasePlanetParser {
    private int[] planets;
    private int[] owners;

    public InitializedPlanetParser(int numPlanets) {
        planets = new int[numPlanets];
        owners = new int[numPlanets];
    }

    @Override
    protected void processPlanet(int planetId, double x, double y, int owner, int numShips, int growthRate) {
        planets[planetId] = numShips;
        owners[planetId] = owner;
    }

    @Override
    protected Arrivals newArrivals() {
        return new Arrivals(planets.length);
    }

    @Override
    protected Result complete(Arrivals myArrivals, Arrivals enemyArrivals) {
        return new Result(
                this,
                new PlanetWarsState(
                        planets,
                        owners,
                        myArrivals,
                        enemyArrivals
                ),
                false
        );
    }
}
