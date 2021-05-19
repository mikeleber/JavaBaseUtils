package org.basetools.util;

import org.basetools.util.cache.Cache;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * This is a simple implementation of an Map to measure timings of different kinds.
 * It make use of DoubleSummaryStatistics to calculate min,max,average and an optional info field.
 */
public class TimingTable<I> extends Cache<String, Statistics<I>> {
    public TimingTable(LinkedHashMap psCacheMap, int size) {
        super(psCacheMap, size);
    }

    /**
     * @param size Max size of the table. If the size exeeds maxSize, the first accessed entry will be removed.
     */
    public TimingTable(int size) {
        super(new LinkedHashMap(size), size);
    }

    /**
     * Add a timing to the map.
     *
     * @param key
     * @param duration
     * @param info     An optional info field to provide further information.
     */
    public void addTiming(String key, double duration, I info) {
        Lock writeLock = getReadWriteLock().writeLock();
        writeLock.lock();
        //ignore negative or 0 durations
        if (duration > 0) {
            Statistics current = get(key);
            if (current == null) {
                current = new Statistics();
                current.accept(duration, info);
                put(key, current);
            } else {
                current.accept(duration, info);
            }
        }
        writeLock.unlock();
    }

    /**
     * Returns a Statistics object for the given key. It returns null if not existent.
     *
     * @param key
     * @return
     */
    public Statistics getTiming(String key) {
        Statistics current = get(key);
        return current;
    }

    /**
     * @return
     */
    public JsonObjectBuilder getTimings() {
        JsonObjectBuilder result = Json.createObjectBuilder();
        Lock readLock = getReadWriteLock().readLock();
        try {
            readLock.lock();
            Iterator<Map.Entry<String, Statistics<I>>> it = getCache().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Statistics<I>> entry = it.next();
                result.add(entry.getKey(), entry.getValue().toJSON());
            }
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public void clear() {
        super.clear();
    }

    /**
     * Returns the number of timing entries
     *
     * @return the number of timing entries
     */
    public int size() {
        return getCache().size();
    }
}
