import edu.yu.da.FindMinyan;
import org.junit.Test;

import static org.junit.Assert.*;

public class FindMinyanTest {

    @Test
    public void findMinyanSimple(){
        FindMinyan fm = new FindMinyan(7);

        fm.addHighway(1, 6, 3);
        fm.addHighway(1, 2, 2);
        fm.addHighway(1, 1, 4);
        fm.addHighway(1, 3, 5);
        fm.addHighway(1, 6, 3);
        fm.addHighway(1, 6, 3);
        fm.addHighway(2, 5, 3);
        fm.addHighway(4, 1, 6);
        fm.addHighway(5, 6, 7);
        fm.addHighway(6, 1, 7);
        fm.addHighway(3, 3, 7);

        fm.hasMinyan(5);
        fm.hasMinyan(3);

        fm.solveIt();

        int minDistance = fm.shortestDuration();
        int totalPaths = fm.numberOfShortestTrips();

        assertEquals("minDistance is wrong", 9, minDistance);
        assert totalPaths == 2;
    }
}
