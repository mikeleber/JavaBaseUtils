package org.basetools.util;

import java.util.Arrays;
import java.util.function.Predicate;

public class StreamUtils {
    @SuppressWarnings("unchecked")
    public static <R> Predicate<R> or(Predicate<R>... predicates) {
        return r -> Arrays.stream(predicates).anyMatch(p -> p.test(r));
    }
}
