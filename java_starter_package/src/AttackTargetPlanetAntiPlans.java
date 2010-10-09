import java.util.LinkedList;
import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 25, 2010
 * Time: 8:22:27 AM
 */
public class AttackTargetPlanetAntiPlans {
    public static List<Plan> attackTargetPlanetAntiPlans(
            PlanetWarsState state, Plan plan) {

        List<Plan> plans = new LinkedList<Plan>();
        if (plan.isEmpty())
            return plans;

        SquareMatrix firstTurn = plan.transitions().iterator().next();


        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        for (int i = 0; i < firstTurn.size(); ++i) {
            for (int j = 0; j < firstTurn.size(); ++j) {
                if (firstTurn.get(i, j) > 0) {
                    for (int k = 0; k < planets.length; ++k) {
                        if (owners[k] == -1 && k != i && k != j) {
                            if (Log.isEnabled()) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Attack target: from ").append(k).append("(").append(planets[k]).append(") to ");
                                sb.append(j).append("(").append(planets[j]).append(")");
                                sb.append(", distance ").append(MyBot.distances[k][j]);
                                sb.append(", victim growth ").append(MyBot.growth[j]).append(". ");
                                sb.append("For transition of size ").append(firstTurn.get(i, j)).
                                        append(" from ").append(i).append("(").append(planets[i]).append(") ").
                                        append(" to ").append(j).append("(").append(planets[j]).append(")");
                                Log.log(sb.toString());
                            }
                            int requiredNumShips = MyBot.requiredNumShipsForAttackAntiPlan(state, k, j);
                            if (Log.isEnabled()) {
                                StringBuilder sb = new StringBuilder("Required num ships by growth and arrivals: ");
                                sb.append(requiredNumShips);
                                Log.log(sb.toString());
                            }
                            requiredNumShips += firstTurn.get(i, j);
                            if (Log.isEnabled()) {
                                StringBuilder sb = new StringBuilder("With transition: ");
                                sb.append(requiredNumShips);
                                Log.log(sb.toString());
                            }
                            requiredNumShips += MyBot.calculateAroundShips(state, k, j, firstTurn);
                            if (Log.isEnabled()) {
                                StringBuilder sb = new StringBuilder("With ships around: ");
                                sb.append(requiredNumShips);
                                Log.log(sb.toString());
                            }
                            if (-planets[k] > requiredNumShips && requiredNumShips > 0) {
                                SquareMatrix antiTurn = new SquareMatrix(firstTurn.size());
                                Plan antiPlan = new Plan();
                                antiPlan.addTransitions(antiTurn);
                                antiTurn.set(k, j, -requiredNumShips);
                                if (Log.isEnabled()) {
                                    StringBuilder sb = new StringBuilder("Added target attack of size ").
                                            append(requiredNumShips);
                                    Log.log(sb.toString());
                                }
                                plans.add(antiPlan);
                            }
                        }
                    }
                }
            }
        }

        return plans;
    }
}
