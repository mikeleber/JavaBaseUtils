package org.basetools.util.tree.xpath;

import java.util.NoSuchElementException;

public class SingleObjectIteratorTree<E> extends HandledTreeNodeIterator implements ResetIterator<E> {
    private E object;
    private boolean seen;

    public SingleObjectIteratorTree(E object) {
        super(null);
        this.object = object;
        seen = false;
    }

    public SingleObjectIteratorTree(E object, XPathTreeNodeHandler handler) {
        super(handler);
        this.object = object;

        seen = false;
    }

    @Override
    public boolean hasNext() {
        return !seen;
    }

    @Override
    public E next() {
        if (hasNext()) {
            seen = true;
            return object;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        seen = false;
    }

    public E getObject() {
        return object;
    }
}
