import java.util.LinkedList;
import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 25, 2010
 * Time: 8:24:38 AM
 */
public class AttackSourcePlanetAntiPlans {
    public static List<Plan> attackSourcePlanetAntiPlans(
            PlanetWarsState state, Plan plan) {

        List<Plan> plans = new LinkedList<Plan>();
        if (plan.isEmpty())
            return plans;

        SquareMatrix firstTurn = plan.transitions().iterator().next();


        int[] planets = state.getNumShipsOnTurn(0);
        int[] owners = state.getOwnersOnTurn(0);

        for (int i = 0; i < firstTurn.size(); ++i) {
            int leavers = 0;
            if (owners[i] != PlanetWarsState.ME)
                continue;
            for (int j = 0; j < firstTurn.size(); ++j) {
                leavers += firstTurn.get(i, j);
            }
            if (leavers > 0) {
                for (int k = 0; k < planets.length; ++k) {
                    if (owners[k] == PlanetWarsState.ENEMY) {
                        if (Log.isEnabled()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Attack source: from ").append(k).append("(").append(planets[k]).append(") to ");
                            sb.append(i).append("(").append(planets[i]).append(")");
                            sb.append(", distance ").append(StaticPlanetsData.distances[k][i]);
                            sb.append(", victim growth ").append(StaticPlanetsData.growth[i]).append(". ");
                            sb.append("For transition of size ").append(leavers).
                                    append(" from ").append(i).append("(").append(planets[i]).append(")");
                            Log.log(sb.toString());
                        }
                        int requiredNumShips = MyBot.requiredNumShips(state, k, i, PlanetWarsState.ENEMY);
                        if (Log.isEnabled()) {
                            StringBuilder sb = new StringBuilder("Required num ships by growth and arrivals: ");
                            sb.append(requiredNumShips);
                            Log.log(sb.toString());
                        }
                        requiredNumShips -= leavers;
                        if (Log.isEnabled()) {
                            StringBuilder sb = new StringBuilder("With transition: ");
                            sb.append(requiredNumShips);
                            Log.log(sb.toString());
                        }
//                        requiredNumShips += MyBot.calculateAroundShips(state, k, i, PlanetWarsState.ME);
//                        if (Log.isEnabled()) {
//                            StringBuilder sb = new StringBuilder("With ships around: ");
//                            sb.append(requiredNumShips);
//                            Log.log(sb.toString());
//                        }
                        if (planets[k] >= requiredNumShips && requiredNumShips > 0) {
                            SquareMatrix antiTurn = new SquareMatrix(firstTurn.size());
                            Plan antiPlan = new Plan(planets.length, StaticPlanetsData.maxDistance);
                            antiPlan.addTransitions(antiTurn);
                            antiTurn.set(k, i, requiredNumShips);
                            if (Log.isEnabled()) {
                                StringBuilder sb = new StringBuilder("Added source attack of size ").
                                        append(requiredNumShips);
                                Log.log(sb.toString());
                            }
                            plans.add(antiPlan);
                        }
                    }
                }
            }
        }

        return plans;
    }
}
