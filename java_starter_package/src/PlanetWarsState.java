import java.util.*;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 11, 2010
 * Time: 8:48:52 PM
 */
public class PlanetWarsState {
    public static final int ME = 1;
    public static final int ENEMY = 2;
    public static final int NEUTRAL = 0;

    private List<int[]> planetsInTime;
    private List<int[]> ownersInTime;
    private List<int[]> arrivalsInTime;

    private Iterator<AsymmetricMatrix> myPlan;
    private Iterator<AsymmetricMatrix> enemyPlan;

    private int currentTurn;

    public PlanetWarsState(
            List<int[]> planetsInTime,
            List<int[]> ownersInTime,
            List<int[]> arrivalsInTime) {
        this.planetsInTime = planetsInTime;
        this.ownersInTime = ownersInTime;
        this.arrivalsInTime = arrivalsInTime;
    }

    public void setMyPlan(Iterator<AsymmetricMatrix> myPlan) {
        this.myPlan = myPlan;
    }

    public void setEnemyPlan(Iterator<AsymmetricMatrix> enemyPlan) {
        this.enemyPlan = enemyPlan;
    }

    public List<int[]> getPlanetsInTime() {
        return planetsInTime;
    }

    public List<int[]> getOwnersInTime() {
        return ownersInTime;
    }

    public List<int[]> getArrivalsInTime() {
        return arrivalsInTime;
    }

    public PlanetWarsState copy() {
        List<int[]> planetsInTime = new ArrayList<int[]>();
        for (int[] planets : this.planetsInTime) {
            int[] copy = new int[planets.length];
            System.arraycopy(planets, 0, copy, 0, planets.length);
            planetsInTime.add(copy);
        }

        List<int[]> ownersInTime = new ArrayList<int[]>();
        for (int[] owners : this.ownersInTime) {
            int[] copy = new int[owners.length];
            System.arraycopy(owners, 0, copy, 0, owners.length);
            ownersInTime.add(copy);
        }

        List<int[]> arrivalsInTime = new ArrayList<int[]>();
        for (int[] arrivals : this.arrivalsInTime) {
            int[] copy = new int[arrivals.length];
            System.arraycopy(arrivals, 0, copy, 0, arrivals.length);
            arrivalsInTime.add(copy);
        }

        return new PlanetWarsState(planetsInTime, ownersInTime, arrivalsInTime);
    }

    private Result turn(Result result) {
        ++currentTurn;
        return result;
    }

    public Result evaluateTurn() {
        int[] prevPlanets = planetsInTime.get(currentTurn);
        int[] prevOwners = ownersInTime.get(currentTurn);

        int planetsAmount = prevPlanets.length;
        int[] planets = new int[planetsAmount];
        int[] arrivals = null;
        if (currentTurn < arrivalsInTime.size()) {
            arrivals = arrivalsInTime.get(currentTurn);
        }
        int[] owners = new int[planetsAmount];

        for (int i = 0; i < planets.length; ++i) {
            planets[i] = (arrivals == null? 0:arrivals[i]) + prevOwners[i] * MyBot.growth[i] + prevPlanets[i];
            if (prevPlanets[i] < 0) {
                if (planets[i] > 0)
                    owners[i] = 1;
                else
                    owners[i] = prevOwners[i];
            } else {
                if (planets[i] < 0)
                    owners[i] = -1;
                else
                    owners[i] = prevOwners[i];
            }
        }
        planetsInTime.add(planets);
        ownersInTime.add(owners);


        if (!myPlan.hasNext() && arrivals == null)
            return turn(Result.FINISHED);
        if (!myPlan.hasNext())
            return turn(Result.SUCCESS);
        AsymmetricMatrix myTransitions = myPlan.next();

        int[][] distances = MyBot.distances;
        for (int i = 0; i < myTransitions.size(); ++i) {
            for (int j = 0; j < i; ++j) {
                if (myTransitions.get(i,j) == 0)
                    continue;
                int distance = distances[i][j];
                if (distance == 0)
                    continue;

                distance += currentTurn - 1;

                ListIterator<int[]> iter = arrivalsInTime.listIterator();
                for (int a = 0; a < distance; ++a) {
                    if (iter.hasNext())
                        arrivals = iter.next();
                    else {
                        iter.add(arrivals = new int[planetsAmount]);
                    }
                }
                if (arrivals == null)
                    throw new RuntimeException();

                if (myTransitions.get(i, j) > 0) {
                    arrivals[j] += myTransitions.get(i, j);
                    planets[i] -= myTransitions.get(i, j);
                    if (prevPlanets[i] < 0 || prevPlanets[i] < myTransitions.get(i, j))
                        return turn(Result.FAILED);
                } else {
                    arrivals[i] += myTransitions.get(j, i);
                    planets[j] -= myTransitions.get(j, i);
                    if (prevPlanets[j] < 0 || prevPlanets[j] < myTransitions.get(j, i))
                        return turn(Result.FAILED);
                }
            }
        }
        return turn(Result.SUCCESS);
    }
}
