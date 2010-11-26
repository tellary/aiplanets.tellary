import java.io.BufferedReader;
import java.io.InputStreamReader;

@SuppressWarnings({"PointlessBooleanExpression", "ConstantConditions"})
public class DoNothingBot {
    public static void main(String[] args) {
        PlanetParser planetParser = new FirstTurnPlanetParser();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                PlanetParser.Result parseResult = planetParser.parsePlanets(br);
                if (parseResult.finished)
                    return;
                planetParser = parseResult.parser;

                //Do nothing
                //And finish turn
                PlanetWars.FinishTurn();
            }
        } catch (Exception e) {
            Log.error("Unable to read from System.in");
            Log.error(e);
        }
    }
}

