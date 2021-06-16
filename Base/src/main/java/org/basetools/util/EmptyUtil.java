package org.basetools.util;

import java.util.Collection;
import java.util.Map;

public class EmptyUtil {
    public static final boolean isEmpty(String x) {
        return (x == null || x.trim().equals(""));
    }
    public static final boolean isEmpty(Integer x) {
        return (x == null || x == 0);
    }
    public static final boolean isEmpty(Long x) {
        return (x == null || x == 0);
    }
    public static final boolean isEmpty(Collection<?> x) {
        return (x == null || x.size() == 0);
    }
    public static final boolean isEmpty(Map<?,?> x) {
        return (x == null || x.size() == 0);
    }
    public static final boolean isEmpty(Object[] x) {
        return (x == null || x.length == 0);
    }
    public static boolean isEmpty(CharSequence cs) {return cs == null || cs.length() == 0; }
}
