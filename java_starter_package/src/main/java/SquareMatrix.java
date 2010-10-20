import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Silvestrov Ilya
 * Date: Sep 19, 2010
 * Time: 11:16:27 PM
 */
public class SquareMatrix {
    private Map<Integer, Map<Integer, Integer>> data;
    private int capacity;


    public SquareMatrix(int capacity) {
        this.capacity = capacity;
        data = new HashMap<Integer, Map<Integer, Integer>>(capacity);
    }

    public void set(int i, int j, int val) {
        if (val == 0)
            return;
        Map<Integer, Integer> toRow = data.get(i);
        if (toRow == null) {
            toRow = new HashMap<Integer, Integer>(capacity);
            data.put(i, toRow);
        }
        toRow.put(j, val);
    }

    public int get(int i, int j) {
        Map<Integer, Integer> toRow = data.get(i);
        if (toRow == null)
            return 0;
        Integer value = toRow.get(j);
        if (value == null)
            return 0;
        else
            return value;
    }

    public int size() {
        return capacity;
    }

    public SquareMatrix add(SquareMatrix tr1) {
        for (int i = 0; i < capacity; ++i) {
            for (int j = 0; j < capacity; ++j) {
                Map<Integer, Integer> tr1ToRow = tr1.data.get(i);
                if (tr1ToRow == null)
                    continue;
                Integer tr1Value = tr1ToRow.get(j);
                if (tr1Value == null)
                    continue;

                int value = get(i, j);
                set(i, j, value + tr1Value);
            }
        }
        return this;
    }

    public boolean isEmpty() {
        return data.isEmpty() || capacity == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null ||
                getClass() != o.getClass()) 
            return false;

        SquareMatrix that = (SquareMatrix) o;

        //noinspection SimplifiableIfStatement
        if (capacity != that.capacity) return false;

        //data is always not null
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        //data is always not null
        int result = data.hashCode();
        result = 31 * result + capacity;
        return result;
    }
}
