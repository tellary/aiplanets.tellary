import java.util.*;

public class MyBot {
    private static int turn = 0;
    private static long start;

    public static int[][] distances;
    public static int avgDistance;
    public static int maxDistance;

    public static int[] growth;
    // The DoTurn function is where your code goes. The PlanetWars object
    // contains the state of the game, including information about all planets
    // and fleets that currently exist. Inside this function, you issue orders
    // using the pw.IssueOrder() function. For example, to send 10 ships from
    // planet 3 to planet 8, you would say pw.IssueOrder(3, 8, 10).
    //
    // There is already a basic strategy in place here. You can use it as a
    // starting point, or you can throw it out entirely and replace it with
    // your own. Check out the tutorials and articles on the contest website at
    // http://www.ai-contest.com/resources.
    public static void DoTurn(PlanetWars pw) {
        if (turn == 0) {
            List<Planet> planetList = pw.planets();
            int planetsAmount = planetList.size();

            distances = new int[planetsAmount][planetsAmount];
            growth = new int[planetsAmount];
            avgDistance = 0;
            int avgNum = 0;
            for (int i = 0; i < planetsAmount; ++i) {
                Planet source = planetList.get(i);
                for (int j = 0 ; j < planetsAmount; ++j) {
                    Planet destination = planetList.get(j);
                    double dx = source.X() - destination.X();
                    double dy = source.Y() - destination.Y();
                    distances[i][j] = (int)Math.ceil(Math.sqrt(dx * dx + dy * dy));
                    if (i != j) {
                        avgDistance += distances[i][j];
                        ++avgNum;
                    }
                    if (distances[i][j] > maxDistance)
                        maxDistance = distances[i][j];
                }
                growth[i] = planetList.get(i).getGrowthRate();
            }
            avgDistance = (int) (((double)avgDistance)/avgNum);

        }
        ++turn;
        SquareMatrix transitions;
        try {
            transitions = selectAction(createState(pw));
        } catch (Exception e) {
            Log.error(turn, e);
            return;
        }
        for (int i = 0; i < transitions.size(); ++i) {
            for (int j = 0; j < transitions.size(); ++j) {
                if (transitions.get(i, j) != 0) {
                    int numShips = transitions.get(i, j);
                    issueOrder(pw, i, j, numShips);
                }
            }
        }
    }

    private static void issueOrder(PlanetWars pw, int i, int j, int numShips) {
        Log.log(turn, "Issuing order: " + i + " " + j + " " + numShips);
        if (pw.getPlanet(i).getOwner() != PlanetWarsState.ME)
            Log.log(turn, "Not my planet!");
        if (pw.getPlanet(i).getNumShips() < numShips)
            Log.log(turn, "Not enough ships!");
        pw.IssueOrder(i, j, numShips);
    }

    private static SquareMatrix selectAction(PlanetWarsState state) {
        List<List<SquareMatrix>> guessedPlans = guessGoodPlans(state);
        return findBestPlan(state, guessedPlans).get(0);
    }

    private static PlanetWarsState createState(PlanetWars pw) {
        List<int[]> planetsInTime = new ArrayList<int[]>();
        List<int[]> ownersInTime = new ArrayList<int[]>();

        int planetsAmount = pw.planets().size();
        int[] planets = new int[planetsAmount];
        int[] owners = new int[planetsAmount];
        for (Planet planet : pw.planets()) {
            growth[planet.getPlanetId()] = planet.getGrowthRate();
            if (planet.getOwner() == PlanetWarsState.ME) {
                planets[planet.getPlanetId()] = planet.getNumShips();
                owners[planet.getPlanetId()] = 1;
            } else {
                planets[planet.getPlanetId()] = -planet.getNumShips();
                if (planet.getOwner() == PlanetWarsState.ENEMY) {
                    owners[planet.getPlanetId()] = -1;
                } else {
                    owners[planet.getPlanetId()] = 0;
                }
            }
        }

        List<int[]> arrivalsInTime = new ArrayList<int[]>();
        for (Fleet f : pw.fleets()) {
            int turns = f.getTurnsRemaining();
            ListIterator<int[]> iter = arrivalsInTime.listIterator();
            int[] arrivals = null;
            for (int i = 0; i < turns; ++i) {
                if (iter.hasNext())
                    arrivals = iter.next();
                else {
                    iter.add(arrivals = new int[planetsAmount]);
                }
            }
            if (arrivals == null)
                throw new RuntimeException();
            if (f.getOwner() == PlanetWarsState.ME)
                arrivals[f.getDestinationPlanet()] += f.getNumShips();
            else
                arrivals[f.getDestinationPlanet()] -= f.getNumShips();
        }
        planetsInTime.add(planets);
        ownersInTime.add(owners);


        return new PlanetWarsState(planetsInTime, ownersInTime, arrivalsInTime);
    }

