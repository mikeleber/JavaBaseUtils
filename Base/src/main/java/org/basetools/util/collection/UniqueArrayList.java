package org.basetools.util.collection;

import java.util.ArrayList;
import java.util.Collection;

public class UniqueArrayList<T> extends ArrayList<T> {

    public UniqueArrayList() {
        super();
    }

    public UniqueArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public UniqueArrayList(Collection<? extends T> c) {
        super();
        addAll(c);
    }

    @Override
    public boolean add(T t) {
        if (contains(t))
            return false;
        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean res = false;
        for (T e : c) {
            res |= add(e);
        }
        return res;
    }

    @Override
    public void add(int index, T element) {
        if (!contains(element))
            super.add(index, element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        ArrayList<T> toAdd = new ArrayList<>();
        for (T e : c) {
            if (!contains(e))
                toAdd.add(e);
        }
        return super.addAll(index, toAdd);
    }
} 