package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.*;


import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {

    //private HashTableImpl hashTable;
    private BTreeImpl bTree; // changed from private -- !!!
    private StackImpl stack;
    private TrieImpl trie;
    private MinHeapImpl heap;
    private int maxDocCount = Integer.MAX_VALUE;
    private int maxByteCount = Integer.MAX_VALUE;
    private int heapDocCount;
    private int heapByteCount;
    private PersistenceManager pm;
    private HashSet<URI> memory = new HashSet<>();


    public DocumentStoreImpl(){
        this.bTree = new BTreeImpl();
        this.stack = new StackImpl();
        this.trie = new TrieImpl();
        this.heap = new MinHeapImpl(bTree);
        this.pm = new DocumentPersistenceManager(null);
        this.bTree.setPersistenceManager(pm);
    }

    public DocumentStoreImpl(File baseDir){
        this.bTree = new BTreeImpl();
        this.stack = new StackImpl();
        this.trie = new TrieImpl();
        this.heap = new MinHeapImpl(bTree);
        this.pm = new DocumentPersistenceManager(baseDir);
        this.bTree.setPersistenceManager(pm);
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (uri == null || format == null){
            throw new IllegalArgumentException();
        }
        DocumentImpl doc;
        if (input == null){
            doc = (DocumentImpl) getDocument(uri);
            if (doc == null){ // doc never existed, delete is a no op
                deleteDocument(uri);
                return 0;
            }
            int hash = doc.hashCode();
            if (deleteDocument(uri)){
                return hash;
            } else {
                return 0;
            }
        }
        if (format == DocumentFormat.TXT){
            String txt = convertStreamToString(input);
            doc = new DocumentImpl(uri, txt, null);
            return putAsTxt(uri, doc);
        } else{
            byte[] bytes = convertStreamToBytes(input);
            doc = new DocumentImpl(uri, bytes);
            return putAsBinary(uri, doc);
        }
    }

    private int putAsTxt(URI uri, DocumentImpl doc){
        Object oldDoc = getDocument(uri);
        Object val = bTree.put(uri, doc);
        // put it into the trie
        if (oldDoc != null){ // if there was another doc, delete its values
            deleteFromTrie((DocumentImpl)oldDoc);
        }
        addToTrie(doc); // add new values to trie
        addToHeap(doc); // add to heap
        Function<URI,Boolean> undo = (URI uri1) -> {bTree.put(uri1,null);deleteFromTrie(doc);deleteFromHeap(doc);return true;}; //heap?
        if (val != null){ //logic to rewrite
            undo = (URI uri1) -> {bTree.put(uri1,oldDoc); deleteFromTrie(doc);deleteFromHeap(doc);return true; }; // does this work? // heap?
        }
        GenericCommand c = new GenericCommand(uri, undo);
        stack.push(c);
        if (val == null || doc.equals(val)){
            return 0;
        } else {
            return val.hashCode();
        }
    }

    private void addToTrie(DocumentImpl doc){
        for (String word : doc.getWords()){
            trie.put(word, doc.getKey());
        }
    }

    private void addToHeap(DocumentImpl doc){
        doc.setLastUseTime(System.nanoTime());
        heap.insert(doc.getKey());
        heap.reHeapify(doc.getKey());
        updateHeapCounters(doc, true);
        checkHeapSize();
        memory.add(doc.getKey());
    }

    private void checkHeapSize() {
        while (heapByteCount > maxByteCount || heapDocCount > maxDocCount){
            //if theres too much in the heap, remove the oldest item
            URI removedURI = (URI) heap.remove();
            memory.remove(removedURI);
            DocumentImpl removed = (DocumentImpl) bTree.get(removedURI);
            updateHeapCounters(removed, false);
            try {
                bTree.moveToDisk(removedURI); //delete from tree
            } catch (Exception e){
            }
        }
    }

    private void updateHeapCounters(DocumentImpl doc, boolean inserting){
        if (doc.getDocumentTxt() != null){ // this is a TXT doc
            if (inserting){
                updateHeapCountersTXT(doc, true);
            } else {
                updateHeapCountersTXT(doc, false);
            }
        } else { // this is a BINARY doc
            if (inserting){
                updateHeapCountersBINARY(doc, true);
            } else {
                updateHeapCountersBINARY(doc, false);
            }
        }

    }
    private void updateHeapCountersTXT(DocumentImpl d, boolean inserting){
        if (inserting){
            heapDocCount++;
            String text = d.getDocumentTxt();
            byte[] bytes = text.getBytes();
            heapByteCount += bytes.length;
        } else {
            heapDocCount--;
            String text = d.getDocumentTxt();
            byte[] bytes = text.getBytes();
            heapByteCount -= bytes.length;
        }

    }
    private void updateHeapCountersBINARY(DocumentImpl d, boolean inserting){
        if (inserting){
            heapDocCount++;
            byte[] bytes = d.getDocumentBinaryData();
            heapByteCount += bytes.length;
        } else {
            heapDocCount--;
            byte[] bytes = d.getDocumentBinaryData();
            heapByteCount -= bytes.length;
        }

    }
    private void updateWithinHeap(DocumentImpl doc){
        doc.setLastUseTime(System.nanoTime());
        heap.reHeapify(doc.getKey());
    }

    private void deleteFromHeap(DocumentImpl doc){
        doc.setLastUseTime(-1);
        heap.reHeapify(doc.getKey());
        heap.remove();
        updateHeapCounters(doc, false);
        memory.remove(doc.getKey());
    }
    private void deleteFromHeapMultipleDocs(Set<DocumentImpl> docs){
        for (DocumentImpl d : docs){
            deleteFromHeap(d);
        }
    }

    private void deleteFromTrie(DocumentImpl doc){
        for (String word : doc.getWords()){
            trie.delete(word, doc.getKey());
        }
    }

    private int putAsBinary(URI uri, DocumentImpl doc){
        Object oldDoc = getDocument(uri);
        Object val = bTree.put(uri, doc);
        //if val says there was a document in it before then it was a rewrite
        Function<URI,Boolean> undo = (URI uri1) -> {bTree.put(uri1,null);return true;}; // heap?
        if (val != null){ // logic to rewrite
            undo = (URI uri1) -> {bTree.put(uri1,oldDoc);return true;}; // heap?
        }
        addToHeap(doc);
        GenericCommand c = new GenericCommand(uri, undo);
        stack.push(c);
        if (val == null || doc.equals(val)){
            return 0;
        } else {
            return val.hashCode();
        }
    }

    @Override
    public Document getDocument(URI uri) {
        boolean wasInMemory = memory.contains(uri);
        Document docToReturn = (Document) bTree.get(uri);
        if (docToReturn != null){
            if (!wasInMemory){
                addToHeap((DocumentImpl) docToReturn);
                Function<URI,Boolean> undo = (URI uri1) -> {deleteFromHeap((DocumentImpl) docToReturn);return true;};
                GenericCommand c = new GenericCommand(uri, undo);
                stack.push(c);
            } else {
                updateWithinHeap((DocumentImpl) docToReturn);
            }
        }
        return (Document)bTree.get(uri);
    }

    @Override
    public boolean deleteDocument(URI uri) {
        DocumentImpl oldDoc = (DocumentImpl) bTree.get(uri);
        if (getDocument(uri) == null){ // if it never existed, no op on the undo
            Function<URI,Boolean> undo = (URI uri1) -> {bTree.put(null,null);return true;};
            GenericCommand c = new GenericCommand(uri, undo);
            stack.push(c);
            return false;
        } else {
            deleteFromTrie(oldDoc); // deleteFromTrie
            deleteFromHeap(oldDoc);// delete this specific doc from heap
            bTree.put(uri, null);
            Function<URI,Boolean> undo = (URI uri1) -> {bTree.put(uri1,oldDoc); addToTrie(oldDoc);return true;};
            GenericCommand c = new GenericCommand(uri, undo);
            stack.push(c);
            return true;
        }
    }

    @Override
    public void undo() throws IllegalStateException {
        if (stack.size() < 1){
            throw new IllegalStateException();
        }
        if (stack.peek() instanceof CommandSet){
            CommandSet c = (CommandSet)stack.pop();
            c.undoAll();
        } else {
            GenericCommand c = (GenericCommand)stack.pop();
            c.undo();
        }
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        StackImpl tempStack = new StackImpl();
        boolean cFound = false;
        while(!cFound){
            if (stack.size() == 0){ //if there is no c for this URI. correct placement? correct logical test?
                resetStack(tempStack);
                throw new IllegalStateException();
            }
            if (stack.peek() instanceof CommandSet){
                cFound = commandSetUndo(uri, tempStack);
            } else if (stack.peek() instanceof GenericCommand){
                cFound = genericCommandUndo(uri, tempStack);
            }
        }
        resetStack(tempStack);
    }

    private boolean commandSetUndo(URI uri, StackImpl tempStack){
        CommandSet commandSet = (CommandSet) stack.peek();
        if (commandSet.containsTarget(uri)){ // if we found the command
            commandSet.undo(uri);
            if (commandSet.isEmpty()){
                stack.pop();
            }
            return true;
        } else { // if we did not
            tempStack.push(stack.pop());
            return false;
        }
    }

    private boolean genericCommandUndo(URI uri, StackImpl tempStack){
        GenericCommand genericCommand = (GenericCommand)stack.peek();
        if (uri != genericCommand.getTarget()) {//target to URI?
            tempStack.push(stack.pop());
            return false;
        } else {
            genericCommand = (GenericCommand) stack.pop();
            genericCommand.undo();
            return true;
        }
    }

    @Override
    public List<Document> search(String keyword) {
        List<URI> docURIs;

        docURIs = trie.getAllSorted(keyword, (uri1, uri2) -> {
            if (((DocumentImpl)bTree.get((URI)uri1)).wordCount(keyword) < ((DocumentImpl)bTree.get((URI)uri2)).wordCount(keyword)) {
                return 1;
            } else if (((DocumentImpl)bTree.get((URI)uri1)).wordCount(keyword) > ((DocumentImpl)bTree.get((URI)uri2)).wordCount(keyword)) {
                return -1;
            }
            return 0;
        });
        List<Document> docs = new ArrayList<>();
        for (URI uri : docURIs){
            if (!memory.contains(uri)){
                addToHeap((DocumentImpl) bTree.get(uri));
            }
            docs.add((Document) bTree.get(uri));
        }
        updateHeapMultipleDocs(docs);
        return docs;
    }

    private void updateHeapMultipleDocs(List<Document> docs){
        for (Document d : docs){
            updateWithinHeap((DocumentImpl) d);
        }
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        List<URI> docURIs;
        docURIs = trie.getAllWithPrefixSorted(keywordPrefix, (uri1, uri2) -> {
            if (((DocumentImpl)bTree.get((URI)uri1)).wordCount(keywordPrefix) < ((DocumentImpl)bTree.get((URI)uri2)).wordCount(keywordPrefix)) {
                return 1;
            } else if (((DocumentImpl)bTree.get((URI)uri1)).wordCount(keywordPrefix) > ((DocumentImpl)bTree.get((URI)uri2)).wordCount(keywordPrefix)) {
                return -1;
            }
            return 0;
        });
        List<Document> docs = new ArrayList<>();
        for (URI uri : docURIs){
            if (!memory.contains(uri)){
                addToHeap((DocumentImpl) bTree.get(uri));
            }
            docs.add((Document) bTree.get(uri));
        }
        updateHeapMultipleDocs(docs);
        return docs;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<DocumentImpl> deleted = new HashSet();
        Set<URI> deletedURIs;
        deletedURIs = trie.deleteAll(keyword);
        for (URI uri : deletedURIs){
            deleted.add((DocumentImpl) bTree.get(uri));
        }
        deleteFromTree(deletedURIs);
        deleteFromHeapMultipleDocs(deleted);//delete all from heap
        CommandSet c = createCommandSet(deleted);
        stack.push(c);
        return deletedURIs;
    }

    private void deleteFromTree(Set<URI> deletedURIs){
        for (URI uri : deletedURIs){
            bTree.put(uri, null);
        }
    }

    private CommandSet<URI> createCommandSet(Set<DocumentImpl> docs){
        CommandSet<URI> commandSet = new CommandSet<URI>();
        for (DocumentImpl d : docs) {
            Function<URI, Boolean> undo = (URI uri1) -> {
                bTree.put(uri1,d);
                addToTrie(d);
                addToHeap(d);
                return true;
            };
            GenericCommand c = new GenericCommand(d.getKey(), undo);
            commandSet.addCommand(c);
        }
        return commandSet;
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<DocumentImpl> deleted = new HashSet<>();
        Set<URI> deletedURIs;
        deletedURIs = trie.deleteAllWithPrefix(keywordPrefix);
        for (URI uri : deletedURIs){
            deleted.add((DocumentImpl) bTree.get(uri));
        }
        CommandSet c = createCommandSet(deleted);
        stack.push(c);
        deleteFromTree(deletedURIs);
        deleteFromHeapMultipleDocs(deleted);
        return deletedURIs;
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        this.maxDocCount = limit;
        checkHeapSize();
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        this.maxByteCount = limit;
        checkHeapSize();
    }

    private void resetStack(StackImpl tempStack){
        while(tempStack.size()>0){ // refill stack
            stack.push(tempStack.pop());
        }
    }

    private String convertStreamToString(InputStream input) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(input);
        BufferedReader buffer = new BufferedReader(streamReader);
        StringBuffer stringBuffer = new StringBuffer();
        String str;
        while ((str = buffer.readLine()) != null){
            stringBuffer.append(str);
        }
        String txt = stringBuffer.toString();
        return txt;
    }
    private byte[] convertStreamToBytes(InputStream input) throws IOException {
        byte[] bytes = input.readAllBytes();
        return bytes;
    }
}