package org.basetools.util.collection.result;

import java.util.*;

public class ResultList<E> implements List<E>, Cloneable {
    private List<E> dataHolder;

    public ResultList() {
        dataHolder = new ArrayList();
    }

    public ResultList(int initialCapacity) {
        dataHolder = new ArrayList<>(initialCapacity);
    }

    public ResultList(Collection c) {
        dataHolder = new ArrayList(c);
    }

    public List getDataHolder() {
        return dataHolder;
    }

    public List<E> unmodifiable() {
        dataHolder = Collections.unmodifiableList(dataHolder);
        return this;
    }

    @Override
    public int size() {
        return dataHolder.size();
    }

    @Override
    public boolean add(E o) {
        return dataHolder.add(o);
    }

    @Override
    public void add(int index, E element) {
        dataHolder.add(index, element);
    }

    @Override
    public boolean addAll(Collection c) {
        return dataHolder.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return dataHolder.addAll(index, c);
    }

    @Override
    public void clear() {
        dataHolder.clear();
    }

    @Override
    public boolean contains(Object o) {
        return dataHolder.contains(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        return dataHolder.containsAll(c);
    }

    @Override
    public E get(int index) {
        return dataHolder.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return dataHolder.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return dataHolder.isEmpty();
    }

    @Override
    public Iterator iterator() {
        return dataHolder.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return dataHolder.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return dataHolder.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return dataHolder.listIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        return dataHolder.remove(o);
    }

    public void remove(int from, int to) {
        if (to == -1) {
            to = size() - 1;
        }
        if (from == -1) {
            from = 0;
        }
        for (; to >= from; to--) {
            remove(to);
        }
    }

    @Override
    public E remove(int index) {
        return dataHolder.remove(index);
    }

    @Override
    public boolean removeAll(Collection c) {
        return dataHolder.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return dataHolder.retainAll(c);
    }

    @Override
    public E set(int index, E element) {
        return dataHolder.set(index, element);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return dataHolder.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return dataHolder.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return dataHolder.toArray(a);
    }

    public void makeImmutable() {
        dataHolder = (List<E>) Collections.singletonList(dataHolder);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ResultList res = (ResultList) super.clone();
        res.dataHolder = new ArrayList(dataHolder);
        return res;
    }

    public void trimToSize() {
        if (dataHolder instanceof ArrayList) {
            ((ArrayList) dataHolder).trimToSize();
        }
    }
}