    private static List<SquareMatrix> findBestPlan(PlanetWarsState state, List<List<SquareMatrix>> plans) {
        List<SquareMatrix> bestPlan = plans.get(0);
        int bestScore = score(state, bestPlan);

        for (int i = 1; i < plans.size(); ++i) {
            int score = score(state, plans.get(i));
            if (shouldStop())
                return bestPlan;
            if (score > bestScore) {
                bestScore = score;
                bestPlan = plans.get(i);
            }
        }
        return bestPlan;
    }

    private static boolean shouldStop() {
        //noinspection SimplifiableIfStatement
        if ("true".equals(System.getProperty("debug"))) {
            return false;
        }
        return System.currentTimeMillis() - start > 800;
    }

    private static int score(PlanetWarsState state, List<SquareMatrix> plan) {
        List<List<SquareMatrix>> antiPlans = new LinkedList<List<SquareMatrix>>();
        antiPlans.add(doNothingPlan(state));
//        antiPlans.add(attackSourcePlanetAntiPlan(state, plan));

        int worseScore = Integer.MAX_VALUE;
        for (List<SquareMatrix> antiPlan : antiPlans) {
            state = state.copy();

            state.setMyPlan(plan.iterator());
            state.setEnemyPlan(antiPlan.iterator());
            Result result;
            //noinspection StatementWithEmptyBody
            while ((result = state.evaluateTurn()) == Result.SUCCESS);

            if (result == Result.FAILED)
                return Integer.MIN_VALUE;

//        return scoreNumShipsCoulomb(state);
            int score = scoreNumShips(state);
//            int score = scoreNumPlanets(state);
            if (score < worseScore) {
                worseScore = score;
            }
        }
        return worseScore;
    }

    private static int scoreNumShips(PlanetWarsState state) {
        int[] lastPlanets = state.getPlanetsInTime().get(state.getPlanetsInTime().size() - 1);
        int[] lastOwners = state.getOwnersInTime().get(state.getOwnersInTime().size() - 1);
        int sumShips = 0;
        for (int i = 0; i < lastPlanets.length; ++i) {
            if (lastOwners[i] != 0) {
                sumShips += lastPlanets[i];
//                sumShips += 1000 * growth[i];
            }
        }
        return sumShips;
    }

    private static int scoreNumShipsCoulomb(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(state.getPlanetsInTime().size() - 1);
        int[] owners = state.getOwnersInTime().get(state.getOwnersInTime().size() - 1);
        int score = 0;
        for (int i = 0; i < planets.length; ++i) {
            for (int j = 0; j <= i; ++j) {
                if (owners[i] == 1 && owners[j] == 1) {
                    if (i != j) {
                        int d = distances[i][j];
                        score += (int) ((double) planets[i]) * planets[j] / (d*d);
                    } else {
//                        score += planets[i] * planets[j];
                    }
                }
            }
        }
        return score;
//        return score;
    }

    private static int scoreNumPlanets(PlanetWarsState state) {
        int[] owners = state.getOwnersInTime().get(state.getOwnersInTime().size() - 1);
        int numPlanets = 0;
        for (int owner : owners) {
            numPlanets += owner;
        }
        return numPlanets;
    }

