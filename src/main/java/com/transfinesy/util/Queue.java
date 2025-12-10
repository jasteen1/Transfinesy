package com.transfinesy.util;

import java.util.NoSuchElementException;

/**
 * Generic Queue data structure implementation (FIFO - First In First Out).
 * Used for processing attendance scans in order.
 */
public class Queue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public Queue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    /**
     * Adds an element to the rear of the queue (enqueue).
     */
    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the element from the front of the queue (dequeue).
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    /**
     * Returns the element at the front without removing it.
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return front.data;
    }

    /**
     * Checks if the queue is empty.
     */
    public boolean isEmpty() {
        return front == null;
    }

    /**
     * Returns the number of elements in the queue.
     */
    public int size() {
        return size;
    }

    /**
     * Clears all elements from the queue.
     */
    public void clear() {
        front = rear = null;
        size = 0;
    }
}

