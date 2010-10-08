import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 9, 2010
 * Time: 12:02:50 AM
 */
public class StateEvaluationScorer implements Scorer {
    @Override
    public int score(PlanetWarsState state, List<SquareMatrix> plan) {
        return MyBot.score(state, plan);
    }
}
