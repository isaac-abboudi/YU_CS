package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage1.Document;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

public class DocumentImpl implements Document {

    private String text;
    private URI uri;
    private byte[] binaryData;

    public DocumentImpl(URI uri, String text){
        this.text = text;
        this.uri = uri;
    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null || binaryData == null){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }

    @Override
    public String getDocumentTxt() {
        return text;
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return binaryData;
    }

    @Override
    public URI getKey() {
        return uri;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0); result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    @Override
    public boolean equals(Object o){
        if (this.hashCode() == o.hashCode()){
            return true;
        }
        return false;
    }

}
