package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import edu.yu.cs.com1320.project.stage3.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {

    private HashTableImpl hashTable;
    private StackImpl stack;
    private TrieImpl trie;

    public DocumentStoreImpl(){
        this.hashTable = new HashTableImpl();
        this.stack = new StackImpl();
        this.trie = new TrieImpl();
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
            doc = new DocumentImpl(uri, txt);
            return putAsTxt(uri, doc);
        } else{
            byte[] bytes = convertStreamToBytes(input);
            doc = new DocumentImpl(uri, bytes);
            return putAsBinary(uri, doc);
        }
    }

    private int putAsTxt(URI uri, DocumentImpl doc){
        Object oldDoc = getDocument(uri);
        Object val = hashTable.put(uri, doc);
        // put it into the trie
        if (oldDoc != null){ // if there was another doc, delete its values
            deleteFromTrie((DocumentImpl)oldDoc);
        }
        addToTrie(doc); // add new values to trie
        Function<URI,Boolean> undo = (URI uri1) -> {hashTable.put(uri1,null);deleteFromTrie(doc);return true;};
        if (val != null){
            undo = (URI uri1) -> {hashTable.put(uri1,oldDoc); deleteFromTrie(doc); return true; }; // does this work?
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
            trie.put(word, doc);
        }
    }

    private void deleteFromTrie(DocumentImpl doc){
        for (String word : doc.getWords()){
            trie.delete(word, doc);
        }
    }

    private int putAsBinary(URI uri, DocumentImpl doc){ // does this get added to trie?
        Object oldDoc = getDocument(uri);
        Object val = hashTable.put(uri, doc);
        //if val says there was a document in it before then it was a rewrite
        Function<URI,Boolean> undo = (URI uri1) -> {hashTable.put(uri1,null);return true;};
        if (val != null){
            undo = (URI uri1) -> {hashTable.put(uri1,oldDoc);return true;};
        }
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
        return (Document)hashTable.get(uri);
    }

    @Override
    public boolean deleteDocument(URI uri) {
        DocumentImpl oldDoc = (DocumentImpl) hashTable.get(uri);
        if (getDocument(uri) == null){
            Function<URI,Boolean> undo = (URI uri1) -> {hashTable.put(null,null);return true;};
            GenericCommand c = new GenericCommand(uri, undo);
            stack.push(c);
            return false;
        } else {
            hashTable.put(uri, null);
            deleteFromTrie(oldDoc); // deleteFromTrie
            Function<URI,Boolean> undo = (URI uri1) -> {hashTable.put(uri1,oldDoc); addToTrie(oldDoc);return true;};
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
        while(!cFound){ // make sense?
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
        List<Document> docs;
        //docs = trie.getAllWithPrefixSorted(keywordPrefix,(a, b) -> Integer.compare(((DocumentImpl)a).wordCount(keywordPrefix),
                //((DocumentImpl)b).wordCount(keywordPrefix)));
        //docs = trie.getAllSorted(keyword, Comparator.comparingInt(a -> ((DocumentImpl) a).wordCount(keyword)));
        docs = trie.getAllSorted(keyword, (doc1, doc2) -> {
            if (((DocumentImpl)doc1).wordCount(keyword) < ((DocumentImpl)doc2).wordCount(keyword)) {
                return 1;
            } else if (((DocumentImpl)doc1).wordCount(keyword) > ((DocumentImpl)doc2).wordCount(keyword)) {
                return -1;
            }
            return 0;
        });
        return docs;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        List<Document> docs;
        docs = trie.getAllWithPrefixSorted(keywordPrefix, (doc1, doc2) -> {
            if (((DocumentImpl)doc1).wordCount(keywordPrefix) < ((DocumentImpl)doc2).wordCount(keywordPrefix)) {
                return 1;
            } else if (((DocumentImpl)doc1).wordCount(keywordPrefix) > ((DocumentImpl)doc2).wordCount(keywordPrefix)) {
                return -1;
            }
            return 0;
        });
        return docs;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<DocumentImpl> deleted;
        Set<URI> deletedURIs = new HashSet();
        deleted = trie.deleteAll(keyword);
        for (Document d : deleted){
            deletedURIs.add(d.getKey());
        }
        deleteFromHash(deletedURIs);
        CommandSet c = createCommandSet(deleted);
        stack.push(c);
        return deletedURIs;
    }

    private void deleteFromHash(Set<URI> deletedURIs){
        for (URI uri : deletedURIs){
            hashTable.put(uri, null);
        }
    }

    private CommandSet<URI> createCommandSet(Set<DocumentImpl> docs){
        CommandSet<URI> commandSet = new CommandSet<URI>();
        for (DocumentImpl d : docs) {
            Function<URI, Boolean> undo = (URI uri1) -> {
                hashTable.put(uri1,d);
                addToTrie(d);
                return true;
            };
            GenericCommand c = new GenericCommand(d.getKey(), undo);
            commandSet.addCommand(c);
        }
        return commandSet;
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<DocumentImpl> deleted;
        Set<URI> deletedURIs = new HashSet();
        deleted = trie.deleteAllWithPrefix(keywordPrefix);
        for (Document d : deleted){
            deletedURIs.add(d.getKey());
        }
        CommandSet c = createCommandSet(deleted);
        stack.push(c);
        deleteFromHash(deletedURIs);
        return deletedURIs;
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