    private static List<SquareMatrix> attackSourcePlanetAntiPlan(PlanetWarsState state, List<SquareMatrix> plan) {
        SquareMatrix firstTurn = plan.get(0);

        SquareMatrix antiTurn = new SquareMatrix(firstTurn.size());
        List<SquareMatrix> antiPlan = new LinkedList<SquareMatrix>();
        antiPlan.add(antiTurn);

        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);
        for (int i = 0; i < firstTurn.size(); ++i) {
            for (int j = 0; j < firstTurn.size(); ++j) {
                if (firstTurn.get(i, j) > 0) {
                    for (int k = 0; k < planets.length; ++k) {
                        int requiredNumShips = planets[i] - firstTurn.get(i, j) + growth[i] * distances[k][i] + 1;
                        if (owners[k] == -1 && -planets[k] > requiredNumShips) {
                            antiTurn.set(k, i, requiredNumShips);
                        }
                    }
                }
            }
        }

        return antiPlan;
    }

    private static List<List<SquareMatrix>> guessGoodPlans(PlanetWarsState state) {
        List<List<SquareMatrix>> plan = new ArrayList<List<SquareMatrix>>();
        plan.add(doNothingPlan(state));
        plan.addAll(takeOnePlanetPlans(state));
        plan.addAll(takeOnePlanetWithDefensePlans(state, 0.7f));
        plan.addAll(takeOnePlanetWithDefensePlans(state, 0.5f));
        plan.addAll(takeOnePlanetWithDefensePlans(state, 0.3f));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.7));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.3));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.1));
//        plan.addAll(exchangeShipsPlan(state, 0.9));
//        plan.addAll(exchangeShipsPlan(state, 0.6));
        plan.addAll(exchangeShipsPlan(state, 0.3));
