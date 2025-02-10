package org.basetools.util.collection;

import java.util.LinkedHashSet;
import java.util.Stack;
import java.util.stream.Stream;

public class UniqueStack<E> extends LinkedHashSet<E> {
    private boolean _keepFirst;
    private Stack<E> stack = new Stack<>();
    private int size = 0;

    public UniqueStack() {
    }

    public UniqueStack(boolean keepFirst) {
        _keepFirst = keepFirst;
    }

    public Stream<E> stream() {
        return stack.stream();
    }

    public boolean push(E e) {

        if (!contains(e)) {
            ++size;
            stack.push(e);
            return add(e);
        } else if (!_keepFirst) {
            stack.removeElementAt(stack.indexOf(e));
            remove(e);
            stack.push(e);
            return add(e);
        }
        return false;
    }

    public E pop() {
        E val = null;
        if (!stack.isEmpty()) {
            --size;
            val = stack.pop();
            remove(val);
        }
        return val;
    }
}