package org.basetools.util.collection;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentList<T> implements List<T> {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final List<T> list;

    public ConcurrentList(List<T> list) {
        this.list = list;
    }

    public int size() {
        readWriteLock.readLock().lock();
        try {
            return list.size();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public boolean isEmpty() {
        readWriteLock.readLock().lock();
        try {
            return list.isEmpty();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public boolean contains(Object o) {
        readWriteLock.readLock().lock();
        try {
            return list.contains(o);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public Iterator<T> iterator() {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<T>(list).iterator();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Object[] toArray() {
        readWriteLock.readLock().lock();
        try {
            return list.toArray();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        readWriteLock.readLock().lock();
        try {
            return list.toArray(a);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public boolean add(T t) {
        readWriteLock.writeLock().lock();
        boolean ret;
        try {
            ret = list.add(t);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return ret;
    }

    public boolean remove(Object o) {
        readWriteLock.writeLock().lock();
        boolean ret;
        try {
            ret = list.remove(o);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        readWriteLock.readLock().lock();
        try {
            return list.containsAll(c);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        readWriteLock.readLock().lock();
        try {
            return list.addAll(c);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        readWriteLock.readLock().lock();
        try {
            return list.addAll(index,c);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        readWriteLock.readLock().lock();
        try {
            return list.removeAll(c);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        readWriteLock.readLock().lock();
        try {
           return list.retainAll(c);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void clear() {
        readWriteLock.writeLock().lock();
        try {
            list.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public T get(int index) {
        readWriteLock.readLock().lock();
        try {
            return list.get(index);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public T set(int index, T element) {
        readWriteLock.readLock().lock();
        try {
           return  list.set(index,element);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void add(int index, T element) {
        readWriteLock.readLock().lock();
        try {
             list.add(index,element);
        } finally {
            readWriteLock.readLock().unlock();
        }

    }

    @Override
    public T remove(int index) {
        readWriteLock.readLock().lock();
        try {
            return list.remove(index);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public int indexOf(Object o) {
        readWriteLock.readLock().lock();
        try {
            return list.indexOf(o);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        readWriteLock.readLock().lock();
        try {
            return list.lastIndexOf(0);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public ListIterator<T> listIterator() {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<T>(list).listIterator();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<T>(list).listIterator(index);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        readWriteLock.readLock().lock();
        try
        {
            return list.subList(fromIndex,toIndex);
        }
        finally
        {
            readWriteLock.readLock().unlock();
        }
    }
//etc
}