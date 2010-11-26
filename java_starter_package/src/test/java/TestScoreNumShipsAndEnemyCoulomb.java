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
