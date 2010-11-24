import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 23, 2010
 * Time: 5:06:17 AM
 */
public interface PlanetParser {
    class Result {
        public PlanetParser parser;
        public PlanetWarsState state;
        public boolean finished;

        public Result(PlanetParser parser, PlanetWarsState state, boolean finished) {
            this.parser = parser;
            this.state = state;
            this.finished = finished;
        }
    }

    Result parsePlanets(BufferedReader reader) throws IOException;
}