//        plan.addAll(exchangeShipsPlan(state, 0.1));
        plan.add(attackAllPlanetsPlan(state));

        return plan;
    }

    private static List<List<SquareMatrix>> exchangeShipsPlan(PlanetWarsState state, double factor) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);


        List<List<SquareMatrix>> plans = new LinkedList<List<SquareMatrix>>();
        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] == 1) {
                for (int j = 0; j < i; ++j) {
                    if (owners[j] == 1) {
                        SquareMatrix transitions = new SquareMatrix(planets.length);
                        transitions.set(i, j, (int)factor*planets[i]);
                        List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();
                        transitionsInTime.add(transitions);
                        plans.add(transitionsInTime);
                    }
                }
            }
        }



        return plans;
    }

    private static List<SquareMatrix> attackAllPlanetsPlan(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        SquareMatrix transitions = new SquareMatrix(planets.length);

        int notMyPlanetsNum = 0;
        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] < 1) {
                ++notMyPlanetsNum;
            }
        }

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] == 1) {
                int numShips = planets[i];
                if (numShips < notMyPlanetsNum) {
                    continue;
                }
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[i] < 1) {
                        transitions.set(i, j, numShips/notMyPlanetsNum);
                    }
                }
            }
        }
        List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();
        transitionsInTime.add(transitions);

        return transitionsInTime;
    }

    private static List<SquareMatrix> doNothingPlan(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(0);
        SquareMatrix transitions = new SquareMatrix(planets.length);
        List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();
        transitionsInTime.add(transitions);

        return transitionsInTime;
    }

    private static List<List<SquareMatrix>> takeOnePlanetPlans(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        List<List<SquareMatrix>> plans = new ArrayList<List<SquareMatrix>>();

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] < 1) {
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[j] == 1) {
                        int requiredNumShips = -planets[i] + 1;
                        int distance = distances[i][j];
                        for (int t = 0; t < distance && t < state.getArrivalsInTime().size(); ++t) {
                            requiredNumShips += -state.getArrivalsInTime().get(t)[i];
                            if (owners[i] == -1) {
                                requiredNumShips += growth[i];
                            }
                        }
                        if (planets[j] > requiredNumShips && requiredNumShips > 0) {
                            SquareMatrix transitions = new SquareMatrix(planets.length);
                            List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();
                            transitions.set(j, i, requiredNumShips);
                            transitionsInTime.add(transitions);
                            plans.add(transitionsInTime);
                        }
                    }
                }
            }
        }

        return plans;
    }

    private static List<List<SquareMatrix>> takeOnePlanetWithDefensePlans(PlanetWarsState state, float defenseFactor) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        List<List<SquareMatrix>> plans = new ArrayList<List<SquareMatrix>>();

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] < 1) {
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[j] == 1) {
                        int requiredNumShips = -planets[i] + 1;
                        int distance;
                        if (j < i)
                            distance = distances[i][j];
                        else
                            distance = distances[j][i];
                        for (int t = 0; t < distance && t < state.getArrivalsInTime().size(); ++t) {
                            requiredNumShips += -state.getArrivalsInTime().get(t)[i];
                            if (owners[i] == -1) {
                                requiredNumShips += growth[i];
                            }
                        }
                        if (planets[j] * (1.0 - defenseFactor) > requiredNumShips && requiredNumShips > 0) {
                            SquareMatrix transitions = new SquareMatrix(planets.length);
                            transitions.set(j, i, requiredNumShips);
                            List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();
                            transitionsInTime.add(transitions);
                            plans.add(transitionsInTime);
                        }
                    }
                }
            }
        }

        return plans;
    }

    private static List<List<SquareMatrix>> onePlanetDefenseAnotherPlans(PlanetWarsState state, double factor) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        List<List<SquareMatrix>> plans = new ArrayList<List<SquareMatrix>>();

        for (int t = 0; t < state.getArrivalsInTime().size(); ++t) {
            for (int i = 0; i < planets.length; ++i) {
                if (owners[i] != -1) {
                    if (state.getArrivalsInTime().get(t)[i] < 0) {
                        for (int j = 0; j < planets.length; ++j) {
                            if (i != j) {
                                SquareMatrix tr = new SquareMatrix(planets.length);
                                tr.set(j, i, (int) (planets[j] * factor));
                                List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();
                                transitionsInTime.add(tr);
                                plans.add(transitionsInTime);
                            }
                        }
                    }
                }
            }
        }

        return plans;
    }

    private static List<List<SquareMatrix>> takeMultiplePlanetPlans(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        List<List<SquareMatrix>> plans = new ArrayList<List<SquareMatrix>>();

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] < 1) {
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[j] == 1) {
                        int requiredNumShips = -planets[i] + 1;
                        int distance;
                        if (j < i)
                            distance = distances[i][j];
                        else
                            distance = distances[j][i];
                        for (int t = 0; t < distance && t < state.getArrivalsInTime().size(); ++t) {
                            requiredNumShips += -state.getArrivalsInTime().get(t)[i];
                            if (owners[i] == -1) {
                                requiredNumShips += growth[i];
                            }
                        }
                        if (planets[j] > requiredNumShips && requiredNumShips > 0) {
                            SquareMatrix transitions = new SquareMatrix(planets.length);
                            List<SquareMatrix> transitionsInTime = new LinkedList<SquareMatrix>();
                            transitions.set(j, i, requiredNumShips);
                            transitionsInTime.add(transitions);
                            plans.add(transitionsInTime);
                        }
                    }
                }
            }
        }

        return plans;
    }

    public static void main(String[] args) {
        String line = "";
        String message = "";
        int c;
        try {
            while ((c = System.in.read()) >= 0) {
                switch (c) {
                    case '\n':
                        if (line.equals("go")) {
                            start = System.currentTimeMillis();
                            if (turn % 10 == 0)
                                System.gc();
                            PlanetWars pw = new PlanetWars(message);
                            DoTurn(pw);
                            pw.FinishTurn();
                            message = "";
                        } else {
                            message += line + "\n";
                        }
                        line = "";
                        break;
                    default:
                        line += (char) c;
                        break;
                }
            }
        } catch (Exception e) {
            Log.error(turn, e);
        } catch (Throwable t) {
            Log.error(turn, t);
        }
    }
}

