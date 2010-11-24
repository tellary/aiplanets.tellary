import java.util.*;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 22, 2010
 * Time: 2:31:50 AM
 */
public class FirstTurnPlanetParser extends BasePlanetParser {
    private static final int EXPECTED_SIZE = 60;

    private ArrayList<Integer> planets = new ArrayList<Integer>(EXPECTED_SIZE);
    private ArrayList<Integer> owners = new ArrayList<Integer>(EXPECTED_SIZE);
    private ArrayList<Integer> growth = new ArrayList<Integer>(EXPECTED_SIZE);
    private ArrayList<Double> x = new ArrayList<Double>(EXPECTED_SIZE);
    private ArrayList<Double> y = new ArrayList<Double>(EXPECTED_SIZE);

    @Override
    protected void processPlanet(int planetId, double x, double y, int owner, int numShips, int growthRate) {
        this.x.add(x);
        this.y.add(y);
        owners.add(owner);
        planets.add(numShips);
        growth.add(growthRate);
    }

    protected Arrivals newArrivals() {
        return new Arrivals(EXPECTED_SIZE);
    }

    protected Result complete(Arrivals myArrivals, Arrivals enemyArrivals) {
        initConstantData();

        return new Result(
                new InitializedPlanetParser(planets.size()),
                new PlanetWarsState(
                        toInt(planets),
                        toInt(owners),
                        myArrivals,
                        enemyArrivals
                ),
                false
        );
    }


    private void initConstantData() {
        int planetsAmount = planets.size();

        StaticPlanetsData.distances = new int[planetsAmount][planetsAmount];

        StaticPlanetsData.sortedPlanets = new ArrayList<List<Integer>>(planetsAmount);

        StaticPlanetsData.avgDistance = 0;
        int avgNum = 0;
        for (int i = 0; i < planetsAmount; ++i) {
            ArrayList<Integer> sortedPlanetsRow = new ArrayList<Integer>(planetsAmount - 1);
            for (int j = 0 ; j < planetsAmount; ++j) {
                double dx = x.get(i) - x.get(j);
                double dy = y.get(i) - y.get(j);
                StaticPlanetsData.distances[i][j] = (int)Math.ceil(Math.sqrt(dx * dx + dy * dy));

                if (i != j) {
                    StaticPlanetsData.avgDistance += StaticPlanetsData.distances[i][j];
                    ++avgNum;
                    sortedPlanetsRow.add(j);
                }
                if (StaticPlanetsData.distances[i][j] > StaticPlanetsData.maxDistance)
                    StaticPlanetsData.maxDistance = StaticPlanetsData.distances[i][j];
            }
            final int currentPlanet = i;
            Collections.sort(sortedPlanetsRow, new Comparator<Integer>() {
                @Override
                public int compare(Integer planet1, Integer planet2) {
                    return new Integer(StaticPlanetsData.distances[currentPlanet][planet1]).
                            compareTo(StaticPlanetsData.distances[currentPlanet][planet2]);
                }
            });
            StaticPlanetsData.sortedPlanets.add(sortedPlanetsRow);
        }
        StaticPlanetsData.avgDistance = (int) (((double)StaticPlanetsData.avgDistance)/avgNum);
        StaticPlanetsData.growth = toInt(this.growth);
    }

    private static int[] toInt(ArrayList<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = list.get(i);
        }
        return result;
    }
}
