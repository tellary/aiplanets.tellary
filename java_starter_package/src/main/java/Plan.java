import java.util.Iterator;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plan plan = (Plan) o;

        //transitionsInTime is always not null
        return transitionsInTime.equals(plan.transitionsInTime);
    }

    @Override
    public int hashCode() {
        //transitionsInTime is always not null
        return transitionsInTime.hashCode();
    }

    public static Plan sum(Plan plan1, Plan plan2) {
        int size = -1;
        Iterator<SquareMatrix> plan1Iter = plan1.transitions().iterator();
        Iterator<SquareMatrix> plan2Iter = plan2.transitions().iterator();

        SquareMatrix tr, tr1, tr2;
        Plan sumPlan = new Plan();
        while (plan1Iter.hasNext() || plan2Iter.hasNext()) {
            if (plan1Iter.hasNext() && plan2Iter.hasNext()) {
                tr1 = plan1Iter.next();
                tr2 = plan2Iter.next();
                if (tr1.size() != tr2.size()) {
                    throw new RuntimeException("Fuck! Different transition matrix sizes!");
                }
                tr = new SquareMatrix(tr1.size());
                tr.add(tr1).add(tr2);
                sumPlan.addTransitions(tr);
                size = tr1.size();
            } else if (plan1Iter.hasNext()) {
                tr = plan1Iter.next();
                if (tr.size() != size) {
                    throw new RuntimeException("Fuck! Different transition matrix sizes!");
                }
                sumPlan.addTransitions(tr);

            } else if (plan2Iter.hasNext()) {
                tr = plan2Iter.next();
                if (tr.size() != size) {
                    throw new RuntimeException("Fuck! Different transition matrix sizes!");
                }
                sumPlan.addTransitions(tr);
            }
        }
        return sumPlan;
    }
}
