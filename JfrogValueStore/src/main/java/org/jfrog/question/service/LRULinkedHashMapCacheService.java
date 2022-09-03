package org.jfrog.question.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An implementation of CacheService that uses LRU methodology implemented with LinkedHashMap Java Object
 */

public class LRULinkedHashMapCacheService<T, E> implements CacheService<T, E> {
    protected Map<T, E> cache;

    public LRULinkedHashMapCacheService(int capacity) {
        cache = new LinkedHashMap<T, E>(capacity, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<T, E> eldest) {
                return size() > capacity;
            }
        };
    }

    public LRULinkedHashMapCacheService() {
    }

    @Override
    public E onRead(T key) {
        return cache.get(key);
    }

    @Override
    public void onPut(T key, E value) {
        cache.put(key, value);
    }

    @Override
    public void onDelete(T key) {
        cache.remove(key);
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
