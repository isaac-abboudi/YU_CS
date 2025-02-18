import edu.yu.da.OverfullGranaries;
import org.junit.Test;

import java.util.Arrays;

public class OFGTC {


    @Test
    public void simpleBoi(){
        String[] overfullGranaries1 = { "Granary1", "Granary2" };
        String[] underfullGranaries1 = { "Granary3", "Granary4" };
        OverfullGranaries o = new OverfullGranaries(overfullGranaries1, underfullGranaries1);
        o.edgeExists("Granary1", "Granary3", 1000);
        o.edgeExists("Granary1", "Granary4", 2000);
        o.edgeExists("Granary2", "Granary3", 3000);
        o.edgeExists("Granary2", "Granary4", 4000);

        double minHours1 = o.solveIt();
        String[] expectedMinCut1 = { "Granary1", "Granary2" };
        System.out.println(minHours1);
        assert Arrays.asList(expectedMinCut1).equals(o.minCut());


    }
}
