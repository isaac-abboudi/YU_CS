import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;

public class TestsStage3 {

    @Test
    public void regularWords() throws URISyntaxException {

        URI uri = new URI("1");
        String text = "this is a test this";
        DocumentImpl doc = new DocumentImpl(uri, text);

        System.out.println(doc.wordCount("this"));
    }

    @Test
    public void funkyWords() throws URISyntaxException { // test for back-to-back non-alphanumeric

        URI uri = new URI("1");
        String text = "THIS is a Te$$t this";
        DocumentImpl doc = new DocumentImpl(uri, text);

        System.out.println(doc.wordCount("tet"));
        System.out.println(doc.wordCount("this"));

    }

    @Test
    public void simpleTrie(){
        TrieImpl trie = new TrieImpl();
        trie.put("one", 1);

    }

}
