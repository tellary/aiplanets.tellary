/**
 * Created by Silvestrov Ilya
 * Date: Nov 23, 2010
 * Time: 3:20:11 AM
 */
public class Force implements Comparable<Force> {
    private Integer owner;
    private Integer force = 0;

    public Force(int owner) {
        this.owner = owner;
    }

    @Override
    public int compareTo(Force o) {
        int res = -force.compareTo(o.force);
        if (res == 0) {
            return owner.compareTo(o.owner);
        }
        return res;
    }

    public void add(int force) {
        this.force += force;
    }

    public int getOwner() {
        return owner;
    }

    public int getForce() {
        return force;
    }
}
