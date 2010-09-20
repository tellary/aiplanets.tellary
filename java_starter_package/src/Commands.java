import java.util.ArrayList;
import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 19, 2010
 * Time: 2:03:53 AM
 */
public class Commands {
    List<int[][]> transitionsInTime = new ArrayList<int[][]>();
    
    public int numShips(int from, int to, int turn) {
        return transitionsInTime.get(turn)[from][to];
    }

    public int[][] transitions(int turn) {
        return transitionsInTime.get(turn);
    }

    public int[][] transitions() {
        return transitions(0);
    }
}
