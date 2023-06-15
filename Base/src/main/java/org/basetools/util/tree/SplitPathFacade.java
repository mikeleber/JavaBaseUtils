package org.basetools.util.tree;

import org.basetools.util.tree.NodePathFacade;

import java.util.function.Function;

public class SplitPathFacade<T> implements NodePathFacade<T> {
    public SplitPathFacade(Function<T, String[]> splitter) {
        _function = splitter;
    }

    private Function<T, String[]> _function;

    @Override
    public String[] buildPath(T o) {
        return _function.apply(o);
    }
}
