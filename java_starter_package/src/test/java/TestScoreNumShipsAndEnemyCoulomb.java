import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Silvestrov Ilya
 * Date: Nov 26, 2010
 * Time: 2:48:10 AM
 */
public class TestScoreNumShipsAndEnemyCoulomb {
    @Test
    public void testTakeOverIsProfitable() {
        int[][] distances = new int[][] {
                new int[] {0, 3, 7},
                new int[] {3, 0, 7},
                new int[] {7, 7, 0}
        };
        int[] planets = new int [] {
                200, 110, 200
        };
        int[] owners = new int[] {
                1, 2, 2
        };

        Assert.assertEquals(-110, MyBot.scoreNumShips(planets, owners));
        Assert.assertEquals(
                (long)Math.sqrt(200*110)/3 + (long)Math.sqrt(200*200)/7, 
                MyBot.scoreNumEnemyCoulomb(planets, owners, distances));
        System.out.println(MyBot.scoreNumEnemyCoulomb(planets, owners, distances));
        long scoreStay = MyBot.scoreNumShipsAndEnemyCoulomb(planets, owners, distances);
        planets = new int [] {
                165, 125, 225
        };
        owners = new int[] {
                1, 1, 2
        };
        long scoreTakeOver = MyBot.scoreNumShipsAndEnemyCoulomb(planets, owners, distances);

        Assert.assertTrue(scoreTakeOver > scoreStay);
        System.out.println(scoreStay);
        System.out.println(scoreTakeOver);
    }
}
