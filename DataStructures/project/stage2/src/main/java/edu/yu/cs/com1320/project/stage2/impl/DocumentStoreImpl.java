package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.DocumentStore;
import edu.yu.cs.com1320.project.stage2.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {

    private HashTableImpl hashTable;
    private StackImpl stack;

    public DocumentStoreImpl(){
        this.hashTable = new HashTableImpl();
        this.stack = new StackImpl();
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
        Function<URI,Boolean> undo = (URI uri1) -> {hashTable.put(uri1,null);return true;};
        if (val != null){// this is new jawn
            undo = (URI uri1) -> {hashTable.put(uri1,oldDoc);return true;};
        }
        Command c = new Command(uri, undo);
        stack.push(c);
        if (val == null || doc.equals(val)){
            return 0;
        } else {
            return val.hashCode(); //is this the right hashcode?
        }
    }

    private int putAsBinary(URI uri, DocumentImpl doc){
        Object oldDoc = getDocument(uri);
        Object val = hashTable.put(uri, doc);
        //if val says there was a document in it before then it was a rewrite
        Function<URI,Boolean> undo = (URI uri1) -> {hashTable.put(uri1,null);return true;};
        if (val != null){// this is new jawn
            undo = (URI uri1) -> {hashTable.put(uri1,oldDoc);return true;};
        }
        Command c = new Command(uri, undo);
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
            Command c = new Command(uri, undo);
            stack.push(c);
            return false;
        } else {
            hashTable.put(uri, null);
            Function<URI,Boolean> undo = (URI uri1) -> {hashTable.put(uri1,oldDoc);return true;};
            Command c = new Command(uri, undo);
            stack.push(c);
            return true;
        }
    }

    @Override
    public void undo() throws IllegalStateException {
        if (stack.size() < 1){
            throw new IllegalStateException();
        }
        Command c = (Command)stack.pop();
        c.undo();
    }

    @Override
    public void undo(URI uri) throws IllegalStateException {
        StackImpl tempStack = new StackImpl();
        Command c;
        boolean cFound = false;
        while(!cFound){ // make sense?
            if (stack.size() == 0){ //if there is no c for this URI. correct placement? correct logical test?
                resetStack(tempStack);
                throw new IllegalStateException();
            }
            c = (Command)stack.peek();
            if (uri != c.getUri()){//if not looking at the right command
                tempStack.push(stack.pop());
            } else{ //found it
                cFound = true;
            }
        }
        c = (Command)stack.pop();
        c.undo();
        resetStack(tempStack);
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
