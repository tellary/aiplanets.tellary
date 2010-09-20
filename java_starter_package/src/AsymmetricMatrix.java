/**
 * Created by Silvestrov Ilya
 * Date: Sep 19, 2010
 * Time: 11:16:27 PM
 */
public class AsymmetricMatrix {
    private int[][] data;

    public AsymmetricMatrix(int size) {
        data = new int[size][];
    }

    public void set(int i, int j, int val) {
        if (j > i) {
            int t = i;
            i = j;
            j = t;
            val = -val;
        }
        int[] row = data[i];
        if (row == null) {
            row = new int[i];
            data[i] = row;
        }
        row[j] = val;
    }

    public int get(int i, int j) {
        if (i == j)
            return 0;
        boolean invert = false;
        if (j > i) {
            int t = i;
            i = j;
            j = t;
            invert = true;
        }
        
        int[] row = data[i];
        if (row == null)
            return 0;
        return invert? -row[j]:row[j];
    }

    public int size() {
        return data.length;
    }
}
