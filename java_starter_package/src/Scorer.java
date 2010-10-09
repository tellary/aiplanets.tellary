import java.util.List;

/**
 * Created by Silvestrov Ilya
 * Date: Oct 9, 2010
 * Time: 12:00:51 AM
 */
public interface Scorer {
    int score(PlanetWarsState state, Plan plan);
}
