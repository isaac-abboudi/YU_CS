package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {

    private static final int alphabetSize = 36; // add 10 for #'s
    private Node root;

    public static class Node<Value>{
        private ArrayList<Value> values = new ArrayList<>();
        private Node[] links = new Node[alphabetSize];
    }

    @Override
    public void put(String key, Value val) {
        if (key == null || val == null){
            throw new IllegalArgumentException();
        }
        if (key.equals("")){
            return;
        }
        else
        {
            this.root = put(this.root, key, val, 0);
        }
    }

    private Node put(Node x, String key, Value val, int d){
        if (key == null || val == null){
            throw new IllegalArgumentException();
        }
        if (d == 0){
            // make lowercase and strip nonletters
            key = cleanString(key);
        }
        if (x == null)
        {
            x = new Node();
        }
        if (d == key.length())
        {
            x.values.add(val);
            return x;
        }
        char c = key.charAt(d);
        if (c < 97){
            c += 76;
        }
        x.links[c-97] = this.put(x.links[c-97], key, val, d + 1);
        return x;
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
    public List<Value> getAllSorted(String key, Comparator comparator) {
        if (key == null || comparator == null){
            throw new IllegalArgumentException();
        }
        if (key.equals("")){
            List<Value> empty = new ArrayList<Value>();
            return empty;
        }
        key = cleanString(key);
        Node x = this.get(this.root, key, 0);
        if (x == null)
        {
            List<Value> empty = new ArrayList<Value>(); //return empty list if null?
            return empty;
        }
        x.values.sort(comparator);
        return x.values;
    }

    private Node get(Node x, String key, int d){
        if (d == 0){
            key = cleanString(key);
        }
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        if (c < 97){
            c += 76;
        }
        return this.get(x.links[c-97], key, d + 1);
    }


    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if (prefix == null || comparator == null){
            throw new IllegalArgumentException();
        }
        if (prefix.equals("")){
            List<Value> empty = new ArrayList<Value>();
            return empty;
        }
        Set<Value> results = new HashSet<Value>();
        Node x = get(this.root, prefix, 0); // what if large gap between prefix and next actual key
        if (x != null){
            getPrefixes(x, new StringBuilder(prefix), results);
        }
        ArrayList<Value> resultsList = new ArrayList<>(results);
        resultsList.sort(comparator); //reversed?
        return resultsList;
    }

    private void getPrefixes(Node x, StringBuilder prefix, Set<Value> results){ // can i use a stringbuilder?
        if (!x.values.isEmpty()){
            results.addAll(x.values);
        }
//        for (char c = 97; c < 123; c++){ // should c be 0 or == a ?
//            if (x.links[c-97] != null){
//                prefix.append(c);
//                getPrefixes(x.links[c-97], prefix, results);
//                prefix.deleteCharAt(prefix.length() - 1);
//            }
//        }
        char c = 'a';
        for (int i = 0; i < alphabetSize; i++){
            if (i == 25){ // ran outta letters, switch to numbers
                c = 48;
            }
            if (x.links[i] != null){
                prefix.append(c);
                getPrefixes(x.links[i], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
            c++;
        }
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if (prefix == null){
            throw new IllegalArgumentException();
        }
        if (prefix.equals("")){
            Set<Value> empty = new HashSet<Value>();
            return empty;
        }
        HashSet deleted = new HashSet<Value>();
        Node x = get(this.root, prefix, 0);
        deleteByPrefix(x, new StringBuilder(prefix), deleted);
        return deleted;
    }

    private void deleteByPrefix(Node x, StringBuilder prefix, Set deleted){
        if (!x.values.isEmpty()){
            deleted.addAll(x.values);
            x.values.clear(); // should i delete this way?
        }
        char c = 'a';
        for (int i = 0; i < alphabetSize; i++){
            if (i == 25){ // ran outta letters, switch to numbers
                c = 48;
            }
            if (x.links[i] != null){
                prefix.append(c);
                deleteByPrefix(x.links[i], prefix, deleted); // changed from getPrefixes()
                prefix.deleteCharAt(prefix.length() - 1);
            }
            c++;
        }
    }

    @Override
    public Set<Value> deleteAll(String key) {
        if (key == null){
            throw new IllegalArgumentException();
        }
        if (key.equals("")){
            Set<Value> empty = new HashSet<Value>();
            return empty;
        }
        Node x = get(this.root, key, 0); //get the node
        Set<Value> valSet = new HashSet<>(x.values);
        this.root = deleteAll(this.root, key, 0); // delete values
        return valSet;
    }

    private Node deleteAll(Node x, String key, int d) {
        if (d == 0){
            key = cleanString(key);
        }
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
            x.values.clear(); // set to null or make empty?
        }
        //continue down the trie to the target node
        else
        {
            char c = key.charAt(d);
            if (c < 97){
                c += 76;
            }
            x.links[c-97] = this.deleteAll(x.links[c-97], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.values != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    @Override
    public Value delete(String key, Value val) {
        if (key == null || val == null){
            throw new IllegalArgumentException();
        }
        if (key.equals("")){
            return null;
        }
        Node x = get(this.root, key, 0);
        if (x.values.remove(val)){
            return val;
        }
        return null;
    }

}