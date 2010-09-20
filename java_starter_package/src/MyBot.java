import java.util.*;

public class MyBot {
    private static int turn = 0;
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
        ++turn;
        AsymmetricMatrix transitions;
        try {
            transitions = selectAction(createState(pw));
        } catch (Exception e) {
            Log.error(turn, e);
            return;
        }
        for (int i = 0; i < transitions.size(); ++i) {
            for (int j = 0; j < i; ++j) {
                if (transitions.get(i, j) != 0) {
                    int numShips = transitions.get(i, j);
                    if (numShips > 0) {
                        if (pw.getPlanet(i).getOwner() != PlanetWarsState.ME)
                            Log.log(turn, "Fuck!");
                        Log.log(turn, "Issuing order: " + i + " " + j + " " + numShips);
                        pw.IssueOrder(i, j, numShips);
                    } else {
                        if (pw.getPlanet(j).getOwner() != PlanetWarsState.ME)
                            Log.log(turn, "Fuck!");
                        Log.log(turn, "Issuing order: " + j + " " + i + " " + -numShips);
                        pw.IssueOrder(j, i, -numShips);
                    }
                }
            }
        }
    }

    private static AsymmetricMatrix selectAction(PlanetWarsState state) {
        List<List<AsymmetricMatrix>> guessedPlans = guessGoodPlans(state);
        return findBestPlan(state, guessedPlans).get(0);
    }

    private static PlanetWarsState createState(PlanetWars pw) {
        List<int[]> planetsInTime = new ArrayList<int[]>();
        List<int[]> ownersInTime = new ArrayList<int[]>();

        int planetsAmount = pw.planets().size();
        int[] planets = new int[planetsAmount];
        int[] owners = new int[planetsAmount];
        int[] growth = new int[planetsAmount];
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

        int[][] distances = new int[planetsAmount][];
        List<Planet> planetList = pw.planets();
        for (int i = 0; i < planets.length; ++i) {
            Planet source = planetList.get(i);
            distances[i] = new int[i];
            for (int j = 0 ; j < i; ++j) {
                Planet destination = planetList.get(j);
                double dx = source.X() - destination.X();
                double dy = source.Y() - destination.Y();
                distances[i][j] = (int)Math.ceil(Math.sqrt(dx * dx + dy * dy));
            }
        }

        return new PlanetWarsState(planetsInTime, ownersInTime, arrivalsInTime, growth, distances);
    }

    private static List<AsymmetricMatrix> findBestPlan(PlanetWarsState state, List<List<AsymmetricMatrix>> plans) {
        List<AsymmetricMatrix> bestPlan = plans.get(0);
        int bestScore = score(state, bestPlan);

        for (int i = 1; i < plans.size(); ++i) {
            int score = score(state, plans.get(i));
            if (score > bestScore) {
                bestScore = score;
                bestPlan = plans.get(i);
            }
        }
        return bestPlan;
    }

    private static int score(PlanetWarsState state, List<AsymmetricMatrix> plan) {
        state = state.copy();

        //TODO: adjust lookahead
        for (int i = 0; i < 500; ++i) {
            boolean success;
            if (i < plan.size())
                success = calculateNextTurn(state, plan.get(i), i);
            else
                success = calculateNextTurn(state, null, i);
            if (!success) {
                return Integer.MIN_VALUE;
            }
        }

        return scoreNumShips(state);
//        return scoreNumPlanets(state);
    }

    private static int scoreNumShips(PlanetWarsState state) {
        int[] lastPlanets = state.getPlanetsInTime().get(state.getPlanetsInTime().size() - 1);
        int sumShips = 0;
        for (int lastPlanet : lastPlanets) {
            sumShips += lastPlanet;
        }
        return sumShips;
    }

    private static int scoreNumPlanets(PlanetWarsState state) {
        int[] owners = state.getOwnersInTime().get(state.getOwnersInTime().size() - 1);
        int numPlanets = 0;
        for (int owner : owners) {
            numPlanets += owner;
        }
        return numPlanets;
    }

    private static boolean calculateNextTurn(PlanetWarsState state, AsymmetricMatrix transitions, int turn) {
        int planetsAmount = state.getPlanetsInTime().iterator().next().length;
        int[] planets = new int[planetsAmount];
        int[] prevPlanets = state.getPlanetsInTime().get(state.getPlanetsInTime().size() - 1);
        int[] arrivals;
        if (turn < state.getArrivalsInTime().size()) {
            arrivals = state.getArrivalsInTime().get(turn);
        } else {
            arrivals = new int[planets.length];
        }
        int[] owners = new int[planetsAmount];
        int[] prevOwners = state.getOwnersInTime().get(state.getOwnersInTime().size() - 1);
        int[] growth = state.getGrowth();

        for (int i = 0; i < planets.length; ++i) {
            planets[i] = arrivals[i] + prevOwners[i] * growth[i] + prevPlanets[i];
            if (prevPlanets[i] < 0) {
                if (planets[i] > 0)
                    owners[i] = 1;
                else
                    owners[i] = prevOwners[i];
            } else {
                if (planets[i] < 0)
                    owners[i] = -1;
                else
                    owners[i] = prevOwners[i];
            }
        }
        state.getPlanetsInTime().add(planets);
        state.getOwnersInTime().add(owners);

        if (transitions == null)
            return true;

        int[][] distances = state.getDistances();
        for (int i = 0; i < transitions.size(); ++i) {
            for (int j = 0; j < i; ++j) {
                if (transitions.get(i,j) == 0)
                    continue;
                int distance = distances[i][j];
                if (distance == 0)
                    continue;

                distance += turn - 1;

                ListIterator<int[]> iter = state.getArrivalsInTime().listIterator();
                for (int a = 0; a < distance; ++a) {
                    if (iter.hasNext())
                        arrivals = iter.next();
                    else {
                        iter.add(arrivals = new int[planetsAmount]);
                    }
                }
                if (arrivals == null)
                    throw new RuntimeException();

                if (transitions.get(i, j) > 0) {
                    arrivals[j] += transitions.get(i, j);
                    planets[i] -= transitions.get(i, j);
                    if (planets[i] < 0)
                        return false;
                } else {
                    arrivals[i] += transitions.get(j, i);
                    planets[j] -= transitions.get(j, i);
                    if (planets[j] < 0)
                        return false;
                }
            }
        }
        return true;
    }

    private static List<List<AsymmetricMatrix>> guessGoodPlans(PlanetWarsState state) {
        List<List<AsymmetricMatrix>> plan = new ArrayList<List<AsymmetricMatrix>>();
        plan.add(doNothingPlan(state));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.01));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.05));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.1));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.2));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.3));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.4));
        plan.addAll(onePlanetDefenseAnotherPlans(state, 0.7));
        plan.addAll(takeOnePlanetWithDefensePlans(state, 0.7f));
        plan.addAll(takeOnePlanetWithDefensePlans(state, 0.5f));
        plan.addAll(takeOnePlanetWithDefensePlans(state, 0.3f));
        plan.addAll(takeOnePlanetPlans(state));
        plan.add(attackAllPlanetsPlan(state));
        
        return plan;
    }

    private static List<AsymmetricMatrix> attackAllPlanetsPlan(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        AsymmetricMatrix transitions = new AsymmetricMatrix(planets.length);

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
        List<AsymmetricMatrix> transitionsInTime = new LinkedList<AsymmetricMatrix>();
        transitionsInTime.add(transitions);

        return transitionsInTime;
    }

    private static List<AsymmetricMatrix> doNothingPlan(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(0);
        AsymmetricMatrix transitions = new AsymmetricMatrix(planets.length);
        List<AsymmetricMatrix> transitionsInTime = new LinkedList<AsymmetricMatrix>();
        transitionsInTime.add(transitions);

        return transitionsInTime;
    }

    private static List<List<AsymmetricMatrix>> takeOnePlanetPlans(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        List<List<AsymmetricMatrix>> plans = new ArrayList<List<AsymmetricMatrix>>();

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] < 1) {
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[j] == 1) {
                        int requiredNumShips = -planets[i] + 1;
                        int distance;
                        if (j < i)
                            distance = state.getDistances()[i][j];
                        else
                            distance = state.getDistances()[j][i];
                        for (int t = 0; t < distance && t < state.getArrivalsInTime().size(); ++t) {
                            requiredNumShips += -state.getArrivalsInTime().get(t)[i];
                            if (owners[i] == -1) {
                                requiredNumShips += state.getGrowth()[i];
                            }
                        }
                        if (planets[j] > requiredNumShips && requiredNumShips > 0) {
                            AsymmetricMatrix transitions = new AsymmetricMatrix(planets.length);
                            List<AsymmetricMatrix> transitionsInTime = new LinkedList<AsymmetricMatrix>();
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

    private static List<List<AsymmetricMatrix>> takeOnePlanetWithDefensePlans(PlanetWarsState state, float defenseFactor) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        List<List<AsymmetricMatrix>> plans = new ArrayList<List<AsymmetricMatrix>>();

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] < 1) {
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[j] == 1) {
                        int requiredNumShips = -planets[i] + 1;
                        int distance;
                        if (j < i)
                            distance = state.getDistances()[i][j];
                        else
                            distance = state.getDistances()[j][i];
                        for (int t = 0; t < distance && t < state.getArrivalsInTime().size(); ++t) {
                            requiredNumShips += -state.getArrivalsInTime().get(t)[i];
                            if (owners[i] == -1) {
                                requiredNumShips += state.getGrowth()[i];
                            }
                        }
                        if (planets[j] * (1.0 - defenseFactor) > requiredNumShips && requiredNumShips > 0) {
                            AsymmetricMatrix transitions = new AsymmetricMatrix(planets.length);
                            transitions.set(j, i, requiredNumShips);
                            List<AsymmetricMatrix> transitionsInTime = new LinkedList<AsymmetricMatrix>();
                            transitionsInTime.add(transitions);
                            plans.add(transitionsInTime);
                        }
                    }
                }
            }
        }

        return plans;
    }

    private static List<List<AsymmetricMatrix>> onePlanetDefenseAnotherPlans(PlanetWarsState state, double factor) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        List<List<AsymmetricMatrix>> plans = new ArrayList<List<AsymmetricMatrix>>();

        for (int t = 0; t < state.getArrivalsInTime().size(); ++t) {
            for (int i = 0; i < planets.length; ++i) {
                if (owners[i] != -1) {
                    if (state.getArrivalsInTime().get(t)[i] < 0) {
                        for (int j = 0; j < planets.length; ++j) {
                            if (i != j) {
                                AsymmetricMatrix tr = new AsymmetricMatrix(planets.length);
                                tr.set(j, i, (int) (planets[j] * factor));
                                List<AsymmetricMatrix> transitionsInTime = new LinkedList<AsymmetricMatrix>();
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

    private static List<List<AsymmetricMatrix>> takeMultiplePlanetPlans(PlanetWarsState state) {
        int[] planets = state.getPlanetsInTime().get(0);
        int[] owners = state.getOwnersInTime().get(0);

        List<List<AsymmetricMatrix>> plans = new ArrayList<List<AsymmetricMatrix>>();

        for (int i = 0; i < planets.length; ++i) {
            if (owners[i] < 1) {
                for (int j = 0; j < planets.length; ++j) {
                    if (owners[j] == 1) {
                        int requiredNumShips = -planets[i] + 1;
                        int distance;
                        if (j < i)
                            distance = state.getDistances()[i][j];
                        else
                            distance = state.getDistances()[j][i];
                        for (int t = 0; t < distance && t < state.getArrivalsInTime().size(); ++t) {
                            requiredNumShips += -state.getArrivalsInTime().get(t)[i];
                            if (owners[i] == -1) {
                                requiredNumShips += state.getGrowth()[i];
                            }
                        }
                        if (planets[j] > requiredNumShips && requiredNumShips > 0) {
                            AsymmetricMatrix transitions = new AsymmetricMatrix(planets.length);
                            List<AsymmetricMatrix> transitionsInTime = new LinkedList<AsymmetricMatrix>();
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

