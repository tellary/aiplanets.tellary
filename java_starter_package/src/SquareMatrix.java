/**
 * Created by Silvestrov Ilya
 * Date: Sep 19, 2010
 * Time: 11:16:27 PM
 */
public class SquareMatrix {
    private int[][] data;

    public SquareMatrix(int size) {
        data = new int[size][size];
    }

    public void set(int i, int j, int val) {
        data[i][j] = val;
    }

    public int get(int i, int j) {
        if (i == j)
            return 0;
        return data[i][j];
    }

    public int size() {
        return data.length;
    }
}
