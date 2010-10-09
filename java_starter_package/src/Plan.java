import java.util.LinkedList;
import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 6, 2010
 * Time: 11:54:47 PM
 */
public class Plan {
    List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();

    public void addTransitions(SquareMatrix transitions) {
        transitionsInTime.add(transitions);
    }


    public Iterable<SquareMatrix> transitions() {
        return transitionsInTime;
    }

    public boolean isEmpty() {
        if (transitionsInTime.isEmpty())
            return true;

        SquareMatrix firstTurn = transitionsInTime.get(0);
        return firstTurn.isEmpty();
    }
}
