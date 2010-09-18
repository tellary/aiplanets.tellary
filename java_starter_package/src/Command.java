/**
 * Created by Silvestrov Ilya
 * Date: Sep 11, 2010
 * Time: 7:45:50 PM
 */
public class Command implements Cloneable {
    private Planet sourcePlanet;
    private int destinationPlanetId;
    private int numShips;


    public Command(Planet sourcePlanet, int destinationPlanetId, int numShips) {
        this.sourcePlanet = sourcePlanet;
        this.destinationPlanetId = destinationPlanetId;
        this.numShips = numShips;
    }

    public Planet getSourcePlanet() {
        return sourcePlanet;
    }

    public int getDestinationPlanetId() {
        return destinationPlanetId;
    }

    public int getNumShips() {
        return numShips;
    }

    public void setNumShips(int numShips) {
        this.numShips = numShips;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
