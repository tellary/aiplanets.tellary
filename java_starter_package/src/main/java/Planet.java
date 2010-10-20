public class Planet implements Cloneable {
    // Initializes a planet.
    public Planet(int planetID,
                  int owner,
                  int numShips,
                  int growthRate,
                  double x,
                  double y) {
        this.planetID = planetID;
        this.owner = owner;
        this.numShips = numShips;
        this.growthRate = growthRate;
        this.x = x;
        this.y = y;
    }

    // Accessors and simple modification functions. These should be mostly
    // self-explanatory.
    public int getPlanetId() {
        return planetID;
    }

    public int getOwner() {
        return owner;
    }

    public int getNumShips() {
        return numShips;
    }

    public int getGrowthRate() {
        return growthRate;
    }

    public double X() {
        return x;
    }

    public double Y() {
        return y;
    }

    public void Owner(int newOwner) {
        this.owner = newOwner;
    }

    public void NumShips(int newNumShips) {
        this.numShips = newNumShips;
    }

    public void addShips(int amount) {
        numShips += amount;
    }

    public void removeShips(int amount) {
        numShips -= amount;
    }

    private int planetID;
    private int owner;
    private int numShips;
    private int growthRate;
    private double x, y;

    private Planet (Planet _p) {
        planetID = _p.planetID;
        owner = _p.owner;
        numShips = _p.numShips;
        growthRate = _p.growthRate;
        x = _p.x;
        y = _p.y;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
