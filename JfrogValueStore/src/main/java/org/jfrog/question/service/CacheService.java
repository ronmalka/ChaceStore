package org.jfrog.question.service;

public interface CacheService<T,E> {
    E onRead(T key);
    void onPut(T key, E value);
    void onDelete(T key);
}