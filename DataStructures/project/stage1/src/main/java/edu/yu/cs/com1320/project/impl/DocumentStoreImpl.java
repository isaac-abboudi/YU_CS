package edu.yu.cs.com1320.project.impl;


import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {

    HashTableImpl hashTable;

    public DocumentStoreImpl(){
        this.hashTable = new HashTableImpl();
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (uri == null || format == null){
            throw new IllegalArgumentException();
        }
        DocumentImpl doc;
        if (input == null){
            doc = (DocumentImpl) getDocument(uri);
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
            Object val = hashTable.put(uri, doc);
            if (doc.equals(val)){
                return 0;
            } else {
                return val.hashCode(); //is this the right hashcode?
            }
        } else{
            byte[] bytes = convertStreamToBytes(input);
            doc = new DocumentImpl(uri, bytes);
            Object val = hashTable.put(uri, doc);
            if (doc.equals(val)){
                return 0;
            } else {
                return val.hashCode();
            }
        }
    }

    @Override
    public Document getDocument(URI uri) {
        return (Document)hashTable.get(uri);
    }

    @Override
    public boolean deleteDocument(URI uri) {
        if (getDocument(uri) == null){
            return false;
        } else {
            hashTable.put(uri, null);
            return true;
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
