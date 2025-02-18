import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import org.junit.Test;

public class TestClass {

    @Test
    public void hashTableTest(){
        HashTableImpl hashT = new HashTableImpl();
        Integer x = 5;
        Integer y = 25;
        Integer a = 2;
        Integer b = 4;
        hashT.put(x, y);
        hashT.put(a, b);
        Object getValofx = hashT.get(x);
        Object getValofa = hashT.get(a);
        System.out.println("Val of x: " + getValofx);
        System.out.println("Val of a: " + getValofa);
    }

}
