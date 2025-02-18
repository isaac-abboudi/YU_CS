import edu.yu.introtoalgs.BigOIt;
import edu.yu.introtoalgs.BigOMeasurable;
import org.junit.Test;

public class BigOItTest {
    BigOIt bigO = new BigOIt();

    String n = "N";
    String nSquared = "NSquared";
    String nCubed = "NCubed";

    @Test
    public void simpleN(){
        double results = bigO.doublingRatio(n, 12000);
        System.out.println();
        System.out.println("ratio: " + results);
    }

    @Test
    public void iterativeN(){
        double[] data = new double[10];
        for (int i = 0; i < data.length; i++){
            try {
                data[i] = bigO.doublingRatio(n, 10000);
            } catch (OutOfMemoryError e){
                data[i] = -1;
            }
        }
        for (int i = 0; i < data.length; i++){
            System.out.println("iter " + i + ", doubling ratio: " + data[i]);
        }
    }

    @Test
    public void simpleNSquared(){
        double results = bigO.doublingRatio(nSquared, 12000);
        System.out.println();
        System.out.println("ratio: " + results);
    }

    @Test
    public void iterativeNSquared(){ // change to print out instantly, dont load array
        for (int i = 0; i < 10; i++){
            try {
                System.out.println("iter " + i + ", doubling ratio: " + bigO.doublingRatio(nSquared, 9000));
            } catch (OutOfMemoryError e){
                System.out.println("iter " + i + ", doubling ratio: " + -1);
            }
        }
    }


    @Test
    public void simpleNCubed(){
        double results = bigO.doublingRatio(nCubed, 12000);
        System.out.println();
        System.out.println("ratio: " + results);
    }

    @Test
    public void iterativeNCubed(){
        for (int i = 0; i < 10; i++){
            try {
                System.out.println("iter " + i + ", doubling ratio: " + bigO.doublingRatio(nCubed, 9000));
            } catch (OutOfMemoryError e){
                System.out.println("iter " + i + ", doubling ratio: " + -1);
            }
        }
    }

}
