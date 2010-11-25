import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@SuppressWarnings({"PointlessBooleanExpression", "ConstantConditions"})
public class MyBot {
    public static final boolean SKIP_ON_ERROR = false;

    private static final long TIME_DIVIDER = 1;
    private static final long TIMEOUT = 700;
    @SuppressWarnings({"PointlessArithmeticExpression"})
    public static final long TIMESTOP = TIMEOUT/TIME_DIVIDER - 200;

    public static int turn = 0;
    public static long start;



    // The DoTurn function is where your code goes. The PlanetWars object
    // contains the state of the game, including information about all planets
    // and fleets that currently exist. Inside this function, you issue orders
    // using the state.IssueOrder() function. For example, to send 10 ships from
    // planet 3 to planet 8, you would say state.IssueOrder(3, 8, 10).
    //
    // There is already a basic strategy in place here. You can use it as a
    // starting point, or you can throw it out entirely and replace it with
    // your own. Check out the tutorials and articles on the contest website at
    // http://www.ai-contest.com/resources.
    public static void DoTurn(PlanetWarsState state) throws Exception {
        ++turn;
        SquareMatrix transitions;
        try {
            transitions = selectAction(state);
        } catch (Exception e) {
            if (SKIP_ON_ERROR)
                return;
            else
                throw e;
        }
        for (int i = 0; i < transitions.size(); ++i) {
            for (int j = 0; j < transitions.size(); ++j) {
                if (transitions.get(i, j) != 0) {
                    int numShips = transitions.get(i, j);
                    issueOrder(state, i, j, numShips);
                }
            }
        }
    }

    private static void issueOrder(PlanetWarsState pw, int i, int j, int numShips) {
        Log.log(turn, "Issuing order: " + i + " " + j + " " + numShips);
        if (pw.getOwner(i) != PlanetWarsState.ME) {
            Log.error("Not my planet!");
            if (SKIP_ON_ERROR)
                return;
        }
        if (pw.getNumShips(i) < numShips) {
            Log.error("Not enough ships!");
            if (SKIP_ON_ERROR)
                return;
        }
        if (numShips < 0) {
            Log.error("Negative ships!");
            if (SKIP_ON_ERROR)
                return;
        }
        PlanetWars.IssueOrder(i, j, numShips);
    }

    public static void fail(String msg) {
        if (!SKIP_ON_ERROR)
            throw new RuntimeException(msg);
    }

    private static SquareMatrix selectAction(PlanetWarsState state) {
        Collection<Plan> guessedPlans = guessGoodPlans(state);
        return findBestPlan(state, guessedPlans).transitions().iterator().next();
    }

    private static Plan findBestPlan(PlanetWarsState state, Collection<Plan> plans) {
        PlanSelection selection = new PlanSelection();
        selection.doPlanSelection(state, plans);
        return selection.getBestPlan();
    }



    public static int score(PlanetWarsState initialState, Plan plan) {
        if (Log.isEnabled()) {
            Log.log("Going to process plan:");
            Log.log(planToString(initialState, plan));
        }
        List<Plan> antiPlans = new LinkedList<Plan>();
        antiPlans.add(doNothingPlan(initialState));
        //TODO: Uncomment 2 anti-plans
//        antiPlans.addAll(AttackTargetPlanetAntiPlans.attackTargetPlanetAntiPlans(initialState, plan));
        antiPlans.addAll(AttackSourcePlanetAntiPlans.attackSourcePlanetAntiPlans(initialState, plan));
//        antiPlans.add(attackWeakestPlanetAntiPlan(initialState));

        int worseScore = Integer.MAX_VALUE;
        for (Plan antiPlan : antiPlans) {
            PlanetWarsState state = initialState.setPlans(
                    plan.transitions().iterator(),
                    antiPlan.transitions().iterator());
            if (Log.isEnabled()) {
                Log.log("Processing anti plan");
                Log.log(planToString(state, antiPlan));
            }

            Result result;
            //noinspection StatementWithEmptyBody
            for (int i = 0; i < StaticPlanetsData.maxDistance; ++i) {
                result = state.evaluateTurn();
                if (result == Result.FAILED)
                    return Integer.MIN_VALUE;
            }

//            int score = scoreNumShips(state);
            int score = (int) Math.pow(scoreNumShips(state), 3) + scoreNumEnemyCoulomb(state);
//            int score = scoreNumPlanets(state);
            if (score < worseScore) {
                if (Log.isEnabled()) {
                    StringBuilder sb = new StringBuilder("Processed anti-plan: new worseScore ").append(score).
                            append(" instead of ").append(worseScore);
                    Log.log(sb.toString());
                }
                worseScore = score;
            } else if (Log.isEnabled()) {
                StringBuilder sb = new StringBuilder("Anti-plan processed : score ").append(score);
                Log.log(sb.toString());
            }
        }
        return worseScore;
    }


