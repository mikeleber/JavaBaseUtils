package org.basetools.util.cache;

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
public class Cache<K, V> {
    private final int cacheSize;
    private Map<K, V> cache = null;
    private ReadWriteLock readWriteLock = null;

    /**
     * Instantiates a new Cache.
     *
     * @param psCacheMap the ps cache map
     * @param size       the size
     */
    public Cache(Map<K, V> psCacheMap, int size) {
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
    public V put(K key, V value) {
        if (cache.size() >= cacheSize && cacheSize > 0) {
            K oldKey = cache.keySet().iterator().next();
            remove(oldKey);
        }
        return cache.put(key, value);
    }

    /**
     * Get v.
     *
     * @param key the key
     * @return the v
     */
    public V get(K key) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            return cache.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public V getOrCreate(K key, Supplier<V> creator) {
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

    public void traverse(BiFunction<K, V, Void> processor) {
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
    public V remove(K key) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            return cache.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns the used map used for caching.
     *
     * @return cache
     */
    public Map<K, V> getCache() {
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
