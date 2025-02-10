package org.basetools.util.cache;

import net.minidev.json.JSONObject;
import org.basetools.format.DateFormatter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Instances of this class can be used as object cache. Each MCRCache has a
 * certain capacity, the maximum number of objects the cache will hold. When the
 * cache is full and another object is put into the cache, the cache will
 * discard the least recently used object to get place for the new object. The
 * cache will always hold the most recently used objects by updating its
 * internal structure whenever an object is get from the cache or put into the
 * cache. The cache also provides methods for getting the current cache hit rate
 * and fill rate. Like in a hashtable, an MCRCache uses a unique key for each
 * object.
 *
 * @see java.util.Hashtable
 */
public class MRUMap<K, O, T, S> implements Runnable {
    public final Comparator<? extends CacheEntry<K, O>> CACHE_ENTRY_COMPARATOR = (o1, o2) -> {
        int returnComp = 0;
        if (o1 != null && o2 != null) {
            returnComp = o2.getHits() - o1.getHits();
            if (returnComp == 0) {
                returnComp = (int) ((o2).getTime() - (o1).getTime());
            }
        } else if (o1 != null) {
            returnComp = -1;
        } else if (o2 != null) {
            returnComp = -1;
        }
        return returnComp;
    };
    protected ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * The most recently used object *
     */
    protected CacheEntry<K, O> mru;
    /**
     * The least recently used object *
     */
    protected CacheEntry<K, O> lru;
    /**
     * A hashtable for looking up a cached object by a given key
     */
    protected Map<K, CacheEntry<K, O>> lookupIndex = new HashMap<>();
    /**
     * The number of requests to get an object from this cache
     */
    protected long requests = 0;
    /**
     * The number of hits, where a requested object really was in the cache
     */
    protected long hitCount = 0;
    /**
     * The number of objects currently stored in the cache
     */
    protected int size = 0;
    /**
     * The maximum number of objects that the cache can hold
     */
    protected int capacity;
    protected boolean updateTimeIfHit = false;
    private boolean _running = false;
    private int _clearingInterval = 30000;
    private int _surviveTime = 30000;

    /**
     * Creates a new cache with a given capacity.
     *
     * @param capacity the maximum number of objects this cache will hold
     */
    public MRUMap(int capacity) {
        setCapacity(capacity);
    }

    /**
     * A small sample program for testing this class.
     */
    public static void main(String[] args) throws Exception {
        final MRUMap cache = new MRUMap(4);
        System.out.println(cache);
        cache.put("a", "Anton");
        cache.put("b", "Bohnen");
        cache.put("c", "Cache");
        System.out.println(cache);
        cache.get("d");
        cache.get("c");
        cache.put("d", "Dieter");
        cache.put("e", "Egon");
        cache.put("f", "Frank");
        cache.get("c");
        System.out.println(cache);
        cache.setPeriodicClear(true, 2000, 20, null);

        TimeUnit.MINUTES.sleep(1);
    }

    /**
     * Clears the cache by removing all entries from the cache
     */
    public void clear() {
        Lock readLock = readWriteLock.writeLock();
        try {
            readLock.lock();
            lookupIndex.clear();
            size = 0;
            mru = lru = null;
        } finally {
            readLock.unlock();
        }
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    /**
     * Returns an object from the cache for the given key, or null if there
     * currently is no object in the cache with this key.
     *
     * @param key the key for the object you want to get from this cache
     * @return the cached object, or null
     */
    public O get(K key) {

        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            requests++;
            final CacheEntry<K, O> found = getInternal(key);
            if (found != null) {
                hitCount++;
                found.addHit();
                return found.object;
            } else {
                return null;
            }
        } finally {
            readLock.unlock();
        }
    }

    public O getOrCreate(K key, Function<K, O> creator) {
        O result = get(key);
        if (result == null) {
            result = creator.apply(key);
            put(key, result);
        }
        return result;
    }

    public O getOrCreate(K key, Supplier<O> creator) {
        O result = get(key);
        if (result == null) {
            result = creator.get();
            put(key, result);
        }
        return result;
    }

