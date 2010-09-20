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
    private int[] growth;
    private int[][] distances;

    public PlanetWarsState(
            List<int[]> planetsInTime,
            List<int[]> ownersInTime,
            List<int[]> arrivalsInTime,
            int[] growth,
            int[][] distances) {
        this.planetsInTime = planetsInTime;
        this.ownersInTime = ownersInTime;
        this.arrivalsInTime = arrivalsInTime;
        this.growth = growth;
        this.distances = distances;
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

    public int[] getGrowth() {
        return growth;
    }

    public int[][] getDistances() {
        return distances;
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

        return new PlanetWarsState(planetsInTime, ownersInTime, arrivalsInTime, growth, distances);
    }
}
