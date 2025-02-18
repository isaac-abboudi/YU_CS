package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {

    public MinHeapImpl(){
        this.elements = (E[]) new Comparable[4];

    }

    @Override
    public void reHeapify(E element) {
        if (!contains(element)){
            throw new NoSuchElementException();
        }
        int index = getArrayIndex(element);
        upHeap(index);
        downHeap(index);
    }

    @Override
    protected int getArrayIndex(E element) {
        if (!contains(element)){
            throw new NoSuchElementException();
        }
        int i = 1;
        while (!elements[i].equals(element)){
            i++;
        }
        return i;
    }

    @Override
    protected void doubleArraySize() {
        E[] newArray = (E[]) new Comparable[elements.length * 2];
        for (int i = 1; i < elements.length; i++){
            newArray[i] = elements[i];
        }
        elements = newArray;
    }

    private boolean contains(E element){
        int i = 1;
        while (!elements[i].equals(element)){
            i++;
            if (i > count){
                return false;
            }
        }
        return true;
    }
}
