package org.jfrog.question.stores;

import org.apache.commons.lang3.StringUtils;
import org.jfrog.question.service.CacheService;
import org.jfrog.question.service.LRULinkedListMapCacheService;

import java.util.List;
public class CachedValueStore implements ValueStore {

    // a list of all available value stores. list order should be kept.
    private final List<ValueStore> valueStores;
    private final CacheService<String, String> cacheService;

    /**
     * Constructor for the cache value store.
     * @param valueStores        list of ordered value stores to operate on
     * @param cacheService       cacheService to use with
     */
    public CachedValueStore(List<ValueStore> valueStores, CacheService<String, String> cacheService) {
        this.valueStores = valueStores;
        this.cacheService = cacheService;
    }

    /**
     * Constructor for the cache value store.
     * @param valueStores        list of ordered value stores to operate on
     * @param maxCachedItems    number of maximum items the cache can hold
     * choosing LRULinkedListMapCacheService as default CacheService implementation
     */
    public CachedValueStore(List<ValueStore> valueStores, int maxCachedItems) {
        this.valueStores = valueStores;
        this.cacheService = new LRULinkedListMapCacheService<>(maxCachedItems);
    }

    /**
     * First check for the availability of the key in the cache.
     * if it does not exist but exists in the store return it to the cache
     * Iterate on valueStores and find the first key that matches. order is important.
     */
    public String read(String key) {
        String value = cacheService.onRead(key);
        if (value == null) {
            value = valueStores
                    .stream()
                    .filter(store -> StringUtils.isNotBlank(store.read(key)))
                    .findFirst()
                    .map(valueStore -> valueStore.read(key))
                    .orElse(null);
            cacheService.onPut(key, value);
        }
        return value;
    }

    /**
     * Put <key, value> in first valueStores only. order is important.
     */
    public void put(String key, String value) {
        cacheService.onPut(key, value);
        valueStores.get(0).put(key, value);
    }

    /**
     * Iterate on valueStores and delete the key from all of them.
     */
    public void delete(String key) {
        cacheService.onDelete(key);
        valueStores
        .stream()
        .filter(store -> StringUtils.isNotBlank(store.read(key)))
        .forEach(valueStore -> valueStore.delete(key));
    }

    @Override
    public String toString() {
        return cacheService.toString();
    }
}