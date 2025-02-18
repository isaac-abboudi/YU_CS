package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    class Link<T> {
        Link next;
        T data;
        Link(T newLink){
            this.data = newLink;
        }
        public void setNext(Link next) {
            this.next = next;
        }
    }

    private Link top;
    private int size;

    public StackImpl(){
        this.top = new Link(null);
    }

    @Override
    public void push(T element) {
        Link pushed = new Link(element);
        if (top.next != null){
            Link temp = top.next;
            pushed.setNext(temp);
        }
        top.setNext(pushed);
        size++;
    }

    @Override
    public T pop() {
        if (top.next != null){
            Link popped = top.next;
            top.next = top.next.next;
            size--;
            return (T) popped.data;
        }
        return null;
    }

    @Override
    public T peek() {
        if (top.next != null){
            return (T) top.next.data; // .data or as a link
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }
}
