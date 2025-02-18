import edu.yu.introtoalgs.BTKeysAtSameLevel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BTreeTests {

    BTKeysAtSameLevel tree = new BTKeysAtSameLevel();

    @Test
    public void treeTest1(){
        String treeAsString = "1(2)(3(8)(9))";
        final List<List<Integer>> actual = new BTKeysAtSameLevel().compute(treeAsString);
        final List<List<Integer>> expected = new ArrayList<>();
        expected.add(new ArrayList<Integer>(List.of(1)));
        expected.add(new ArrayList<Integer>(List.of(2,3)));
        expected.add(new ArrayList<Integer>(List.of(8,9)));
        assertEquals(expected, actual);
    }

}
