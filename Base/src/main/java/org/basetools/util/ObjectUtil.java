package org.basetools.util;

public class ObjectUtil {
    public static <T> T getNotNull(T first, T second) {
        return first != null ? first : second;
    }
}
