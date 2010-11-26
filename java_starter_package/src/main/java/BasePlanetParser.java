import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 23, 2010
 * Time: 5:46:14 AM
 */
public abstract class BasePlanetParser implements PlanetParser {
    protected abstract Arrivals newArrivals();
    protected abstract PlanetParser.Result complete(Arrivals myArrivals, Arrivals enemyArrivals);
    protected abstract void processPlanet(int planetId, double x, double y, int owner, int numShips, int growthRate);

    public PlanetParser.Result parsePlanets(BufferedReader reader) throws IOException {
        Arrivals myArrivals = newArrivals();
        Arrivals enemyArrivals = newArrivals();
        int planetId = 0;

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return new PlanetParser.Result(null, null, true);
            }
            if ("go".equals(line)) {
                return complete(myArrivals, enemyArrivals);
            }
            processLine(planetId++, line, myArrivals, enemyArrivals);
        }
    }

    protected void processLine(int planetId, String line, Arrivals myArrivals, Arrivals enemyArrivals) {
        int commentBegin = line.indexOf('#');
        if (commentBegin >= 0) {
            line = line.substring(0, commentBegin);
        }
        if (line.trim().length() == 0) {
            return;
        }
        String[] tokens = line.split(" ");
        if (tokens.length == 0) {
            return;
        }
        if (tokens[0].equals("P")) {
            if (tokens.length != 6) {
                throw new RuntimeException("Bad planet string");
            }
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            int owner = Integer.parseInt(tokens[3]);
            int numShips = Integer.parseInt(tokens[4]);
            int growthRate = Integer.parseInt(tokens[5]);
            processPlanet(planetId, x, y, owner, numShips, growthRate);
        } else if (tokens[0].equals("F")) {
            if (tokens.length != 7) {
                throw new RuntimeException("Bad fleet string");
            }
            int owner = Integer.parseInt(tokens[1]);
            int numShips = Integer.parseInt(tokens[2]);
            int destination = Integer.parseInt(tokens[4]);
            int turnsRemaining = Integer.parseInt(tokens[6]);
            if (owner == PlanetWarsState.ME)
                myArrivals.add(turnsRemaining, destination, numShips);
            else if (owner == PlanetWarsState.ENEMY)
                enemyArrivals.add(turnsRemaining, destination, numShips);
            else
                throw new RuntimeException("owner should be 1 or 2");

        } else {
            throw new RuntimeException("Bad fleet string, unknown first token: " + tokens[0]);
        }
    }
}
