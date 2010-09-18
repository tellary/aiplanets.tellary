import java.util.*;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 11, 2010
 * Time: 8:48:52 PM
 */
public class PlanetWarsState {
    public static final int ME = 1;
    public static final int ENEMY = 2;
    public static final int NEUTRAL = 0;

    private Map<Integer, Planet> planets;
    private List<Fleet> fleets;

    private boolean badState = false;

    //Calculate PlanetWars state for next move after all commands will be applied
    public PlanetWarsState(PlanetWars pw, List<Command> commands) {
        planets = new HashMap<Integer, Planet>();
        for (Planet p : pw.Planets()) {
            try {
                Planet planet = (Planet) p.clone();
                if (planet.getOwner() != NEUTRAL) {
                    planet.addShips(planet.getGrowthRate());
                } 
                planets.put(p.getPlanetId(), (Planet) p.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        fleets = new LinkedList<Fleet>();
        for (Fleet f : pw.Fleets()) {
            Fleet fleet;
            try {
                fleet = (Fleet) f.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            if (f.getTurnsRemaining() == 1) {
                Planet planet = planets.get(f.getDestinationPlanet());
                if (planet.getOwner() == ME) {
                    planet.addShips(f.getNumShips());
                } else {
                    if (planet.getNumShips() >= f.getNumShips()) {
                        planet.removeShips(f.getNumShips());
                    } else {
                        planet.addShips(f.getNumShips() - planet.getNumShips());
                    }
                }
            } else {
                fleet.timeStep();
                fleets.add(fleet);
            }
        }
        for (Command command : commands) {
            if (command.getNumShips() == 0)
                continue;
            Planet source = planets.get(command.getSourcePlanet().getPlanetId());
            if (source.getNumShips() >= command.getNumShips())
                source.removeShips(command.getNumShips());
            else {
                badState = true;
                return;
            }
            Planet dest = planets.get(command.getDestinationPlanetId());
            int length = pw.Distance(source.getPlanetId(), dest.getPlanetId());
            fleets.add(new Fleet(ME, command.getNumShips(), command.getSourcePlanet().getPlanetId(), command.getDestinationPlanetId(), length, length));
        }
    }

    public boolean isBadState() {
        return badState;
    }

    // Return a list of all the fleets owned by the current player.
    public List<Fleet> myFleets() {
        List<Fleet> r = new ArrayList<Fleet>();
        for (Fleet f : fleets) {
            if (f.Owner() == ME) {
                r.add(f);
            }
        }
        return r;
    }

    // Return a list of all the planets owned by the current player. By
    // convention, the current player is always player number 1.
    public List<Planet> myPlanets() {
        List<Planet> r = new ArrayList<Planet>();
        for (Planet p : planets.values()) {
            if (p.getOwner() == ME) {
                r.add(p);
            }
        }
        return r;
    }

    // Returns the planet with the given planet_id. There are NumPlanets()
    // planets. They are numbered starting at 0.
    public Planet getPlanet(int planetID) {
        return planets.get(planetID);
    }
}
