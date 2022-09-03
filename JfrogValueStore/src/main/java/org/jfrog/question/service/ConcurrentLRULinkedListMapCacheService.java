package org.jfrog.question.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentLRULinkedListMapCacheService<T, E> implements CacheService<T, E> {

    private final ConcurrentLinkedQueue<T> queue;
    private final ConcurrentHashMap<T, E> map;
    private final Lock writeLock;
    private final Lock readLock;
    protected int capacity, total;

    public ConcurrentLRULinkedListMapCacheService(int capacity) {
        this.capacity = capacity;
        this.total = 0;
        queue = new ConcurrentLinkedQueue<>();
        map = new ConcurrentHashMap<>();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    @Override
    public E onRead(T key) {
        writeLock.lock();
        System.out.println("onRead " + key);
        try {
            E val = null;
            if (map.contains(key)) {
                queue.remove(key);
                val = map.get(key);
                queue.add(key);
            }
            return val;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void onPut(T key, E value) {
        writeLock.lock();
        System.out.println("onPut " + key + " " + value);
        try {
            if (map.contains(key)) {
                queue.remove(key);
            }
            if (queue.size() == capacity) {
                T queueKey = queue.poll();
                map.remove(queueKey);
            }
            queue.add(key);
            map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void onDelete(T key) {
        writeLock.lock();
        writeLock.unlock();
        System.out.println("onDelete " + key);
        try {
            if (map.contains(key)) {
                map.remove(key);
                queue.remove(key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
