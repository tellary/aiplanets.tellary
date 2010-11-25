import java.util.Collections;
import java.util.Map;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 25, 2010
 * Time: 2:51:55 AM
 */
public class PlanetState {
    private int currentTurn = 0;
    private int planetId = 0;
    private int numShips = 0;
    private int owner = 0;
    private int myArrivals = 0;
    private int enemyArrivals = 0;
    private int growth = 0;
    private Map<Integer, Integer> departures = Collections.emptyMap();
    private Arrivals arrivals = null;
    private int[][] distances = StaticPlanetsData.distances;


    public PlanetState setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
        return this;
    }

    public PlanetState setPlanetId(int planetId) {
        this.planetId = planetId;
        return this;
    }

    public PlanetState setDistances(int[][] distances) {
        this.distances = distances;
        return this;
    }

    public int[][] getDistances() {
        return distances;
    }

    public PlanetState setNumShips(int numShips) {
        this.numShips = numShips;
        return this;
    }

    public void addNumShips(int numShips) {
        this.numShips += numShips;
    }

    public PlanetState setOwner(int owner) {
        this.owner = owner;
        return this;
    }

    public PlanetState setMyArrivals(int myArrivals) {
        this.myArrivals = myArrivals;
        return this;
    }

    public PlanetState setEnemyArrivals(int enemyArrivals) {
        this.enemyArrivals = enemyArrivals;
        return this;
    }

    public PlanetState setDepartures(Map<Integer, Integer> departures) {
        this.departures = departures;
        return this;
    }

    public PlanetState setArrivals(Arrivals arrivals) {
        this.arrivals = arrivals;
        return this;
    }

    public PlanetState setGrowth(int growth) {
        this.growth = growth;
        return this;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getPlanetId() {
        return planetId;
    }

    public int getNumShips() {
        return numShips;
    }

    public int getOwner() {
        return owner;
    }

    public int getMyArrivals() {
        return myArrivals;
    }

    public int getEnemyArrivals() {
        return enemyArrivals;
    }

    public Map<Integer, Integer> getDepartures() {
        return departures;
    }

    public Arrivals getArrivals() {
        return arrivals;
    }

    public int getGrowth() {
        return growth;
    }
}
