package org.basetools.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {
    @SuppressWarnings("unchecked")
    public static <R> Predicate<R> or(Predicate<R>... predicates) {
        return r -> Arrays.stream(predicates).anyMatch(p -> p.test(r));
    }

    public static <T> Stream<List<T>> chunked(Stream<T> stream, int chunkSize) {
        AtomicInteger index = new AtomicInteger(0);
        return stream.collect(Collectors.groupingBy(x -> index.getAndIncrement() / chunkSize))
                .entrySet().stream()
                .filter(aEntry ->
                        {
                            if (aEntry == null) throw new RuntimeException("Tombstone received");
                            return true;
                        }
                ).sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue);
    }
}
