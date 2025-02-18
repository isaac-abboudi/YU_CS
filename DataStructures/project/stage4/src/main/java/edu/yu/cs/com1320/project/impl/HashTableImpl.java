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

    private int numOfEntries;
    private int sizeOfTable;
    private Entry[] table;
    private boolean resizing = false;

    public HashTableImpl(){
        this.table = new Entry[5];
        this.sizeOfTable = table.length;
    }

    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % table.length;
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
        if (k == null){ //no op
            return null;
        }
        if (v == null){
            delete(k);
            return null;
        }
        int index = this.hashFunction(k);
        Entry newEntry = new Entry(k,v);
        if (table[index] == null){
            table[index] = newEntry;
            if (!resizing){
                numOfEntries++;
            }
            if (!resizing && numOfEntries >= sizeOfTable * 0.75){
                resize();
            }
            return null;
        }
        Value old;
        Entry current = table[index];
        while (current.key != k && current.next != null){
            current = current.next;
        }
        if (current.key == k){
            old = (Value)current.value;
            current.value = v;
            if (!resizing){
                numOfEntries++;
            }
            return old;
        }
        current.setNext(newEntry);
        if (!resizing){
            numOfEntries++;
        }
        return null; //changed from return null
    }

    private int delete(Key k){
        int index = this.hashFunction(k);
        Entry current = table[index];
        Entry previous = null;
        while (current.key != k && current.next != null){
            previous = current;
            current = current.next;
        }
        if (current.key == k){
            if (previous == null){
                return deleteFirstLink(index, current, k);
            }
            if (current.next != null){
                return deleteMiddleLink(previous, current, k);
            }
            return deleteLastLink(previous, k);
        }
        return 0;
    }
    private void resize(){
        resizing = true;
        Entry[] oldTable = table;
        Entry[] newTable = new Entry[sizeOfTable*2];
        table = newTable;
        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null) { //index is a link
                Entry current = oldTable[i];
                put((Key) current.key, (Value) current.value); //put into new table
                while (current.next != null) {
                    current = current.next;                         //iterate over
                    put((Key) current.key, (Value) current.value); // and put
                }
            }
        }
        sizeOfTable = this.table.length;
        resizing = false;
    }
    private int deleteFirstLink(int index, Entry current, Key k){
        table[index] = current.next;
        numOfEntries--;
        return hashFunction(k);
    }
    private int deleteMiddleLink(Entry previous, Entry current, Key k){
        previous.next = current.next;
        numOfEntries--;
        return hashFunction(k);
    }
    private int deleteLastLink(Entry previous, Key k){
        previous.next = null;
        numOfEntries--;
        return hashFunction(k);
    }
}