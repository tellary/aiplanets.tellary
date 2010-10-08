import java.util.*;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 11, 2010
 * Time: 8:48:52 PM
 */
public class PlanetWarsState {
    public static final int ME = 1;
    public static final int ENEMY = 2;
//    public static final int NEUTRAL = 0;

    private List<int[]> planetsInTime;
    private List<int[]> ownersInTime;
    private List<int[]> arrivalsInTime;

    private Iterator<SquareMatrix> myPlan;
    private Iterator<SquareMatrix> enemyPlan;

    private int currentTurn;

    public PlanetWarsState(
            List<int[]> planetsInTime,
            List<int[]> ownersInTime,
            List<int[]> arrivalsInTime) {
        this.planetsInTime = planetsInTime;
        this.ownersInTime = ownersInTime;
        this.arrivalsInTime = arrivalsInTime;
    }

    public void setMyPlan(Iterator<SquareMatrix> myPlan) {
        this.myPlan = myPlan;
    }

    public void setEnemyPlan(Iterator<SquareMatrix> enemyPlan) {
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

        SquareMatrix myTransitions = null;
        if (myPlan.hasNext()) {
            myTransitions = myPlan.next();
        }
        SquareMatrix enemyTransitions = null;
        if (enemyPlan.hasNext()) {
            enemyTransitions = enemyPlan.next();
        }

        for (int i = 0; i < planets.length; ++i) {
            int arrivalsForPlanet = (arrivals == null? 0:arrivals[i]);
            if (Log.isEnabled()) {
                StringBuilder sb = new StringBuilder("evaluateTurn arrivals: planet ");
                MyBot.printPlanet(sb, prevPlanets, prevOwners, i);
                sb.append(", turn: ").append(currentTurn).append(", arrivals: ").append(arrivalsForPlanet);
                Log.log(sb.toString());
            }
            
            if (applyTransitions(myTransitions, i, prevPlanets, prevOwners, planets) != Result.SUCCESS)
                return turn(Result.FAILED);
            if (applyTransitions(enemyTransitions, i, prevPlanets, prevOwners, planets) != Result.SUCCESS)
                return turn(Result.FAILED);

            if (!(arrivalsForPlanet < 0 && prevOwners[i] == 0)) {
                planets[i] += arrivalsForPlanet + prevOwners[i] * MyBot.growth[i] + prevPlanets[i];
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
            } else {
                planets[i] += arrivalsForPlanet + prevOwners[i] * MyBot.growth[i] - prevPlanets[i];
                if (planets[i] > 0) {
                    owners[i] = 0;
                    planets[i] = -planets[i];
                } else {
                    owners[i] = -1;
                }
            }



            if (Log.isEnabled()) {
                StringBuilder sb = new StringBuilder("evaluateTurn planet: ");
                MyBot.printPlanet(sb, planets, owners, i);
                Log.log(sb.toString());
            }
        }
        planetsInTime.add(planets);
        ownersInTime.add(owners);

        if (!myPlan.hasNext() && !enemyPlan.hasNext() && arrivals == null)
            return turn(Result.FINISHED);

        return turn(Result.SUCCESS);
    }

    private Result applyTransitions(SquareMatrix transitions, int i, int[] prevPlanets, int[] prevOwners, int[] planets) {
        if (transitions == null)
            return Result.SUCCESS;
        int[] arrivals = null;
        int[][] distances = MyBot.distances;
        for (int j = 0; j < planets.length; ++j) {
            if (transitions.get(i,j) == 0)
                continue;

            if (Log.isEnabled()) {
                StringBuilder sb = new StringBuilder("Going to apply transition from planet ");
                MyBot.printPlanet(sb, prevPlanets, prevOwners, i);
                sb.append(" to planet ");
                MyBot.printPlanet(sb, prevPlanets, prevOwners, j);
                sb.append(", num ships: ").append(transitions.get(i, j));
                sb.append(", distance: ").append(distances[i][j]);
                Log.log(sb.toString());
            }

            if (prevPlanets[i]*transitions.get(i, j) < 0)
                return Result.FAILED;

            int distance = distances[i][j];
            if (distance == 0)
                continue;

            ListIterator<int[]> iter = arrivalsInTime.listIterator();
            for (int a = 0; a < currentTurn + distance; ++a) {
                if (iter.hasNext())
                    arrivals = iter.next();
                else {
                    iter.add(arrivals = new int[transitions.size()]);
                }
            }
            if (arrivals == null)
                throw new RuntimeException();

            arrivals[j] += transitions.get(i, j);
            planets[i] -= transitions.get(i, j);
            if (prevPlanets[i] > 0 && prevPlanets[i] < -planets[i])
                return Result.FAILED;
            if (prevPlanets[i] < 0 && -prevPlanets[i] < planets[i])
                return Result.FAILED;
        }

        return Result.SUCCESS;
    }
}