    private static int scoreNumShips(PlanetWarsState state) {
        int[] lastPlanets = state.getNumShipsOnTurn(state.getCurrentTurn());
        int[] lastOwners = state.getOwnersOnTurn(state.getCurrentTurn());
        int sumShips = 0;
        for (int i = 0; i < lastPlanets.length; ++i) {
            if (lastOwners[i] != PlanetWarsState.NEUTRAL) {
                sumShips += lastPlanets[i];
            }
        }
        return sumShips;
    }


    private static int scoreNumEnemyCoulomb(PlanetWarsState state) {
        int[] planets = state.getNumShipsOnTurn(state.getCurrentTurn());
        int[] owners = state.getOwnersOnTurn(state.getCurrentTurn());
        int score = 0;
        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] == PlanetWarsState.ME) {
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[j] == PlanetWarsState.ENEMY) {
                        int d = StaticPlanetsData.distances[i][j];
                        score += Math.sqrt(planets[i] * planets[j]) / d;
                    }
                }
            }
        }
        return score;
    }


    private static int scoreNumShipsCoulomb(PlanetWarsState state) {
        int[] planets = state.getNumShipsOnTurn(state.getCurrentTurn());
        int[] owners = state.getOwnersOnTurn(state.getCurrentTurn());
        int score = 0;
        for (int i = 0; i < planets.length; ++i) {
            for (int j = 0; j < i; ++j) {
                if (owners[i] == PlanetWarsState.ME && owners[j] == PlanetWarsState.ME) {
                    int d = StaticPlanetsData.distances[i][j];
                    score += Math.sqrt(planets[i] * planets[j]) / d;
                }
            }
        }
        return score;
    }

    private static int scoreNumPlanets(PlanetWarsState state) {
        int[] owners = state.getOwnersOnTurn(state.getCurrentTurn());
        int numPlanets = 0;
        for (int owner : owners) {
            numPlanets += owner;
        }
        return numPlanets;
    }

    private static List<SquareMatrix> attackWeakestPlanetAntiPlan(PlanetWarsState state) {
        int[] planets = state.getNumShipsOnTurn(0);
        int[] owners = state.getOwnersOnTurn(0);

        SquareMatrix antiTurn = new SquareMatrix(planets.length);
        List<SquareMatrix> antiPlan = new LinkedList<SquareMatrix>();
        antiPlan.add(antiTurn);

        int weakest = Integer.MAX_VALUE;
        int weakestId = -1;
        int strongest = Integer.MIN_VALUE;
        int strongestId = -1;
        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] == PlanetWarsState.ME) {
                if (weakest > planets[i]) {
                    weakest = planets[i];
                    weakestId = i;
                }
            } else if (owners[i] == PlanetWarsState.ENEMY) {
                if (strongest < -planets[i]) {
                    strongest = planets[i];
                    strongestId = i;
                }
            }
        }

        int requiredNumShips = planets[weakestId] +
                StaticPlanetsData.growth[weakestId] * StaticPlanetsData.distances[strongestId][weakestId] + 1;
        if (strongest > requiredNumShips)
            antiTurn.set(strongestId, weakestId, -requiredNumShips);

        return antiPlan;
    }

    public static void printPlanet(StringBuilder sb, int[] planets, int[] owners, int i) {
        sb.append(i).append("(").
                append(planets[i]).append("/").
                append(owners[i]).append("/").
                append(StaticPlanetsData.growth[i]).append(")");
    }

    private static String planToString(PlanetWarsState state, Plan plan) {
        StringBuilder sb = new StringBuilder();
        int[] planets = state.getNumShipsOnTurn(0);
        int[] owners = state.getOwnersOnTurn(0);
        Iterator<SquareMatrix> iter = plan.transitions().iterator();
        for (int t = 0; iter.hasNext(); ++t) {
            sb.append("\n");
            sb.append("Print plan t: ").append(t).append(" - ");
            SquareMatrix tr = iter.next();
            int numTransitions = 0;
            for (int i = 0; i < tr.size(); ++i) {
                for (int j = 0; j < tr.size(); ++j) {
                    if (tr.get(i, j) != 0) {
                        printPlanet(sb, planets, owners, i);
                        sb.append(", ");
                        printPlanet(sb, planets, owners, j);
                        sb.append(", num ships: ").append(tr.get(i, j));
                        sb.append(", distance: ").append(StaticPlanetsData.distances[i][j]);
                        sb.append("; ");
                        ++numTransitions;
                    }
                }
            }
            sb.append("numTransitions: ").append(numTransitions);
        }
        return sb.toString();
    }

    public static int calculateAroundShips(PlanetWarsState state, int source, int target, int targetOwner) {
        int distance = StaticPlanetsData.distances[source][target];
        int enemyRadius;
        int ownerOnArrival = state.getOwnersOnTurn(distance)[target];
        if (ownerOnArrival == targetOwner) {
            //I want to keep this planet at least for one turn, this is why
            //I need to with stand all arrivals from distance + 1 radius
            enemyRadius = distance + 1;
        } else if (ownerOnArrival == PlanetWarsState.NEUTRAL) {
            int numShipsOnArrival = state.getNumShipsOnTurn(distance)[target];
            if (StaticPlanetsData.growth[target] != 0) {
                int restoreTime = (int) Math.ceil((double)numShipsOnArrival / StaticPlanetsData.growth[target]);
                enemyRadius = distance + restoreTime + 1;
            } else {
                enemyRadius = StaticPlanetsData.maxDistance;
            }

        } else {
            return 0;
        }

        for (int t = state.getCurrentTurn(); t <= enemyRadius; ++t) {
            state.evaluateTurn();
        }

        int aroundShips = 0;
        //Calculate around planet's enemy ship which could be sent onto target planet take it
        for (int i = 0; i < state.getNumPlanets(); ++i) {
            //Start time for a planet is chosen to be as late as possible to accumulate more ships. 
            int startTime = enemyRadius - StaticPlanetsData.distances[i][target];
            if (i != target && startTime >= 0 && state.getOwnersOnTurn(startTime)[i] == targetOwner) {
                aroundShips += state.getNumShipsOnTurn(startTime)[i];
            }
        }

        return aroundShips;
    }

    private static Collection<Plan> guessGoodPlans(PlanetWarsState state) {
        Collection<Plan> plans = new LinkedHashSet<Plan>();
        plans.add(doNothingPlan(state));
        addKickOutPlans(state, plans);
        addTakeOnePlanetPlans(state, 0, plans);
        int step = 15;
        for (int i = 95; i >= 0; i-=step) {
            addTakeOnePlanetPlans(state, ((float) (100 - i)) / 100, plans);
        }
        for (int i = 95; i >= 0; i-=step) {
            onePlanetDefenseAnotherPlans(state, ((float) (100 - i)) / 100, plans);
        }

        addExchangeShipsPlans(state, 0.9, plans);
        addExchangeShipsPlans(state, 0.6, plans);
        addExchangeShipsPlans(state, 0.3, plans);
        addExchangeShipsPlans(state, 0.1, plans);
        addAttackAllPlanetsPlan(state, plans);

        return plans;
    }

    private static void addExchangeShipsPlans(PlanetWarsState state, double factor, Collection<Plan> plans) {
        int[] planets = state.getNumShipsOnTurn(0);
        int[] owners = state.getOwnersOnTurn(0);


        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] == PlanetWarsState.ME) {
                for (int j = 0; j < i; ++j) {
                    if (owners[j] == PlanetWarsState.ME) {
                        SquareMatrix transitions = new SquareMatrix(planets.length);
                        transitions.set(i, j, (int)(factor*planets[i]));
                        Plan transitionsInTime = new Plan();
                        transitionsInTime.addTransitions(transitions);

                        plans.add(transitionsInTime);
                    }
                }
            }
        }
    }

    private static void addAttackAllPlanetsPlan(PlanetWarsState state, Collection<Plan> plans) {
        int[] planets = state.getNumShipsOnTurn(0);
        int[] owners = state.getOwnersOnTurn(0);

        SquareMatrix transitions = new SquareMatrix(planets.length);

        int notMyPlanetsNum = 0;
        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] != PlanetWarsState.ME) {
                ++notMyPlanetsNum;
            }
        }

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] == PlanetWarsState.ME) {
                int numShips = planets[i];
                if (numShips < notMyPlanetsNum) {
                    continue;
                }
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[i] != PlanetWarsState.ME) {
                        transitions.set(i, j, numShips/notMyPlanetsNum);
                    }
                }
            }
        }
        Plan transitionsInTime = new Plan();
        transitionsInTime.addTransitions(transitions);

        plans.add(transitionsInTime);
    }

    private static Plan doNothingPlan(PlanetWarsState state) {
        int[] planets = state.getNumShipsOnTurn(0);
        SquareMatrix transitions = new SquareMatrix(planets.length);
        Plan plan = new Plan();
        plan.addTransitions(transitions);

        return plan;
    }

    public static int requiredNumShips(PlanetWarsState state, int source, int target, int targetOwner) {
        int distance = StaticPlanetsData.distances[source][target];

        //Calculate planet ownership and number of ships on it because of arrivals
        for (int t = state.getCurrentTurn(); t <= distance; ++t) {
            state.evaluateTurn();
        }
        int[] planets = state.getNumShipsOnTurn(distance);
        int[] owners = state.getOwnersOnTurn(distance);
        int requiredNumShips = planets[target] + 1;

        if (owners[target] == targetOwner) {
            return -1;
        }

        return requiredNumShips + calculateAroundShips(state, source, target, targetOwner);
    }

    private static void addKickOutPlans(PlanetWarsState state, Collection<Plan> plans) {
        Arrivals enemyArrivals = state.getEnemyArrivals();
        int numPlanets = state.getNumPlanets();

        Set<Integer> checkIfChangedToEnemy = new HashSet<Integer>(numPlanets);
        Set<Integer> checkIfRemainsEnemy = new HashSet<Integer>(numPlanets);

        for (int t = state.getCurrentTurn(); t < state.getMaxArrivalsTurn(); ++t) {
            state.evaluateTurn();
            int[] owners = state.getOwnersOnTurn(t);
            for (int i = 0; i < numPlanets; ++i) {
                int enemyNextTurnArrivalsOnPlanet = enemyArrivals.get(t, i);
                if (enemyNextTurnArrivalsOnPlanet == 0) {
                    continue;
                } else if (enemyNextTurnArrivalsOnPlanet < 0) {
                    if (!MyBot.SKIP_ON_ERROR)
                        throw new RuntimeException("Negative arrivals detected");
                }
                if (checkIfRemainsEnemy.remove(i)) {
                    List<Integer> sortedPlanetsRow = StaticPlanetsData.sortedPlanets.get(i);
                    for (int j : sortedPlanetsRow) {
                        int timeToStart = t - StaticPlanetsData.distances[j][i];

                        if (state.getOwnersOnTurn(t)[j] != PlanetWarsState.ME) {
                            continue;
                        }

                        Plan plan = new Plan();
                        SquareMatrix tr;
                        plan.addTransitions(tr = new SquareMatrix(numPlanets));

                        for (int k = 0; k < timeToStart; ++k) {
                            plan.addTransitions(tr = new SquareMatrix(numPlanets));
                        }
                        //noinspection ConstantConditions
                        tr.set(j, i, state.getNumShipsOnTurn(t)[i] + 1);
                        if (Log.isEnabled()) {
                            Log.log("Going to add kickOut plan");
                            Log.log(planToString(state, plan));
                        }
                        plans.add(plan);
                    }
                }
                if (checkIfChangedToEnemy.remove(i)) {
                    if (owners[i] == PlanetWarsState.ENEMY) {
                        checkIfRemainsEnemy.add(i);
                    }
                }
                if (owners[i] == PlanetWarsState.NEUTRAL) {
                    checkIfChangedToEnemy.add(i);
                }
            }
        }
    }

    private static void addTakeOnePlanetPlans(PlanetWarsState state, float defenseFactor, Collection<Plan> plans) {
        int[] planets = state.getNumShipsOnTurn(0);
        int[] owners = state.getOwnersOnTurn(0);

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] != PlanetWarsState.ME) {
                List<Integer> sortedPlanetsRow = StaticPlanetsData.sortedPlanets.get(i);
                for (int j : sortedPlanetsRow) {
                    if (owners[j] == PlanetWarsState.ME) {
                        int requiredNumShips = requiredNumShips(state, j, i, PlanetWarsState.ENEMY);
                        int searchedNumShips = (int)(planets[j] * (1.0 - defenseFactor));
                        if (searchedNumShips > requiredNumShips && requiredNumShips > 0) {
                            SquareMatrix transitions = new SquareMatrix(planets.length);
                            transitions.set(j, i, requiredNumShips);
                            Plan transitionsInTime = new Plan();
                            transitionsInTime.addTransitions(transitions);

                            if (Log.isEnabled()) {
                                Log.log("Going to add takeOnePlanet plan, defense factor: " + defenseFactor);
                                Log.log(planToString(state, transitionsInTime));
                            }
                            plans.add(transitionsInTime);
                        }
                    }
                }
            }
        }
    }

    private static void onePlanetDefenseAnotherPlans(PlanetWarsState state, double factor, Collection<Plan> plans) {
        int[] planets = state.getNumShipsOnTurn(0);
        int[] owners = state.getOwnersOnTurn(0);

        for (int t = 0; t <= state.getEnemyArrivals().getMaxTurn(); ++t) {
            for (int i = 0; i < planets.length; ++i) {
                if (owners[i] == PlanetWarsState.ME) {
                    if (state.getEnemyArrivals().get(t, i) > 0) {
                        for (int j = 0; j < planets.length; ++j) {
                            if (i != j && owners[j] == PlanetWarsState.ME) {
                                SquareMatrix tr = new SquareMatrix(planets.length);
                                //TODO: Rewrite this to send only required num ships to protect a planet
                                // which is going to be lost
                                tr.set(j, i, (int) (planets[j] * factor));
                                Plan transitionsInTime = new Plan();
                                transitionsInTime.addTransitions(tr);

                                plans.add(transitionsInTime);
                            }
                        }
                    }
                }
            }
        }
    }