    /**
     * Returns the capacity of this cache. This is the maximum number of objects
     * this cache will hold at a time.
     *
     * @return the capacity of this cache
     */
    public int getCapacity() {
        return capacity;
    }

    //  public CacheEntry<K, O>[] getCacheEntries() {
    // return (CacheEntry[]) ArrayUtil.toGenericArray(lookupIndex.values());
    //    return lookupIndex.values();
    //   }

    /**
     * Changes the capacity of this cache. This is the maximum number of objects
     * that will be cached at a time. If the new capacity is smaller than the
     * current number of objects in the cache, the least recently used objects will
     * be removed from the cache.
     *
     * @param capacity the maximum number of objects this cache will hold
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            System.out.println("cache capacity must be >= 0");
        }

        while (size > capacity) {
            remove(lru.key);
        }
        this.capacity = capacity;
    }

    /**
     * Returns the number of objects currently cached.
     *
     * @return the number of objects currently cached
     */
    public int getCurrentSize() {
        return size;
    }

    public Map getDelegate() {
        return lookupIndex;
    }

    /**
     * Returns the fill rate of this cache. This is the current number of objects in
     * the cache diveded by its capacity.
     *
     * @return the fill rate of this cache as double value
     */
    public double getFillRate() {
        return ((capacity == 0) ? 1.0 : ((double) size / (double) capacity));
    }

    /**
     * Returns the hit rate of this cache. This is the number of successful hits
     * divided by the total number of get requests so far. Using this ratio can help
     * finding the appropriate cache capacity.
     *
     * @return the hit rate of this cache as double value
     */
    public double getHitRate() {
        return ((requests == 0) ? 1.0 : ((double) hitCount / (double) requests));
    }

    public JSONObject getInfo() {
        final JSONObject info = new JSONObject();
        info.put("capacity", capacity);
        info.put("size", size);
        info.put("fillRate", getFillRate());
        info.put("hitRate", getHitRate());
        info.put("cleanupThread", isRunning());
        info.put("clearingInterval", _clearingInterval);
        info.put("surviveTime", getSurviveTime());
        return info;
    }

    public CacheEntry<K, O> getInternal(K key) {
        if (!lookupIndex.containsKey(key)) {
            return null;
        }
        final CacheEntry<K, O> found = (lookupIndex.get(key));
        if (found != mru) {
            found.after.before = found.before;
            if (found == lru) {
                lru = found.after;
            } else {
                found.before.after = found.after;
            }
            found.after = null;
            found.before = mru;
            mru.after = found;
            mru = found;
        }
        return found;
    }


    public Collection<CacheEntry<K, O>> getKeyEntriesCollection() {
        return lookupIndex.values();
    }

    public CacheEntry<K, O>[] getKeyObjects() {
        return (CacheEntry<K, O>[]) lookupIndex.values().toArray();
    }

    public int getSizeInByte() {
        int size = 0;
        if (lru != null) {
            CacheEntry entry = lru;
            while (entry != null) {
                if (entry != null) {
                    final Object o = entry.object;
                    if (o instanceof byte[]) {
                        size += ((byte[]) o).length;
                    } else {
                        return -1;
                    }
                    entry = entry.after;
                } else {
                    break;
                }
            }
        }
        return size;
    }

    public int getClearingInterval() {
        return _clearingInterval;
    }

    public int getSurviveTime() {
        return _surviveTime;
    }

    /**
     * Returns true if this cache is empty.
     *
     * @return true if this cache is empty
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Returns true if this cache is full.
     *
     * @return true if this cache is full
     */
    public boolean isFull() {
        return (size == capacity);
    }

    public boolean isRunning() {
        return _running;
    }

    public boolean isUpdateTimeIfHit() {
        return updateTimeIfHit;
    }

    public void setUpdateTimeIfHit(boolean updateTimeIfHit) {
        this.updateTimeIfHit = updateTimeIfHit;
    }

    public Set<K> keySet() {
        return lookupIndex.keySet();
    }

