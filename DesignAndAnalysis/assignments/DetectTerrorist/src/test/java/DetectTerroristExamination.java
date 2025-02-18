import edu.yu.da.DetectTerrorist;
import org.junit.*;

public class DetectTerroristExamination {

    //public int[] passengers;

    private int[] initPassengers(int size, int terroristIndex){
        int[] passengers = new int[size];

        for (int i = 0; i < size; i++){
            passengers[i] = 7;
        }
        passengers[terroristIndex] = 5;
        return passengers;
    }

    @Test
    public void simpleDetect(){
        int[] passengers = initPassengers(300000, 3);
        DetectTerrorist dt = new DetectTerrorist(passengers);
        int result = dt.getTerrorist();
        System.out.println("Expected: " + 3 + ", found: " + result);
        assert result == 3;
    }

    @Test
    public void detectAtEnds(){
//        initPassengers(3000, 0);
//        DetectTerrorist dt = new DetectTerrorist(passengers);
//        int result = dt.getTerrorist();
//        System.out.println("Expected: " + 0 + ", found: " + result);
//        assert result == 0;

        int[] passengers = initPassengers(3000, 2999);
        DetectTerrorist dt = new DetectTerrorist(passengers);
        int result = dt.getTerrorist();
        System.out.println("Expected: " + 2999 + ", found: " + result);
        assert result == 2999;

    }

    @Test
    public void inTheMiddle(){
        int[] passengers = initPassengers(30000, (30000 / 2)+1);
        DetectTerrorist dt = new DetectTerrorist(passengers);
        int result = dt.getTerrorist();
        System.out.println("Expected: " + 15001 + ", found: " + result);
        assert result == 15001;
    }

    private long timeTrial(DetectTerrorist dt, int[] passengers){
        long start = System.nanoTime();
        dt = new DetectTerrorist(passengers);
        dt.getTerrorist();
        long end = System.nanoTime();
        return end-start;
    }

    @Test
    public void doublingRatio(){
        int n = 25000;
        double counter = 0;
        double total = 0;
        DetectTerrorist dt = null;
        long prev = timeTrial(dt, initPassengers(n, 3));
        while(counter < 15){
            counter++;
            n=n*2;
            long time = timeTrial(dt, initPassengers(n, 3));
            total += time/prev;
            prev = time;
        }

        System.out.println("Doubling ratio: " + total/counter);

    }



}