//    private static List<List<SquareMatrix>> takeMultiplePlanetPlans(PlanetWarsState state) {
//        int[] planets = state.getNumShipsOnTurn(0);
//        int[] owners = state.getOwnersOnTurn(0);
//
//        List<List<SquareMatrix>> plans = new ArrayList<List<SquareMatrix>>();
//
//        //TODO: Fuck!
//        for (int i = 0; i < planets.length; ++i) {
//            if (owners[i] != PlanetWarsState.ME) {
//                for (int j = 0; j < planets.length; ++j) {
//                    if (owners[j] == PlanetWarsState.ME) {
//                        int requiredNumShips = planets[i] + 1;
//                        int distance;
//                        if (j < i)
//                            distance = StaticPlanetsData.distances[i][j];
//
//                        for (int t = 0; t < distance && t < state.getArrivalsInTime().size(); ++t) {
//                            requiredNumShips += -state.getArrivalsInTime().get(t)[i];
//                            if (owners[i] == -1) {
//                                requiredNumShips += StaticPlanetsData.growth[i];
//                            }
//                        }
//                        if (planets[j] > requiredNumShips && requiredNumShips > 0) {
//                            SquareMatrix transitions = new SquareMatrix(planets.length);
//                            List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();
//                            transitions.set(j, i, requiredNumShips);
//                            transitionsInTime.add(transitions);
//                            plans.add(transitionsInTime);
//                        }
//                    }
//                }
//            }
//        }
//
//        return plans;
//    }

    public static void main(String[] args) {
        PlanetParser planetParser = new FirstTurnPlanetParser();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                start = System.currentTimeMillis();

                PlanetParser.Result parseResult = planetParser.parsePlanets(br);
                if (parseResult.finished)
                    return;
                planetParser = parseResult.parser;

                if (turn % 20 == 0)
                    System.gc();

                try {
                    DoTurn(parseResult.state);
                } catch (Throwable t) {
                    Log.error(t);
                    if (!SKIP_ON_ERROR) {
                        PlanetWars.IssueOrder(1000, 1000, 1000);
                    }
                }
                PlanetWars.FinishTurn();
            }
        } catch (Exception e) {
            Log.error("Unable to read from System.in");
            Log.error(e);
        }
    }
}

