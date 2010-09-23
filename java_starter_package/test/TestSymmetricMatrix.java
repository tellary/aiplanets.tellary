import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 20, 2010
 * Time: 12:02:23 AM
 */
public class TestSymmetricMatrix {
    @Test
    public void testRW() {
        SquareMatrix sm = new SquareMatrix(2);
        sm.set(0,1,8);
        Assert.assertEquals(8, sm.get(0,1));
    }

    @Test
    public void testSym() {
        SquareMatrix sm = new SquareMatrix(3);
        sm.set(2, 1, 21);
        Assert.assertEquals(-21, sm.get(1,2));
        sm.set(0, 1, 1);
        Assert.assertEquals(-1, sm.get(1,0));
        sm.set(2, 0, 2);
        Assert.assertEquals(-2, sm.get(0,2));
    }
}
