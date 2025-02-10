package org.basetools.util.map;

import org.basetools.util.reflection.Reflecter;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class MapMap<K, K2, V> {
    private ReadWriteLock _readWriteLock = new ReentrantReadWriteLock();
    private Map<K, Map<K2, V>> _holder;

    public MapMap(Map<K, Map<K2, V>> impl) {
        Objects.requireNonNull(impl);
        _holder = impl;
    }

    public MapMap() {
        _holder = new HashMap<>();
    }

    public V put(K key, K2 key2, V value) {
        Lock lock = _readWriteLock.writeLock();
        try {
            lock.lock();
            Map<K2, V> innerMap = _holder.get(key);
            if (innerMap == null) {
                innerMap = Reflecter.on(_holder).create().get();
            }
            _holder.put(key, innerMap);
            return innerMap.put(key2, value);
        } finally {
            lock.unlock();
        }
    }


    public void clear() {
        Lock lock = _readWriteLock.writeLock();
        lock.lock();
        _holder.clear();
        lock.unlock();
    }

    public void clear(K key) {
        Lock lock = _readWriteLock.writeLock();
        lock.lock();
        Map inner = _holder.get(key);
        if (inner != null) inner.clear();
        lock.unlock();
    }

    public V remove(K key, K2 key2) {
        Lock lock = _readWriteLock.writeLock();
        try {
            lock.lock();
            Map<K2, V> inner = _holder.get(key);
            if (inner == null) return null;
            else
                return inner.remove(key2);
        } finally {
            lock.unlock();
        }

    }

    public Map<K2, V> remove(K key) {
        Lock lock = _readWriteLock.writeLock();
        try {
            lock.lock();
            return _holder.remove(key);
        } finally {
            lock.unlock();
        }

    }

    public Map<K2, V> get(Object key) {
        Lock lock = _readWriteLock.readLock();
        try {
            lock.lock();
            return _holder.get(key);
        } finally {
            lock.unlock();
        }
    }

    public Collection<V> allValues() {
        Lock lock = _readWriteLock.readLock();
        try {
            lock.lock();
            ArrayList<V> allV = new ArrayList<>(_holder.size());
            _holder.values().forEach(inner -> inner.values().forEach(val -> allV.add(val)));
            return allV;
        } finally {
            lock.unlock();
        }
    }

    public void forEach(Consumer<? super V> action) {
        Objects.requireNonNull(action);
        Lock lock = _readWriteLock.readLock();
        try {
            lock.lock();
            ArrayList<V> allV = new ArrayList<>(_holder.size());
            _holder.values().forEach(inner -> inner.values().forEach(val -> action.accept(val)));
        } finally {
            lock.unlock();
        }
    }

    public V get(Object key, Object key2) {
        Lock lock = _readWriteLock.readLock();
        try {
            lock.lock();
            Map<K2, V> inner = _holder.get(key);
            if (inner == null) return null;
            else return inner.get(key2);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return _holder.size();
    }
}