package com.transfinesy.util;

import java.util.NoSuchElementException;

/**
 * Generic Stack data structure implementation (LIFO - Last In First Out).
 * Used for managing transaction history and viewing recent transactions.
 */
public class Stack<T> {
    private Node<T> top;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public Stack() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Pushes an element onto the top of the stack.
     */
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    /**
     * Removes and returns the element from the top of the stack.
     */
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Returns the element at the top without removing it.
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return top.data;
    }

    /**
     * Checks if the stack is empty.
     */
    public boolean isEmpty() {
        return top == null;
    }

    /**
     * Returns the number of elements in the stack.
     */
    public int size() {
        return size;
    }

    /**
     * Clears all elements from the stack.
     */
    public void clear() {
        top = null;
        size = 0;
    }
}

