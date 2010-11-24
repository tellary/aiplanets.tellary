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

    private LinkedList<int[]> numShipsInTime;
    private LinkedList<int[]> ownersInTime;
    private Arrivals myArrivals;
    private Arrivals enemyArrivals;

    private Iterator<SquareMatrix> myPlan = Collections.<SquareMatrix>emptyList().iterator();
    private Iterator<SquareMatrix> enemyPlan = Collections.<SquareMatrix>emptyList().iterator();

    private int currentTurn = 0;

    public PlanetWarsState(
            int[] planets,
            int[] owners,
            Arrivals myArrivals,
            Arrivals enemyArrivals) {

        this.numShipsInTime = new LinkedList<int[]>();
        this.numShipsInTime.add(planets);
        this.ownersInTime = new LinkedList<int[]>();
        this.ownersInTime.add(owners);
        this.myArrivals = myArrivals;
        this.enemyArrivals = enemyArrivals;
    }

    public int getNumShips(int i) {
        return numShipsInTime.peek()[i];
    }

    public int getOwner(int i) {
        return ownersInTime.peek()[i];
    }

    public int[] getNumShipsOnTurn(int turn) {
        return numShipsInTime.get(turn);
    }

    public int getNumPlanets() {
        return numShipsInTime.peek().length;
    }

    public int[] getOwnersOnTurn(int turn) {
        return ownersInTime.get(turn);
    }

    public int getMaxArrivalsTurn() {
        return Math.max(myArrivals.getMaxTurn(), enemyArrivals.getMaxTurn());
    }

    public Arrivals getEnemyArrivals() {
        return enemyArrivals;
    }

    public PlanetWarsState setPlans(Iterator<SquareMatrix> myPlan, Iterator<SquareMatrix> enemyPlan) {
        int[] planets = numShipsInTime.peek();
        int[] planetsCopy = new int[planets.length];
        System.arraycopy(planets, 0, planetsCopy, 0, planets.length);
        int[] owners = ownersInTime.peek();
        int[] ownersCopy = new int[owners.length];
        System.arraycopy(owners, 0, ownersCopy, 0, planets.length);

        Arrivals myArrivals = this.myArrivals.copy();
        Arrivals enemyArrivals = this.enemyArrivals.copy();

        PlanetWarsState copy = new PlanetWarsState(planetsCopy, ownersCopy, myArrivals, enemyArrivals);
        copy.myPlan = myPlan;
        copy.enemyPlan = enemyPlan;
        return copy;
    }

    private Result turn(Result result) {
        ++currentTurn;
        return result;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public Result evaluateTurn() {
        int[] prevPlanets = numShipsInTime.get(currentTurn);
        int[] prevOwners = ownersInTime.get(currentTurn);

        int planetsAmount = prevPlanets.length;
        int[] planets = new int[planetsAmount];
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
            int myArrivalsForPlanet = myArrivals.get(currentTurn, i);
            int enemyArrivalsForPlanet = enemyArrivals.get(currentTurn, i);
            if (Log.isEnabled()) {
                StringBuilder sb = new StringBuilder("evaluateTurn arrivals: planet ");
                MyBot.printPlanet(sb, prevPlanets, prevOwners, i);
                sb.append(", turn: ").append(currentTurn).
                        append(", my arrivals: ").append(myArrivalsForPlanet).
                        append(", enemy arrivals: ").append(enemyArrivalsForPlanet);
                Log.log(sb.toString());
            }


            //My Departure phase handling
            if (applyTransitions(myTransitions, i, prevPlanets, prevOwners, planets) != Result.SUCCESS)
                return turn(Result.FAILED);
            //Enemy Departure phase handling
            if (applyTransitions(enemyTransitions, i, prevPlanets, prevOwners, planets) != Result.SUCCESS)
                return turn(Result.FAILED);

            //Planet Advancement (fleet advancement is handled by arrivals by turn indexing in Arrivals data structure
            planets[i] += StaticPlanetsData.growth[i];

            //Arrival phase handling
            //TODO: write test fo forces handling
            TreeSet<Force> forces = new TreeSet<Force>();
            Force myForce = new Force(ME);
            if (prevOwners[i] == ME) {
                myForce.add(prevPlanets[i]);
            }
            myForce.add(myArrivalsForPlanet);
            forces.add(myForce);

            Force enemyForce = new Force(ENEMY);
            if (prevOwners[i] == ENEMY) {
                enemyForce.add(prevPlanets[i]);
            }
            enemyForce.add(enemyArrivalsForPlanet);
            forces.add(enemyForce);

            Force neutralForce = new Force(NEUTRAL);
            if (prevOwners[i] == NEUTRAL) {
                neutralForce.add(prevPlanets[i]);
            }
            forces.add(neutralForce);

            
            Iterator<Force> forceIterator = forces.iterator();
            Force topForce = forceIterator.next();
            Force secondForce = forceIterator.next();
            if (topForce.getForce() == secondForce.getForce()) {
                planets[i] = 0;
            } else {
                planets[i] = topForce.getForce() - secondForce.getForce();
                owners[i] = topForce.getOwner();
            }

            
            if (Log.isEnabled()) {
                StringBuilder sb = new StringBuilder("evaluateTurn complete planet: ");
                MyBot.printPlanet(sb, planets, owners, i);
                Log.log(sb.toString());
            }
        }
        numShipsInTime.add(planets);
        ownersInTime.add(owners);

        if (!myPlan.hasNext() && !enemyPlan.hasNext() &&
                myArrivals.allShipsArrived(currentTurn) && enemyArrivals.allShipsArrived(currentTurn))
            return turn(Result.FINISHED);

        return turn(Result.SUCCESS);
    }

    private Result applyTransitions(SquareMatrix transitions, int i, int[] prevPlanets, int[] prevOwners, int[] planets) {
        if (transitions == null)
            return Result.SUCCESS;
        int[][] distances = StaticPlanetsData.distances;
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

            int distance = distances[i][j];
            if (distance == 0)
                continue;

            planets[i] -= transitions.get(i, j);
            if (-planets[i] > prevPlanets[i])
                return Result.FAILED;
            //Ship takes amount of turn equal to distance to get to destination,
            //Arrivals data structure stores arrivals number by (turn - 1)
            //That is why before arrival turn number is calculated to add to arrivals
            int beforeArrivalTurn = currentTurn + distance - 1;
            if (prevOwners[i] == ME) {
                myArrivals.add(beforeArrivalTurn, j, transitions.get(i, j));
            } else if (prevOwners[i] == ENEMY) {
                enemyArrivals.add(beforeArrivalTurn, j, transitions.get(i, j));
            } else if (prevOwners[i] == NEUTRAL) {
                MyBot.fail("Attempt to apply transition from neutral planet");
            } else {
                MyBot.fail("Unknown owner code: " + prevOwners[i]);
            }
        }

        return Result.SUCCESS;
    }
}
