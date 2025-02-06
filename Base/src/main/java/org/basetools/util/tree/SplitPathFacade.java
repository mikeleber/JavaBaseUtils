package org.basetools.util.tree;

import java.util.function.Function;

public class SplitPathFacade<T> implements NodePathFacade<T> {
    private Function<T, String[]> _function;

    public SplitPathFacade(Function<T, String[]> splitter) {
        _function = splitter;
    }

    @Override
    public String[] buildPath(T o) {
        return _function.apply(o);
    }
}
