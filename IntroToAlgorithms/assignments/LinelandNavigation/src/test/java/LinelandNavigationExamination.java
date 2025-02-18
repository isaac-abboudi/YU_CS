import edu.yu.introtoalgs.LinelandNavigation;
import org.junit.Test;

public class LinelandNavigationExamination {

    @Test
    public void simpleLineland(){
        LinelandNavigation ln = new LinelandNavigation(100, 10);
        int result = ln.solveIt();
        assert result == 10;
    }

    @Test
    public void withMines(){
        LinelandNavigation ln = new LinelandNavigation(100, 10);
        ln.addMinedLineSegment(18,20);
        ln.addMinedLineSegment(26,30);
        int result = ln.solveIt();
        System.out.println(result);
        assert result == 12;
    }

    @Test
    public void impossibleMineSegmentTooLong(){
        LinelandNavigation ln = new LinelandNavigation(100, 10);
        ln.addMinedLineSegment(30, 40);
        int result = ln.solveIt();
        assert result == 0;
    }

    @Test
    public void impossibleFirstJump(){
        LinelandNavigation ln = new LinelandNavigation(100, 10);
        ln.addMinedLineSegment(8, 10);
        int result = ln.solveIt();
        assert result == 0;
    }



}
