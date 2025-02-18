package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.Document;
import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document {

    private String text;
    private URI uri;
    private byte[] binaryData;
    private HashMap<String, Integer> wordCount;
    private long timeLastUsed;

    public DocumentImpl(URI uri, String text){
        this.text = text;
        this.uri = uri;
        this.wordCount = new HashMap<>();
        mapWords(text);
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
    public int wordCount(String word) {
        word = cleanString(word);
        if (wordCount != null && wordCount.containsKey(word)){
            return wordCount.get(word);
        } else {
            return 0;
        }
    }

    @Override
    public Set<String> getWords() {
        if (wordCount != null){
            return wordCount.keySet();
        } else {
            HashSet<String> empty = new HashSet<String>();
            return empty;
        }

    }

    //new for stage4
    @Override
    public long getLastUseTime() {
        return timeLastUsed;
    }

    //new for stage4
    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        timeLastUsed = timeInNanoseconds;
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

    private void mapWords(String text){
        // change to lower case
        text = text.toLowerCase();
        // split into array of words
        String [] textArray = text.split(" ");
        // for each word
        for (int word = 0; word < textArray.length; word++){
            String w = textArray[word];
            if (!w.equals("")){ // not just a space, so add it
                w = cleanString(w);
                if (wordCount.containsKey(w)){
                    wordCount.put(w, wordCount.get(w)+1);
                } else {
                    wordCount.put(w, 1);
                }
            }
        }
    }

    private String cleanString(String word){
        word = word.toLowerCase();
        StringBuilder sb = new StringBuilder(word);
        for (int i = sb.length(); i > 0; i--){
            // if its both Not inbtwn a-z && not inbtwn 0-9
            if ((sb.charAt(i-1) < 97 || sb.charAt(i-1) > 122) && (sb.charAt(i-1) < 48 || sb.charAt(i-1) > 57)){
                sb.deleteCharAt(i-1);
            }
        }
        word = sb.toString();
        return word;
    }

    @Override
    public int compareTo(Document o) { //confirm return values
        if (this.timeLastUsed > o.getLastUseTime()){
            return 1;
        } else if (this.timeLastUsed < o.getLastUseTime()){
            return -1;
        }
        else {
            return 0;
        }
    }
}