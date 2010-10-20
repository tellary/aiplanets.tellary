import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

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

    @Test
    public void testCollections() {
        HashSet<SquareMatrix> set = new HashSet<SquareMatrix>();

        SquareMatrix sm1 = new SquareMatrix(2);
        SquareMatrix sm2 = new SquareMatrix(2);
        sm2.set(0, 1, 1);
        sm1.add(sm2);

        set.add(sm1);
        Assert.assertFalse(set.add(sm2));

        Assert.assertEquals("Matrixes are equal, set should have 1 element", 1, set.size());

        sm2.set(0, 0, 8);
        Assert.assertTrue(set.add(sm2));

        Assert.assertEquals(2, set.size());

        //Now adding same element into set
        Assert.assertFalse(set.add(sm2));

        //Checking equals on same object
        Assert.assertTrue(sm2.equals(sm2));

        //Checking equals on different type
        //noinspection EqualsBetweenInconvertibleTypes
        Assert.assertFalse(sm2.equals(1));

        //Checking not equals on different capacity
        SquareMatrix sm3 = new SquareMatrix(3);
        sm3.set(0, 2, 300);
        Assert.assertTrue(set.add(sm3));
        Assert.assertEquals(3, set.size());

        Assert.assertFalse(sm3.equals(sm2));

        //Check size getter
        Assert.assertEquals(3, sm3.size());

        //Check not set value returns 0
        Assert.assertEquals(0, sm3.get(0, 1));
    }
}