    /**
     * Puts an object into the cache, storing it under the given key. If the cache
     * is already full, the least recently used object will be removed from the
     * cache first. If the cache already contains an entry under the key provided,
     * this entry is replaced.
     *
     * @param key the non-null key to store the object under
     * @param obj the non-null object to be put into the cache
     */
    public void put(K key, O obj) {
        Lock readLock = readWriteLock.writeLock();
        try {
            readLock.lock();
            if (capacity == 0) {
                return;
            }
            if (lookupIndex.containsKey(key)) {
                remove(key);
            }
            if (isFull()) {
                remove(lru.key);
            }
            final CacheEntry<K, O> added = new CacheEntry();
            added.object = obj;
            added.key = key;
            added.time = System.currentTimeMillis();
            lookupIndex.put(key, added);
            if (isEmpty()) {
                lru = mru = added;
            } else {
                added.before = mru;
                mru.after = added;
            }
            size++;
            mru = added;
        } finally {
            readLock.unlock();
        }
    }

    public void put(K key, O obj, T context) {
        put(key, obj, context, null, -1);
    }

    public void put(K key, O obj, T context, S subCtx) {
        put(key, obj, context, subCtx, -1);
    }

    public void put(K key, O obj, T context, S subCtx, int surviveTimeMS) {
        put(key, obj, context, subCtx, surviveTimeMS, -1);
    }

