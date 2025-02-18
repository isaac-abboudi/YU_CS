import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StackTests {

    private StackImpl stack;

    @Test
    public void initStack(){
        this.stack = new StackImpl();
        stack.push("push1");
        stack.push("push2");
        stack.push("push3");
        stack.push("push4");
    }

    @Test
    public void testPop(){
        initStack();
        assertEquals("push4", stack.pop());
        assertEquals("push3", stack.pop());
        assertEquals("push2", stack.pop());
        assertEquals("push1", stack.pop());
    }

    @Test
    public void testPopEmptyStack(){
        testPop();
        assertEquals(null, stack.pop());
    }

    @Test
    public void testPeek(){
        initStack();
        assertEquals("push4", stack.peek()); //peek without pop
        assertEquals("push4", stack.pop()); // pop and hope peek didn't effect
        // pop the rest and peek
        assertEquals("push3", stack.peek());
        assertEquals("push3", stack.pop());
        assertEquals("push2", stack.peek());
        assertEquals("push2", stack.pop());
        assertEquals("push1", stack.peek());
        assertEquals("push1", stack.pop());
        assertNull(stack.peek()); // should be empty
    }
}
