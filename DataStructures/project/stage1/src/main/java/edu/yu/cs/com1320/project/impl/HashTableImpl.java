package edu.yu.cs.com1320.project.impl;


import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {
    class Entry<Key, Value> {
        Key key;
        Value value;
        Entry next;
        Entry(Key k, Value v) {
            if (k == null) {
                throw new IllegalArgumentException();
            }
            key = k;
            value = v;
        }
        void setNext(Entry next){
            this.next = next;
        }
    }

    private Entry[] table;
    public HashTableImpl(){
        this.table = new Entry[5];
    }

    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }

    @Override
    public Value get(Key k) {
        int index = this.hashFunction(k);
        Entry current = this.table[index];
        if (current == null){
            return null;
        }
        while (current.key != k && current.next != null){
            current = current.next;
        }
        if (current.key != k){
            return null;
        }
        return (Value)current.value;
    }

    @Override
    public Value put(Key k, Value v) {
        if (v == null){
            delete(k);
            return null; //return null if its deleted?
        }
        int index = this.hashFunction(k);
        Entry newEntry = new Entry(k,v);
        if (table[index] == null){
            table[index] = newEntry;
            return (Value)newEntry.value;
        }
        Value old;
        Entry current = table[index];
        Entry previous = null;
        while (current.key != k && current.next != null){
            previous = current;
            current = current.next;
        }
        if (current.key == k){
            old = (Value)current.value;
            Entry next = current.next;
            current.value = v;
//            if (previous != null){
//                previous.setNext(newEntry);
//            }
            //newEntry.setNext(next);
            return old;
        }
        current.setNext(newEntry);
        return null;
    }

    private int delete(Key k){
        int index = this.hashFunction(k);
        Entry newEntry = new Entry(k,null);
        Entry current = table[index];
        Entry previous = null;
        while (current.key != k && current.next != null){
            previous = current;
            current = current.next;
        }
        if (current.next != null){
            previous.next = current.next;
            return hashFunction(k);

        } else if (current.next == null){
            if (previous != null){
                previous.next = null;
            }
            table[index] = null;
            return hashFunction(k);
        }
        return 0;
    }
}
