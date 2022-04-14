package org.basetools.util;

public class ObjectUtil {
    public static <T> T getNotNull(T first, T second) {
        return first != null ? first : second;
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
