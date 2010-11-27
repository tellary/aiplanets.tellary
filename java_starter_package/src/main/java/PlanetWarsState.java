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

    private ArrayList<int[]> numShipsInTime;
    private ArrayList<int[]> ownersInTime;
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

        this.numShipsInTime = new ArrayList<int[]>(StaticPlanetsData.maxDistance*10);
        this.numShipsInTime.add(planets);
        this.ownersInTime = new ArrayList<int[]>(StaticPlanetsData.maxDistance*10);
        this.ownersInTime.add(owners);
        this.myArrivals = myArrivals;
        this.enemyArrivals = enemyArrivals;
    }

    public int getNumShips(int i) {
        return numShipsInTime.get(0)[i];
    }

    public int getOwner(int i) {
        return ownersInTime.get(0)[i];
    }

    public int[] getNumShipsOnTurn(int turn) {
        return numShipsInTime.get(turn);
    }

    public int getNumPlanets() {
        return numShipsInTime.get(0).length;
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
        int[] planets = numShipsInTime.get(0);
        int[] planetsCopy = new int[planets.length];
        System.arraycopy(planets, 0, planetsCopy, 0, planets.length);
        int[] owners = ownersInTime.get(0);
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

    static void printPlanet(StringBuilder sb, PlanetState state) {
        sb.append(state.getPlanetId()).append("(").
                append(state.getNumShips()).append("/").
                append(state.getOwner()).append("/").
                append(StaticPlanetsData.growth[state.getPlanetId()]).append(")");
    }

    static Result evaluateTurnForPlanet(PlanetState state) {
        if (Log.isEnabled()) {
            StringBuilder sb = new StringBuilder("evaluateTurn arrivals: planet ");
            printPlanet(sb, state);
            sb.append(", turn: ").append(state.getCurrentTurn()).
                    append(", my arrivals: ").append(state.getMyArrivals()).
                    append(", enemy arrivals: ").append(state.getEnemyArrivals());
            Log.log(sb.toString());
        }

        //Departure phase handling
        if (applyTransitions(state) != Result.SUCCESS)
            return Result.FAILED;

        //Planet Advancement (fleet advancement is handled by arrivals by turn indexing in Arrivals data structure
        if (state.getOwner() != NEUTRAL)
            state.addNumShips(state.getGrowth());

        //Arrival phase handling
        //TODO: write test fo forces handling
        TreeSet<Force> forces = new TreeSet<Force>();
        Force myForce = new Force(ME);
        if (state.getOwner() == ME) {
            myForce.add(state.getNumShips());
        }
        myForce.add(state.getMyArrivals());
        forces.add(myForce);

        Force enemyForce = new Force(ENEMY);
        if (state.getOwner() == ENEMY) {
            enemyForce.add(state.getNumShips());
        }
        enemyForce.add(state.getEnemyArrivals());
        forces.add(enemyForce);

        Force neutralForce = new Force(NEUTRAL);
        if (state.getOwner() == NEUTRAL) {
            neutralForce.add(state.getNumShips());
        }
        forces.add(neutralForce);


        Iterator<Force> forceIterator = forces.iterator();
        Force topForce = forceIterator.next();
        Force secondForce = forceIterator.next();
        if (topForce.getForce() == secondForce.getForce()) {
            state.setNumShips(0);
        } else {
            state.setNumShips(topForce.getForce() - secondForce.getForce());
            state.setOwner(topForce.getOwner());
        }

        if (Log.isEnabled()) {
            StringBuilder sb = new StringBuilder("evaluateTurn complete planet: ");
            printPlanet(sb, state);
            Log.log(sb.toString());
        }

        return Result.SUCCESS;
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

            Map<Integer, Integer> departures;
            Arrivals arrivals = null;
            if (prevOwners[i] == ME) {
                if (myTransitions != null)
                    departures = myTransitions.getRow(i);
                else
                    departures = Collections.emptyMap();
                arrivals = myArrivals;
            } else if (prevOwners[i] == ENEMY) {
                if (enemyTransitions != null)
                    departures = enemyTransitions.getRow(i);
                else
                    departures = Collections.emptyMap();
                arrivals = enemyArrivals;
            } else {
                if (myTransitions != null && !myTransitions.getRow(i).isEmpty())
                    MyBot.fail("My transitions are not empty for neutral planet");
                if (enemyTransitions != null && !enemyTransitions.getRow(i).isEmpty())
                    MyBot.fail("Enemy transitions are not empty for neutral planet");
                departures = Collections.emptyMap();
            }
            PlanetState state =
                    new PlanetState().
                            setCurrentTurn(currentTurn).
                            setPlanetId(i).
                            setGrowth(StaticPlanetsData.growth[i]).
                            setNumShips(prevPlanets[i]).
                            setOwner(prevOwners[i]).
                            setMyArrivals(myArrivalsForPlanet).
                            setEnemyArrivals(enemyArrivalsForPlanet).
                            setDepartures(departures).
                            setArrivals(arrivals);
            if (evaluateTurnForPlanet(state) == Result.FAILED) {
                return turn(Result.FAILED);
            }
            owners[i] = state.getOwner();
            planets[i] = state.getNumShips();
        }
        numShipsInTime.add(planets);
        ownersInTime.add(owners);

        if (!myPlan.hasNext() && !enemyPlan.hasNext() &&
                myArrivals.allShipsArrived(currentTurn) && enemyArrivals.allShipsArrived(currentTurn))
            return turn(Result.FINISHED);

        return turn(Result.SUCCESS);
    }

    private static Result applyTransitions(PlanetState state) {
        int[][] distances = state.getDistances();

        for (Map.Entry<Integer, Integer> departure : state.getDepartures().entrySet()) {
            if (departure.getValue() == 0)
                continue;

            int targetPlanet = departure.getKey();
            if (Log.isEnabled()) {
                StringBuilder sb = new StringBuilder("Going to apply transition from planet ");
                printPlanet(sb, state);
                sb.append(" to planet ");
                sb.append(targetPlanet);
                sb.append(", num ships: ").append(departure.getValue());
                sb.append(", distance: ").append(distances[state.getPlanetId()][departure.getKey()]);
                Log.log(sb.toString());
            }

            int distance = distances[state.getPlanetId()][targetPlanet];
            if (distance == 0)
                continue;

            state.addNumShips(-departure.getValue());
            if (state.getNumShips() < 0)
                return Result.FAILED;
            int beforeArrivalTurn = state.getCurrentTurn() + distance - 1;
            state.getArrivals().add(beforeArrivalTurn, targetPlanet, departure.getValue());
        }

        return Result.SUCCESS;
    }
}
