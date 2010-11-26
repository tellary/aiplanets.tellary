/**
 * Created by Silvestrov Ilya
 * Date: Oct 9, 2010
 * Time: 12:02:50 AM
 */
public class StateEvaluationScorer implements Scorer {
    @Override
    public long score(PlanetWarsState state, Plan plan) {
        return MyBot.score(state, plan);
    }
}
