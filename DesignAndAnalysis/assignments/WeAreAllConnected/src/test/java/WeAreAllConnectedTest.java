import edu.yu.da.WeAreAllConnected;
import edu.yu.da.WeAreAllConnectedBase.*;
import org.junit.*;

import java.util.ArrayList;

public class WeAreAllConnectedTest {

    @Test
    public void simpleTest(){
        WeAreAllConnected w = new WeAreAllConnected();
        ArrayList<SegmentBase> current = new ArrayList<>();
        current.add(new SegmentBase(0,1,1));
        current.add(new SegmentBase(0,3,2));
        current.add(new SegmentBase(1,2,2));
        current.add(new SegmentBase(2,3,1));

        ArrayList<SegmentBase> poss = new ArrayList<>();
        poss.add(new SegmentBase(1,3,4));
        poss.add(new SegmentBase(1,3,3));
        poss.add(new SegmentBase(1,3, 1));

        SegmentBase result = w.findBest(4, current, poss);

        assert result.duration == 1;



    }
}