    public void traverse(BiFunction<K, O, Void> processor) {
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            lookupIndex.values().forEach(entry -> {
                processor.apply(entry.key, entry.object);
            });
        } finally {
            readLock.unlock();
        }
    }

    public void put(K key, O obj, T context, S subCtx, int surviveTimeMS, int ttl) {
        Lock readLock = readWriteLock.writeLock();
        try {
            readLock.lock();
            if (capacity == 0) {
                return;
            }
            if (lookupIndex.containsKey(key)) {
                remove(key);
            }
            if (isFull()) {
                remove(lru.key);
            }
            final CacheEntry<K, O> added = new CacheEntry();
            added.object = obj;
            added.key = key;
            added.context = context;
            added.subContext = subCtx;
            added.surviveTime = surviveTimeMS;
            added.ttl = ttl;
            lookupIndex.put(key, added);
            if (isEmpty()) {
                lru = mru = added;
            } else {
                added.before = mru;
                mru.after = added;
            }
            size++;
            mru = added;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Removes an object from the cache for the given key.
     *
     * @param key the key for the object you want to remove from this cache
     */
    public void remove(K key) {
        Lock readLock = readWriteLock.writeLock();
        try {
            readLock.lock();
            if (!lookupIndex.containsKey(key)) {
                return;
            }
            final CacheEntry<K, O> removed = lookupIndex.get(key);
            if (removed == lru) {
                lru = removed.after;
            } else {
                removed.before.after = removed.after;
            }
            if (removed == mru) {
                mru = removed.before;
            } else {
                removed.after.before = removed.before;
            }
            removed.object = null;
            removed.key = null;
            removed.time = 0;
            removed.before = null;
            removed.after = null;
            lookupIndex.remove(key);
            size--;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns an object from the cache for the given key, but only if the cache
     * entry is not older than the given timestamp. If there currently is no object
     * in the cache with this key, null is returned. If the cache entry is older
     * than the timestamp, the entry is removed from the cache and null is returned.
     *
     * @param key  the key for the object you want to get from this cache
     * @param time the timestamp to check that the cache entry is up to date
     * @return the cached object, or null
     */
    public Object returnIfUpToDat(K key, long time) {
        final Object value = get(key);
        if (value == null) {
            return null;
        }
        final CacheEntry<K, O> found = (lookupIndex.get(key));
        if (found.time >= time) {
            return value;
        }
        remove(key);
        return null;
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {
                wait(_clearingInterval);
                if (isEmpty()) {
                } else if (_surviveTime == 0) {
                    clear();
                } else {

                    Lock readLock = readWriteLock.writeLock();
                    try {
                        readLock.lock();
                        final long now = System.currentTimeMillis();
                        final Iterator<Map.Entry<K, CacheEntry<K, O>>> entries = lookupIndex.entrySet().iterator();
                        while (entries.hasNext()) {
                            final CacheEntry<K, O> aEntry = entries.next().getValue();
                            final long duration = now - aEntry.getTime();
                            final long ttl = aEntry.getTtl();
                            final long ttlNow = now - aEntry.createdTime;
                            int sTime = _surviveTime;
                            if (aEntry.surviveTime != -1) {
                                sTime = aEntry.surviveTime;
                            }
                            if (duration > sTime) {
                                remove((K) aEntry.getKey());
                            } else if (ttl > 0 && ttlNow >= aEntry.getTtl()) {
                                remove((K) aEntry.getKey());
                            }
                        }
                    } finally {
                        readLock.unlock();
                    }
                }
            } catch (final Exception e) {
            }
        }
    }

    public void setPeriodicClear(boolean removePeriodic, int interval, int surviveTime) {
        setPeriodicClear(removePeriodic, interval, surviveTime, null);
    }

    public void setPeriodicClear(boolean removePeriodic, int interval, int surviveTime, String threadName) {
        if (surviveTime <= 0 || interval <= 0) {
            return;
        }
        final boolean wasRunning = _running;
        _running = removePeriodic;
        _clearingInterval = interval;
        _surviveTime = surviveTime;
        if (!wasRunning) {
            final Thread t = new Thread(this);
            if (threadName != null) {
                t.setName(threadName);
            } else {
                t.setName("MRU Periodic clear");
            }
            t.start();
        }
    }

    public void stopPeriodicClear() {
        _running = false;
    }

    /**
     * Returns a String containing information about cache capacity, size, current
     * fill rate and hit rate. Useful for testing and debugging.
     */
    @Override
    public String toString() {
        String sb = "Total Capacity:" + capacity + "\n" +
                " Actual size:      " + size + "\n" +
                " Actual fill rate: " + getFillRate() + "\n" +
                " Actual hit rate:  " + getHitRate() +
                " Cache cleanup thread:" + isRunning() + "\n" +
                " ClearingInterval:" + _clearingInterval + "ms.\n" +
                " ObjectSurviveTime:" + getSurviveTime() + "ms. \n";
        return sb;
    }

    /**
     * For each object in the cache, there is one MCRCacheEntry object encapsulating
     * it. The cache uses a double-linked list of MCRCacheEntries and holds
     * references to the most and least recently used entry.
     */
    public class CacheEntry<K, O> {
        /**
         * The entry before this one, more often used than this entry
         */
        CacheEntry<K, O> before;
        /**
         * The entry after this one, less often used than this entry
         */
        CacheEntry<K, O> after;
        /**
         * The key for this object, to be used for removing the object
         */
        K key;
        /** The timestamp when this object was placed in the cache */
        /**
         * The stored object encapsulated by this entry
         */
        O object;
        /**
         * The context of the cached object
         */
        T context;
        S subContext;
        int surviveTime = -1;
        long createdTime = System.currentTimeMillis();
        long time = createdTime;
        int ttl = -1;
        int hits = 0;

        public void addHit() {
            if (updateTimeIfHit) {
                time = System.currentTimeMillis();
            }
            hits++;
        }

        public T getContext() {
            return context;
        }

        public long getCreatedTime() {
            return createdTime;
        }

        public int getHits() {
            return hits;
        }

        public String getInfo() {
            return " Ctx:" + (context != null ? context : "n.s.") + " subCtx:"
                    + (subContext != null ? subContext : "n.s.") + " hits:" + getHits() + " ("
                    + (getSurviveTime() >= 0 ? (getRipTime() > 0 ? (getRipTime() / 1000) + "'s" : "to be killed")
                    : "&infin;")
                    + ") created at:" + DateFormatter.toIsoTimestampString(createdTime) + " time to live:" + (ttl / 1000)
                    + "'s ";
        }

        public Object getKey() {
            return key;
        }

        public long getLiveTime() {
            return System.currentTimeMillis() - createdTime;
        }

        public long getRipTime() {
            if (surviveTime > 0) {
                return surviveTime - (System.currentTimeMillis() - getTime());
            } else {
                return surviveTime;
            }
        }


        public S getSubContext() {
            return subContext;
        }

        public int getSurviveTime() {
            return surviveTime;
        }

        public long getTime() {
            return time;
        }

        public int getTtl() {
            return ttl;
        }

        public Object getValue() {
            return object;
        }
    }
}
