package org.basetools.util.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ToListConsumer<T> implements Consumer<T> {
    private List<T> list = new ArrayList<>();
    private Predicate<T> _predicate;

    public static <T> ToListConsumer<T> toList() {
        return new ToListConsumer<T>();
    }

    public ToListConsumer withPredicate(Predicate<T> predicate) {
        _predicate = predicate;
        return this;
    }

    public void accept(T t) {
        if (_predicate != null) {
            if (_predicate.test(t)) list.add(t);
        } else
            list.add(t);
    }

    public List<T> get() {
        return list;
    }
}