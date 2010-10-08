import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 9, 2010
 * Time: 12:03:28 AM
 */
public class SumAllScorer implements Scorer {
    private int maxValue = 100;

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public int score(PlanetWarsState state, List<SquareMatrix> plan) {
        int sum = 0;
        for (SquareMatrix step : plan) {
            for (int i = 0; i < step.size(); ++i) {
                for (int j = 0; j < step.size(); ++j) {
                    sum += step.get(i, j);
                    if (sum > maxValue)
                        return maxValue;
                }
            }
        }
        return sum;
    }
}
