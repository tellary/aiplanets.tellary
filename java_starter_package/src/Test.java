import java.util.Arrays;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 19, 2010
 * Time: 2:10:14 AM
 */
public class Test {
    public static void main(String[] args) {
        int[][] arr = new int[2][];

        arr[0] = new int[2];
        arr[1] = new int[1];

        arr[0][0] = 1;
        arr[0][1] = 2;
        arr[1][0] = 3;
        arr[1][1] = 3;

        for (int[] a : arr) {
            System.out.println(Arrays.toString(a));
        }

    }
}
