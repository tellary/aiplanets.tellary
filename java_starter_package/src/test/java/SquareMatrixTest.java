import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 20, 2010
 * Time: 12:02:23 AM
 */
public class SquareMatrixTest {
    @Test
    public void testRW() {
        SquareMatrix sm = new SquareMatrix(2);
        Assert.assertTrue(sm.isEmpty());
        sm.set(0,1,8);
        Assert.assertEquals(8, sm.get(0,1));
        Assert.assertFalse(sm.isEmpty());
    }

    @Test
    public void testZeroAddsOne() {
        SquareMatrix sm1 = new SquareMatrix(2);
        sm1.set(0, 1, 0);
        SquareMatrix sm2 = new SquareMatrix(2);
        sm2.set(0, 1, 1);
        sm1.add(sm2);

        Assert.assertEquals(1, sm1.get(0, 1));
    }

    @Test
    public void testNullAddsOne() {
        SquareMatrix sm1 = new SquareMatrix(2);
        SquareMatrix sm2 = new SquareMatrix(2);
        sm2.set(0, 1, 1);
        sm1.add(sm2);

        Assert.assertEquals(1, sm1.get(0, 1));
    }
}
