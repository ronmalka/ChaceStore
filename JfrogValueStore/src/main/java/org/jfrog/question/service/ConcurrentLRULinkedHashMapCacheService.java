package org.jfrog.question.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentLRULinkedHashMapCacheService<T, E> extends LRULinkedHashMapCacheService<T, E> {

    private final Lock writeLock;
    private final Lock readLock;

    public ConcurrentLRULinkedHashMapCacheService(int capacity) {
        cache = Collections.synchronizedMap(new LinkedHashMap<T, E>(capacity, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<T, E> eldest) {
                return size() > capacity;
            }
        });
        ReadWriteLock lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    @Override
    public E onRead(T key) {
        writeLock.lock();
        System.out.println("onRead " + key);
        try {
            return super.onRead(key);
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public void onPut(T key, E value) {
        writeLock.lock();
        System.out.println("onPut " + key + " " + value);
        try {
            super.onPut(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void onDelete(T key) {
        writeLock.lock();
        System.out.println("onDelete " + key);
        try {
            super.onDelete(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
