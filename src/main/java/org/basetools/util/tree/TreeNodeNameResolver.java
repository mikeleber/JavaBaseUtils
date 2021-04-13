package org.basetools.util.tree;

public interface TreeNodeNameResolver<T> {
    public String getName(T object);

    public String getValue(T object);
}
