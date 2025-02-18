import edu.yu.introtoalgs.BTKeysAtSameLevel2;
import org.junit.Test;

public class BTKASL2Test {


    @Test
    public void scaleByDepth(){
        BigOItTree bigOTree = new BigOItTree();
        double time;


        String depth2 = "1(2)(3)";
        String depth3 = "1(2(4)(5))(3(6)(7))"; //18
        String depth4 = "1(2(4(8)(9))(5(1)(2)))(3(6(3)(4))(7(5)(6)))"; //42
        String depth5 = "1(2(4(8(7)(8))(9(9)(1)))(5(1(2)(3))(2(4)(5))))(3(6(3(6)(7))(4(8)(9)))(7(5(1)(2))(6(3)(4))))";
        String depth6 = "1(2(4(8(7(5)(6))(8(7)(8)))(9(9(9)(1))(1(2)(3))))(5(1(2(4)(5))(3(6)(7)))(2(4(8)(9))(5(1)(2)))))" +
                "(3(6(3(6(3)(4))(7(5)(6)))(4(8(7)(8))(9(9)(1))))(7(5(1(2)(3))(2(4)(5)))(6(3(6)(7))(4(8)(9)))))";
        String depth7 = "1(2(4(8(7(5(4)(4))(6(4)(4)))(8(7(4)(4))(8(4)(4))))(9(9(9(4)(4))(1(4)(4)))(1(2(4)(4))" +
                "(3(4)(4)))))(5(1(2(4(4)(4))(5(4)(4)))(3(6(4)(4))(7(4)(4))))(2(4(8(4)(4))(9(4)(4)))(5(1(4)(4))" +
                "(2(4)(4))))))(3(6(3(6(3(4)(4))(4(4)(4)))(7(5(4)(4))(6(4)(4))))(4(8(7(4)(4))(8(4)(4)))(9(9(4)(4))" +
                "(1(4)(4)))))(7(5(1(2(4)(4))(3(4)(4)))(2(4(4)(4))(5(4)(4))))(6(3(6(4)(4))(7(4)(4)))(4(8(4)(4))(9(4)" +
                "(4))))))";
        System.out.println("   warmup: " + timeOfExecution(depth6));
        double trial1 = timeOfExecution(depth2);
        System.out.println("depth of 2: " + trial1);
        double trial2 = timeOfExecution(depth3);
        double dr1 = (trial2/trial1);
        System.out.println("depth of 3: " + trial2 + ", DR: " + dr1);
        double trial3 = timeOfExecution(depth4);
        double dr2 = (trial3/trial2);
        System.out.println("depth of 4: " + trial3 + ", DR: " + dr2);
        double trial4 = timeOfExecution(depth5);
        double dr3 = (trial4/trial3);
        System.out.println("depth of 5: " + trial4 + ", DR: " + dr3);
        double trial5 = timeOfExecution(depth6);
        double dr4 = (trial5/trial4);
        System.out.println("depth of 6: " + trial5 + ", DR: " + dr4);
        double trial6 = timeOfExecution(depth7);
        double dr5 = (trial6/trial5);
        System.out.println("depth of 7: " + trial6 + ", DR: " + dr5);

        System.out.println("Average DR: " + (dr1+dr2+dr3+dr4+dr5)/5);
    }

    public double timeOfExecution(String tree){
        BTKeysAtSameLevel2 btKey = new BTKeysAtSameLevel2();
        long start = System.nanoTime();
        btKey.compute(tree);
        long end = System.nanoTime();
        return (double)(end-start);
    }

}
