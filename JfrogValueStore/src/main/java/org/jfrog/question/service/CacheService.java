package org.jfrog.question.service;

/**
 * Provide an interface of caching service that will handle cached data by key and value
 */

public interface CacheService<T, E> {
    E onRead(T key);

    void onPut(T key, E value);

    void onDelete(T key);
}