package org.jfrog.question.service;

import java.util.HashMap;
import java.util.Map;

public class LRULinkedListMapCacheService<T, E> implements CacheService<T, E> {
    private final Node<T, E> head;
    private final Node<T, E> tail;
    protected Map<T, Node<T, E>> cache;
    protected int capacity, total;

    public LRULinkedListMapCacheService(int capacity) {
        cache = new HashMap<>();
        this.capacity = capacity;
        this.total = 0;

        head = new Node<>();
        tail = new Node<>();

        head.next = tail;
        tail.prev = head;

        head.prev = null;
        tail.next = null;
    }

    @Override
    public E onRead(T key) {
        Node<T, E> node = cache.get(key);

        if (node == null) {
            return null;
        }

        reinsertNode(node);
        return node.value;
    }

    @Override
    public void onPut(T key, E value) {

        Node<T, E> node = cache.get(key);
        if (node == null) {
            Node<T, E> newNode = new Node<>();
            newNode.key = key;
            newNode.value = value;

            cache.put(key, newNode);
            addNode(newNode);

            total++;

            if (total > capacity) {
                LRUItemEvictions();
            }
        } else {
            node.value = value;
            reinsertNode(node);
        }
    }

    @Override
    public void onDelete(T key) {
        if (cache.get(key) != null) {
            Node<T, E> nodeToRemove = cache.get(key);
            delete(nodeToRemove);
        }
    }

    private void delete(Node<T, E> node) {
        removeNode(node);
        cache.remove(node.key);
        total--;
    }


    private void LRUItemEvictions() {
        Node<T, E> tail = getLRUNode();
        delete(tail);
    }

    private void reinsertNode(Node<T, E> node) {
        removeNode(node);
        addNode(node);
    }

    private void addNode(Node<T, E> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<T, E> node) {
        Node<T, E> tmpPrev = node.prev;
        Node<T, E> tmpNext = node.next;

        tmpPrev.next = tmpNext;
        tmpNext.prev = tmpPrev;

        node.next = null;
        node.prev = null;
    }

    private Node<T, E> getLRUNode() {
        return tail.prev;
    }

    @Override
    public String toString() {
        return cache.toString();
    }

    static class Node<T, E> {
        T key;
        E value;
        Node<T, E> next;
        Node<T, E> prev;

        @Override
        public String toString() {
            return value.toString();
        }
    }
}