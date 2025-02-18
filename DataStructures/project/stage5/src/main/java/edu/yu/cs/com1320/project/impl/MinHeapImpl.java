package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

import java.net.URI;
import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {

    private BTreeImpl bTree;

    public MinHeapImpl(BTreeImpl bTree){
        this.elements = (E[]) new Comparable[4];
        this.bTree = bTree;
    }

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
        //while (!elements[i].equals(element)){
        while (!bTree.get(elements[i]).equals(bTree.get(element))){
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
        //while (!elements[i].equals(element)){
        while (!bTree.get(elements[i]).equals(bTree.get(element))){
            i++;
            if (i > count){
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean isGreater(int i, int j){
        return ((Comparable)bTree.get(elements[i])).compareTo(bTree.get(elements[j])) > 0;
    }

    @Override
    protected void upHeap(int k) {
        while (k > 1 && this.isGreater(k / 2, k)) {
            this.swap(k, k / 2);
            k = k / 2;
        }
    }

    @Override
    protected void downHeap(int k) {
        while (2 * k <= this.count) {
            //identify which of the 2 children are smaller
            int j = 2 * k;
            if (j < this.count && this.isGreater(j, j + 1)) {
                j++;
            }
            //if the current value is < the smaller child, we're done
            if (!this.isGreater(k, j)) {
                break;
            }
            //if not, swap and continue testing
            this.swap(k, j);
            k = j;
        }
    }

    @Override
    protected void swap(int i, int j) {
        E temp = this.elements[i];
        this.elements[i] = this.elements[j];
        this.elements[j] = temp;
    }

    @Override
    public void insert(E x) {
        // double size of array if necessary
        if (this.count >= this.elements.length - 1) {
            this.doubleArraySize();
        }
        //add x to the bottom of the heap
        this.elements[++this.count] = x;
        //percolate it up to maintain heap order property
        this.upHeap(this.count);
    }

    @Override
    public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        E min = this.elements[1];
        //swap root with last, decrement count
        this.swap(1, this.count--);
        //move new root down as needed
        this.downHeap(1);
        this.elements[this.count + 1] = null; //null it to prepare for GC
        return min;
    }

}