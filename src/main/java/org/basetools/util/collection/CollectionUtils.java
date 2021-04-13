package org.basetools.util.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {

    /**
     * Wraps the stream map function to create a new converted list using function func.
     *
     * @param from
     * @param func
     * @return a Converted List
     */
    public static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    public static <T> List<T> removeDuplicates(List<T> aList) {
        if (aList == null) {
            return null;
        }
        if (aList.size() <= 1) {
            return aList;
        }
        HashSet<T> h = new HashSet<>(aList);
        if (h.size() != aList.size()) {
            aList.clear();
            aList.addAll(h);
        }
        return aList;
    }

    public static <E> void swap(List<E> a, int i, int j) {
        E tmp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, tmp);
    }

    public static <T> T getFirst(Collection<T> elements) {
        if (elements != null && !elements.isEmpty()) {
            return elements.iterator().next();
        } else {
            return null;
        }
    }
}
