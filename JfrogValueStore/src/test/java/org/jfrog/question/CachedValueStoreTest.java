/**
 * This is a test, designed to help if needed to show the idea behind the cache.
 * You can use it if you want
 */
package org.jfrog.question;

import org.jfrog.question.service.ConcurrentLRULinkedHashMapCacheService;
import org.jfrog.question.service.ConcurrentLRULinkedListMapCacheService;
import org.jfrog.question.service.LRULinkedHashMapCacheService;
import org.jfrog.question.stores.CachedValueStore;
import org.jfrog.question.stores.FileValueStore;
import org.jfrog.question.stores.MapValueStore;
import org.jfrog.question.stores.ValueStore;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class CachedValueStoreTest {

    @Test
    void testCache() {
        ValueStore[] stores = new ValueStore[]{new MapValueStore()};
        CachedValueStore cache = new CachedValueStore(Arrays.asList(stores), 5);
        cache.put("a", "b");
        String a = cache.read("a");
        assertEquals("b", a);
        cache.delete("a");
        a = cache.read("a");
        assertNull(a);
    }

    @Test
    void testPut() {
        MapValueStore mapStore = new MapValueStore();
        ValueStore[] stores = new ValueStore[]{mapStore};
        CachedValueStore cache = new CachedValueStore(Arrays.asList(stores), 2);
        cache.put("1", "a");
        cache.put("2", "b");
        mapStore.delete("2");
        cache.put("3", "c");

        String a = cache.read("2");
        assertNotNull(a);
    }

    @Test
    void testLru() throws IOException {
        File inputFile = Files.createTempFile("a", ".txt").toFile();
        inputFile.deleteOnExit();
        ValueStore mapStore = new FileValueStore(inputFile);
        ValueStore[] stores = new ValueStore[]{mapStore};
        CachedValueStore cache = new CachedValueStore(Arrays.asList(stores), 2);
        cache.put("1", "a");
        cache.put("2", "b");
        cache.put("3", "c");
        mapStore.delete("1");

        String a = cache.read("1");
        assertNull(a);
    }

    @Test
    void testCaseOne() throws IOException {
        File inputFile = Files.createTempFile("a", ".txt").toFile();
        inputFile.deleteOnExit();
        ValueStore mapStore = new FileValueStore(inputFile);
        ValueStore[] stores = new ValueStore[]{mapStore};
        CachedValueStore cache = new CachedValueStore(Arrays.asList(stores), 2);
        cache.put("1", "a");
        cache.put("2", "b");
        cache.put("3", "c");
        String a = cache.read("1");

        //in that case key "1" will not return from the cache, but from the store
        assertNotNull(a);

        //in that case key "1" will return from the cache, because we returned it into the cache in the previous read
        String b = cache.read("1");
        assertNotNull(b);

        //in that case key "2" will no longer exist because the LRU methodology evacuated him and we deleted him from the store himself

        mapStore.delete("2");
        String c = cache.read("2");
        assertNull(c);
    }

    @Test
    void testCaseOneWithLinkedHashMap() throws IOException {
        File inputFile = Files.createTempFile("a", ".txt").toFile();
        inputFile.deleteOnExit();
        ValueStore mapStore = new FileValueStore(inputFile);
        ValueStore[] stores = new ValueStore[]{mapStore};
        LRULinkedHashMapCacheService<String, String> lruLinkedHashMapCacheService = new LRULinkedHashMapCacheService<>(2);
        CachedValueStore cache = new CachedValueStore(Arrays.asList(stores), lruLinkedHashMapCacheService);
        cache.put("1", "a");
        cache.put("2", "b");
        cache.put("3", "c");
        String a = cache.read("1");

        //in that case key "1" will not return from the cache, but from the store
        assertNotNull(a);

        //in that case key "1" will return from the cache, because we returned it into the cache in the previous read
        String b = cache.read("1");
        assertNotNull(b);

        //in that case key "2" will no longer exist because the LRU methodology evacuated him and we deleted him from the store himself

        mapStore.delete("2");
        String c = cache.read("2");
        assertNull(c);
    }

    @Test
    void testCaseTwo() throws IOException {
        File inputFile = Files.createTempFile("a", ".txt").toFile();
        inputFile.deleteOnExit();
        ValueStore mapStore = new FileValueStore(inputFile);
        ValueStore[] stores = new ValueStore[]{mapStore};
        CachedValueStore cache = new CachedValueStore(Arrays.asList(stores), 2);
        cache.put("1", "a");
        cache.put("2", "b");
        cache.put("3", "c");
        cache.read("3");
        cache.read("1");
        cache.delete("2");
        cache.put("1", "d");
        cache.put("2", "e");

        assertEquals("{1=d, 2=e}", cache.toString());

    }

//    @Test
//    void testCaseOneWithConcurrentLinkedHashMap() throws IOException {
//        File inputFile = Files.createTempFile("a", ".txt").toFile();
//        inputFile.deleteOnExit();
//        ValueStore mapStore = new FileValueStore(inputFile);
//        ValueStore[] stores = new ValueStore[]{mapStore};
//        ConcurrentLRULinkedHashMapCacheService<String, String> lruLinkedHashMapCacheService = new ConcurrentLRULinkedHashMapCacheService<>(2);
//        CachedValueStore cache = new CachedValueStore(Arrays.asList(stores), lruLinkedHashMapCacheService);
//
//        Thread threadPut1 = new Thread(createPutRunnable(cache, "1", "a"));
//        Thread threadPut2 = new Thread(createPutRunnable(cache, "2", "b"));
//        Thread threadPut3 = new Thread(createPutRunnable(cache, "3", "c"));
//
//        Thread threadRead1 = new Thread(createReadRunnable(cache, "3"));
//        Thread threadRead2 = new Thread(createReadRunnable(cache, "1"));
//
//        Thread threadDelete1 = new Thread(createDeleteRunnable(cache, "2"));
//
//        Thread threadPut4 = new Thread(createPutRunnable(cache, "1", "d"));
//        Thread threadPut5 = new Thread(createPutRunnable(cache, "2", "e"));
//
//        threadPut1.start();
//        threadPut2.start();
//        threadPut3.start();
//        threadRead1.start();
//        threadRead2.start();
//        threadDelete1.start();
//        threadPut4.start();
//        threadPut5.start();
//
//        try {
//            threadPut1.join();
//            threadPut2.join();
//            threadPut3.join();
//            threadRead1.join();
//            threadRead2.join();
//            threadDelete1.join();
//            threadPut5.join();
//            threadPut4.join();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        assertEquals("{1=d, 2=e}", cache.toString());
//    }

}