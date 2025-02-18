import edu.yu.introtoalgs.HydratedHakofos;
import org.junit.Test;

public class HHTests {

    @Test
    public void noFlags(){
        int[] waterAvailable = { 1, 3, 3, 3, 3};
        int[] waterRequired =  { 1, 3, 3, 3, 3};
        HydratedHakofos hh = new HydratedHakofos();
        int result = hh.doIt(waterAvailable, waterRequired);
        System.out.println(result);
    }

    @Test
    public void zeroNotValid(){
        int[] waterAvailable = { 1, 3, 3, 3, 6};
        int[] waterRequired = { 2, 3, 3, 3, 3};
        HydratedHakofos hh = new HydratedHakofos();
        int result = hh.doIt(waterAvailable, waterRequired);
        assert result == 2;
        System.out.println(result);
    }

    @Test
    public void middleTableValid(){
        int[] waterAvailable = { 3, 3, 5, 3, 3};
        int[] waterRequired = { 3, 5, 3, 3, 3};
        HydratedHakofos hh = new HydratedHakofos();
        int result = hh.doIt(waterAvailable, waterRequired);
        //assert result == 3;
        //System.out.println(result);
    }
    @Test
    public void middleTableValidDouble(){
        int[] waterAvailable = { 3, 3, 5, 3, 3, 3, 3, 3, 3, 3};
        int[] waterRequired = { 3, 5, 3, 3, 3, 3, 3, 3, 3, 3};
        HydratedHakofos hh = new HydratedHakofos();
        int result = hh.doIt(waterAvailable, waterRequired);
    }
    @Test
    public void middleTableValidQuadruple(){
        int[] waterAvailable = { 3, 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        int[] waterRequired = { 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        HydratedHakofos hh = new HydratedHakofos();
        int result = hh.doIt(waterAvailable, waterRequired);
    }
    @Test
    public void middleTableValid8x(){
        int[] waterAvailable = { 3, 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        int[] waterRequired = { 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        HydratedHakofos hh = new HydratedHakofos();
        int result = hh.doIt(waterAvailable, waterRequired);
    }
    @Test
    public void middleTableValid16x(){
        int[] waterAvailable = { 3, 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        int[] waterRequired = { 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        HydratedHakofos hh = new HydratedHakofos();
        int result = hh.doIt(waterAvailable, waterRequired);
    }
    @Test
    public void middleTableValid32x(){
        int[] waterAvailable = { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
         int[] waterRequired = { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        HydratedHakofos hh = new HydratedHakofos();
        int result = hh.doIt(waterAvailable, waterRequired);
        //System.out.println(result);
    }

    public double timeTrial(){
        //warm up
//        middleTableValid32x();

        double drAverage = 0;

        long start = System.nanoTime();
        middleTableValid();
        long end = System.nanoTime();
        double prev = end - start;

        start = System.nanoTime();
        middleTableValidDouble();
        end = System.nanoTime();
        double current = end - start;
        double dr = current/prev;
        //System.out.println("Doubling Ratio: " + dr);
        drAverage += dr;

        prev = current;
        start = System.nanoTime();
        middleTableValidQuadruple();
        end = System.nanoTime();
        current = end - start;
        dr = current/prev;
        //System.out.println("Doubling Ratio: " + dr);
        drAverage += dr;

        prev = current;
        start = System.nanoTime();
        middleTableValid8x();
        end = System.nanoTime();
        current = end - start;
        dr = current/prev;
        //System.out.println("Doubling Ratio: " + dr);
        drAverage += dr;

        prev = current;
        start = System.nanoTime();
        middleTableValid16x();
        end = System.nanoTime();
        current = end - start;
        dr = current/prev;
        //System.out.println("Doubling Ratio: " + dr);
        drAverage += dr;

        prev = current;
        start = System.nanoTime();
        middleTableValid32x();
        end = System.nanoTime();
        current = end - start;
        dr = current/prev;
        //System.out.println("Doubling Ratio: " + dr);
        drAverage += dr;
        drAverage = drAverage/5;
        //System.out.println("Average DR: " + drAverage);
        return drAverage;
    }

    @Test
    public void timeTrialLoop(){
        //warm up
        middleTableValid32x();

        double average = 0;
        for (int i = 0; i < 10; i++){
            double timeTrial = timeTrial();
            average += timeTrial;
            System.out.println("Doubling Ratio: " + timeTrial);
        }
        System.out.println();
        System.out.println("Total Average: " + average/10);
    }





}
