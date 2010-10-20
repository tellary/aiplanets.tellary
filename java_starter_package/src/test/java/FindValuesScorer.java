/**
* Created by Silvestrov Ilya
* Date: Oct 9, 2010
* Time: 12:03:28 AM
*/
@SuppressWarnings({"FieldCanBeLocal"})
public class FindValuesScorer implements Scorer {
    private int[] values;
    private int i = 0;
    private int j = 1;

    public FindValuesScorer(int... values) {
        this.values = values;
    }

    @Override
    public int score(PlanetWarsState state, Plan plan) {
        SquareMatrix step = plan.transitions().iterator().next();
        for (int value : values) {
            if (step.get(i, j) == value) {
                return value;
            }
        }
        return 0;
    }
}
