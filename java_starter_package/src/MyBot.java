import java.util.*;

public class MyBot {
    private static int turn = 0;
    private static Random random = new Random(19008);
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
        List<Command> commands;
        try {
            commands = randomSelectAction(pw);
        } catch (Exception e) {
            Log.error(turn, e);
            return;
        }
        for (Command command : commands) {
            int sourcePlanetId = command.getSourcePlanet().getPlanetId();
            if (pw.getPlanet(sourcePlanetId).getOwner() != PlanetWarsState.ME)
                Log.log(turn, "Fuck!");
            if (command.getNumShips() > 0) {
                Log.log(turn, "Issuing order: " + sourcePlanetId + " " + command.getDestinationPlanetId() + " " + command.getNumShips());
                pw.IssueOrder(
                        sourcePlanetId,
                        command.getDestinationPlanetId(),
                        command.getNumShips());
            }
        }
    }

    public static final double delta = 0.1;

    private static List<Command> selectAction(PlanetWars pw) {
        long startTime = System.currentTimeMillis();

        List<Command> bestCommands = new LinkedList<Command>();
        int bestScore = 0;

        List<Command> commands = new LinkedList<Command>();
        Log.log(turn, "Going to prepare initial state, myPlanets.size=" + pw.myPlanets().size() + ", nonMyPlanets.size=" + pw.notMyPlanets().size());
        for (Planet myPlanet : pw.myPlanets()) {
            for (Planet notMyPlanet : pw.notMyPlanets()) {
                commands.add(new Command(myPlanet, notMyPlanet.getPlanetId(), 0));
            }
        }
        PlanetWarsState state = new PlanetWarsState(pw, commands);
        Log.log(turn, "Initial state prepared");
        if (!state.isBadState()) {
            bestScore = score(state);
            Log.log(turn, "Initial state processed, score: " + bestScore);
            bestCommands = cloneCommands(commands);
        } else {
            Log.log(turn, "Initial state is bad");
        }

        int atts = 1;

        int curIdx = 0;
        while (curIdx < commands.size() && !timeOut (atts, startTime)) {
            ++atts;
            Command command = commands.get(curIdx);
            int numShips = command.getNumShips() + (int) (command.getSourcePlanet().getNumShips()*delta);
            if (numShips <= command.getSourcePlanet().getNumShips()) {
                command.setNumShips(numShips);
            } else {
                do {
                    ++curIdx;
                    command = commands.get(curIdx);
                    numShips = command.getNumShips() + (int) (command.getSourcePlanet().getNumShips()*delta);
                    if (timeOut (atts, startTime)) {
                        Log.log(turn, "Exiting from internal loop, bestScore: " + bestScore);
                        return bestCommands;
                    }
                } while (numShips > command.getSourcePlanet().getNumShips());

                command.setNumShips(numShips);
                for (int i = 0; i < curIdx; ++i) {
                    command = commands.get(i);
                    command.setNumShips(0);
                }
                curIdx = 0;
            }
            state = new PlanetWarsState(pw, commands);
            if (!state.isBadState()) {
                int score = score(state);
                if (bestScore < score) {
                    bestScore = score;
                    bestCommands = cloneCommands(commands);
                }
                Log.log(turn, "state processed, score: " + score + ", bestScore: " + bestScore);
            } else {
                Log.log(turn, "state is bad");
            }
        }

        Log.log(turn, "Best score: " + bestScore);
        return bestCommands;
    }

    private static List<Command> randomSelectAction(PlanetWars pw) {
        long startTime = System.currentTimeMillis();



        List<Command> bestCommands = new LinkedList<Command>();
        int bestScore = 0;

        List<Command> commands = new LinkedList<Command>();
        Log.log(turn, "Going to prepare initial state, myPlanets.size=" + pw.myPlanets().size() + ", nonMyPlanets.size=" + pw.notMyPlanets().size());
        for (Planet myPlanet : pw.myPlanets()) {
            for (Planet notMyPlanet : pw.notMyPlanets()) {
                commands.add(new Command(myPlanet, notMyPlanet.getPlanetId(), 0));
            }
        }
        PlanetWarsState state = new PlanetWarsState(pw, commands);
        Log.log(turn, "Initial state prepared");
        if (!state.isBadState()) {
            bestScore = score(state);
            Log.log(turn, "Initial state processed, score: " + bestScore);
            bestCommands = cloneCommands(commands);
        } else {
            Log.log(turn, "Initial state is bad");
        }

        int atts = 0;
        while (!timeOut (atts, startTime)) {
            ++atts;
            for (Command command : commands) {
                int numShips;
                do {
                    do {
                        numShips = command.getNumShips() + random.nextInt(command.getSourcePlanet().getNumShips()) - command.getSourcePlanet().getNumShips()/2;
                    } while (numShips < 0 || numShips > command.getSourcePlanet().getNumShips());
                    state = new PlanetWarsState(pw, commands);
                } while (!state.isBadState() && !timeOut (atts, startTime));

                command.setNumShips(numShips);
            }

            if (!state.isBadState()) {
                int score = score(state);
                if (bestScore < score) {
                    bestScore = score;
                    bestCommands = cloneCommands(commands);
                }
                Log.log(turn, "state processed, score: " + score + ", bestScore: " + bestScore);
            } else {
                Log.log(turn, "state is bad");
            }
        }

        Log.log(turn, "Best score: " + bestScore);
        return bestCommands;
    }

    private static boolean timeOut(int atts, long startTime) {
        long timePassed = System.currentTimeMillis() - startTime;
        Log.log(turn, "timePassed: " + timePassed);
        return timePassed > 300;
    }

    private static List<Command> cloneCommands(List<Command> commands) {
        LinkedList<Command> result = new LinkedList<Command>();
        for (Command c : commands) {
            if (c.getNumShips() == 0)
                continue;
            Log.log(turn, "clone command: " + c.getSourcePlanet().getPlanetId() + ", " + c.getDestinationPlanetId() + ", " + c.getNumShips());
            try {
                result.add((Command) c.clone());
            } catch (CloneNotSupportedException e) {
                //do nothing
            }
        }
        return result;
    }

    private static int score(PlanetWarsState pw) {
        int score = 0;
        for (Planet p : pw.myPlanets()) {
            int ships = p.getNumShips();
            score += ships;
            Log.log(turn, "Planet " + p.getPlanetId() + " result in score: " + score);
        }
        for (Fleet f : pw.myFleets()) {
            Planet p = pw.getPlanet(f.getDestinationPlanet());
            if (p.getOwner() != PlanetWarsState.ME) {
                int destinationShips = p.getNumShips();
                int diff;
                if (p.getOwner() == PlanetWarsState.NEUTRAL)
                    diff = f.getNumShips() - destinationShips;
                else
                    diff = f.getNumShips() - f.getTurnsRemaining()*p.getGrowthRate() - destinationShips;
                if (diff > 0) {
                    score += 4*p.getGrowthRate();
                    score += f.getNumShips();
                } else {
                    score -= f.getNumShips();
                }
                Log.log(turn, "Fleet from my planet " + f.getSourcePlanet() + " to others' planet " + p.getPlanetId() + " of size " + f.getNumShips() + ", score: " + score);
            }
        }
        return score;
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
                        line += (char)c;
                        break;
                }
            }
        } catch (Exception e) {
            // Owned.
        }
    }
}

