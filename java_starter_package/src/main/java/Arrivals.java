import java.util.HashMap;
import java.util.Map;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 23, 2010
 * Time: 2:10:57 AM
 */
public class Arrivals implements Cloneable {
    private Map<Integer, Map<Integer, Integer>> myArrivals;
    private int maxTurn = 0;
    private int numPlanets;


    public Arrivals(int numPlanets) {
        this.numPlanets = numPlanets;
        this.myArrivals = new HashMap<Integer, Map<Integer, Integer>>();
    }

    public int getMaxTurn() {
        return maxTurn;
    }

    /**
     * @param turn - number of turn, 0 - current, 1 - next turn etc.
     * @param planetIdx - index of planet ships will arrive at
     * @return number of ships which will arrive on planet on next turn (turn + 1)
     */
    public int get(int turn, int planetIdx) {
        myArrivals.get(turn);
        if (turn > maxTurn) {
            return 0;
        }
        Map<Integer, Integer> turnArrivals = myArrivals.get(turn);
        if (turnArrivals == null) {
            return 0;
        }
        Integer numArrivals = turnArrivals.get(planetIdx);
        if (numArrivals == null) {
            return 0;
        }
        return numArrivals;
    }

    public void add(int turn, int planetIdx, int num) {
        if (turn > maxTurn) {
            maxTurn = turn;
        }
        Map<Integer, Integer> turnArrivals = myArrivals.get(turn);
        if (turnArrivals == null) {
            turnArrivals = new HashMap<Integer, Integer>(numPlanets);
            myArrivals.put(turn, turnArrivals);
        }
        Integer currentNumArrivals = turnArrivals.get(planetIdx);
        if (currentNumArrivals == null) {
            currentNumArrivals = 0;
        }
        currentNumArrivals += num;
        turnArrivals.put(planetIdx, currentNumArrivals);
    }

    public Arrivals copy() {
        Arrivals copy = new Arrivals(numPlanets);
        copy.maxTurn = maxTurn;
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : myArrivals.entrySet()) {
            Map<Integer, Integer> turnCopy = new HashMap<Integer, Integer>(entry.getValue());
            copy.myArrivals.put(entry.getKey(), turnCopy);
        }
        return copy;
    }

    public boolean allShipsArrived(int currentTurn) {
        return currentTurn >= maxTurn;
    }
}
