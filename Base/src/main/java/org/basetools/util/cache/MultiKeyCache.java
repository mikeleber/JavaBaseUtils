package org.basetools.util.cache;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Simple cache implementation backed by a map. This implementation use ReadWritLock to ensure it's threadsave.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public class MultiKeyCache<K, V> {
    private final int cacheSize;
    private MultiKeyMap<K, V> cache = null;
    private ReadWriteLock readWriteLock = null;


    /**
     * Instantiates a new Cache.
     *
     * @param psCacheMap the ps cache map
     * @param size       the size
     */
    public MultiKeyCache(MultiKeyMap<K, V> psCacheMap, int size) {
        cache = psCacheMap;
        cacheSize = size;
        readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Put v.
     *
     * @param key   the key
     * @param value the value
     * @return the old Key
     */
    public V put(MultiKey<? extends K> key, V value) {
        if (cache.size() >= cacheSize && cacheSize > 0) {
            MultiKey<? extends K> oldKey = cache.keySet().iterator().next();
            remove(oldKey);
        }
        return cache.put(key, value);
    }

    public V put(K key, K key2, V value) {
        if (cache.size() >= cacheSize && cacheSize > 0) {
            MultiKey<? extends K> oldKey = cache.keySet().iterator().next();
            remove(oldKey);
        }
        return cache.put(key, key2, value);
    }

    public V put(K key, K key2, K key3, V value) {
        if (cache.size() >= cacheSize && cacheSize > 0) {
            MultiKey<? extends K> oldKey = cache.keySet().iterator().next();
            remove(oldKey);
        }
        return cache.put(key, key2, key3, value);
    }

    public V put(K key, K key2, K key3, K key4, V value) {
        if (cache.size() >= cacheSize && cacheSize > 0) {
            MultiKey<? extends K> oldKey = cache.keySet().iterator().next();
            remove(oldKey);
        }
        return cache.put(key, key2, key3, key4, value);
    }

    public V put(K key, K key2, K key3, K key4, K key5, V value) {
        if (cache.size() >= cacheSize && cacheSize > 0) {
            MultiKey<? extends K> oldKey = cache.keySet().iterator().next();
            remove(oldKey);
        }
        return cache.put(key, key2, key3, key4, key5, value);
    }

    /**
     * Get v.
     *
     * @param key the key
     * @return the v
     */
    public V get(MultiKey<? extends K> key) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public V get(Object key, Object key2) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.get(key, key2);
        } finally {
            readLock.unlock();
        }
    }

    public V get(Object key, Object key2, Object key3) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.get(key, key2, key3);
        } finally {
            readLock.unlock();
        }
    }

    public V get(Object key, Object key2, Object key3, Object key4) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.get(key, key2, key3, key4);
        } finally {
            readLock.unlock();
        }
    }

    public V get(Object key, Object key2, Object key3, Object key4, Object key5) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.get(key, key2, key3, key4, key5);
        } finally {
            readLock.unlock();
        }
    }

    public V getOrCreate(MultiKey<? extends K> key, Supplier<V> creator) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            V result = get(key);
            if (result == null) {
                result = creator.get();
                put(key, result);
            }
            return result;
        } finally {
            writeLock.unlock();
        }

    }

    /**
     * Contains key boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean containsKey(K key) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsKey(Object key, Object key2) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.containsKey(key, key2);
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsKey(Object key, Object key2, Object key3) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.containsKey(key, key2, key3);
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsKey(Object key, Object key2, Object key3, Object key4) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.containsKey(key, key2, key3, key4);
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsKey(Object key, Object key2, Object key3, Object key4, Object key5) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.containsKey(key, key2, key3, key4, key5);
        } finally {
            readLock.unlock();
        }
    }

    public JsonObject getInfo() {
        JsonArrayBuilder entries = Json.createArrayBuilder();
        traverse((key, value) -> {
            entries.add(key.toString());
            return null;
        });
        return Json.createObjectBuilder()
                .add("size", getCache().size())
                .add("entries", entries).build();
    }

    public void traverse(BiFunction<MultiKey<? extends K>, V, Void> processor) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            getCache().entrySet().forEach((e) -> processor.apply(e.getKey(), e.getValue()));
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Remove v.
     *
     * @param key the key
     * @return the v
     */
    public V remove(MultiKey<? extends K> key) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            return cache.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    public V removeMultiKey(Object key, Object key2) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            return cache.removeMultiKey(key, key2);
        } finally {
            writeLock.unlock();
        }
    }

    public V removeMultiKey(Object key, Object key2, Object key3) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            return cache.removeMultiKey(key, key2, key3);
        } finally {
            writeLock.unlock();
        }
    }

    public V removeMultiKey(Object key, Object key2, Object key3, Object key4) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            return cache.removeMultiKey(key, key2, key3, key4);
        } finally {
            writeLock.unlock();
        }
    }

    public V removeMultiKey(Object key, Object key2, Object key3, Object key4, Object key5) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            return cache.removeMultiKey(key, key2, key3, key4, key5);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns the used map used for caching.
     *
     * @return cache
     */
    public MultiKeyMap<K, V> getCache() {
        return cache;
    }

    /**
     * Returns the ReadWriteLock.
     *
     * @return read write lock
     */
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    /**
     * Removes all cache entries.
     */
    public void clear() {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            getCache().clear();
        } finally {
            writeLock.unlock();
        }
    }
}
