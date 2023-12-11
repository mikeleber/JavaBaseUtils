package org.basetools.util.map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IndexedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    private final Map<Integer, K> _index2Key = new LinkedHashMap<>();
    private final Map<K, Integer> _key2index = new LinkedHashMap<>();
    private int _currentpos = 0;
    private ReadWriteLock _readWriteLock = new ReentrantReadWriteLock();
    ;

    @Override
    public V put(K key, V value) {
        Lock lock = _readWriteLock.writeLock();
        try {
            lock.lock();
            putToIndex(key);
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    private void putToIndex(K key) {
        _index2Key.put(_currentpos, key);
        _key2index.put(key, _currentpos);
        _currentpos++;
    }

    @Override
    public void clear() {
        Lock lock = _readWriteLock.writeLock();
        lock.lock();
        super.clear();
        _currentpos = 0;
        _index2Key.clear();
        _key2index.clear();
        lock.unlock();
    }

    @Override
    public V remove(Object key) {
        Lock lock = _readWriteLock.writeLock();
        try {
            lock.lock();
            V removed = super.remove(key);
            //recalculate indexes
            _currentpos = 0;
            _index2Key.clear();
            _key2index.clear();
            keySet().forEach(keyId -> putToIndex(keyId));
            return removed;
        } finally {
            lock.unlock();
        }

    }


    public V getValueIndex(int index) {
        Lock lock = _readWriteLock.readLock();
        try {
            lock.lock();
            return get(_index2Key.get(index));
        } finally {
            lock.unlock();
        }

    }

    @Override
    public V get(Object key) {
        Lock lock = _readWriteLock.readLock();
        try {
            lock.lock();
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    public Integer getKeyIndex(K key) {
        Lock lock = _readWriteLock.readLock();
        try {
            lock.lock();
            return _key2index.get(key);
        } finally {
            lock.unlock();
        }
    }
}