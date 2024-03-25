package org.basetools.util.array;

import org.basetools.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The type Array util.
 */
public final class ArrayUtil {

    /**
     * Create int sequence int [ ].
     *
     * @param start  the start
     * @param length the length
     * @param step   the step
     * @return the int [ ]
     */
    public static final int[] createIntSequence(int start, int length, int step) {
        int[] result = new int[length];
        int j = start;
        int end = j + length;
        int val = start;
        for (; j < end; j++) {
            result[j] = val;
            val += step;
        }
        return result;
    }

    /**
     * Gets first.
     *
     * @param <T>   the type parameter
     * @param array the array
     * @return the first
     */
    public static final <T> T getFirst(T[] array) {
        if (array != null && array.length > 0) {
            return array[0];
        }
        return null;
    }
    public static  short[] toShortArray(String val) {
        int s = val.length();
        short[] result = new short[s];
        for (int i = 0; i < s; i++) {
            result[i] = Short.valueOf(val.substring(i, i + 1));
        }
        return result;
    }

    public static short getValueAtPos(short[] vals, int pos) {
        if (pos >= 0 && pos < vals.length) {
            return vals[pos];
        }
        return 0;
    }



    public static final <T> T get(T[][] srcArr, int comCol, Object compVal, int getCol, T defaultValue) {
        int idx = contains(srcArr, comCol, compVal);
        if (idx >= 0) {
            return get(srcArr[idx], getCol, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static final <T> int contains(T[][] array, int col, Object value) {
        return contains(array, col, value, false);
    }

    /**
     * Returns the object at the given postion. If t is out of the possible value range defaultValue will be returned.
     *
     * @param <T>          the type parameter
     * @param srcArr       the src arr
     * @param t            the t
     * @param defaultValue the default value
     * @return the t
     */
    public static final <T> T get(T[] srcArr, int t, T defaultValue) {
        if (srcArr != null && t >= 0 && t < srcArr.length) {
            return srcArr[t] != null ? srcArr[t] : defaultValue;
        }
        return defaultValue;
    }

    public static final <T> int contains(T[][] array, int col, Object value, boolean firstIDXIsRow) {
        int foundAt = -1;
        if (array == null || array.length <= 0 || value == null) {
            return foundAt;
        }
        int length = 0;
        if (firstIDXIsRow) {
            length = array.length - 1;
        } else {
            length = array[0].length - 1;
        }
        for (int r = length; r >= 0; r--) {
            if (firstIDXIsRow) {
                if (value.equals(array[r][col])) {
                    foundAt = r;
                    break;
                }
            } else {
                if (value.equals(array[col][r])) {
                    foundAt = r;
                    break;
                }
            }
        }
        return foundAt;
    }

    /**
     * Returns the value with the index t from srcArr. Returns null if index is not in range within array.
     *
     * @param <T>    the type parameter
     * @param source the source
     * @param t      index to get
     * @return t
     */
    public static final <T> T get(T[] source, int t) {
        if (source != null && t >= 0 && t < source.length) {
            return source[t];
        }
        return null;
    }

    /**
     * Gets starts with.
     *
     * @param target              the target
     * @param startsWith          the starts with
     * @param ignoreCase          the ignore case
     * @param trim                the trim
     * @param returnMatchFragment the return match fragment
     * @return the starts with
     */
    public static String getStartsWith(String[] target, String startsWith, boolean ignoreCase, boolean trim, boolean returnMatchFragment) {
        String result = null;
        if (target != null && target.length > 0) {
            for (int t = 0; t < target.length; t++) {
                if (target[t] != null) {
                    String val = target[t];
                    if (trim) {
                        val = val.trim();
                    }
                    if (StringUtils.startsWith(val, startsWith, ignoreCase)) {
                        if (returnMatchFragment) {
                            result = val.substring(startsWith.length()).trim();
                        } else {
                            result = val.trim();
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static final <V> V[] add(Class clazz, V[] array, V o, int pos) {
        if (pos > array.length) {
            pos = array.length;
        }
        int s = array.length + 1;
        V[] newArray = (V[]) Array.newInstance(clazz, s);
        System.arraycopy(array, 0, newArray, 0, pos);
        newArray[pos] = o;
        System.arraycopy(array, pos, newArray, pos + 1, array.length - pos);
        return newArray;
    }
    public static final <V> V[] add(V[] array, V element) {
        if (array == null) {
            V[] newArray = toGenericArray(element, 1);
            newArray[0] = element;
            return newArray;
        }
        final int N = array.length;
        array = Arrays.copyOf(array, N + 1);
        array[N] = element;
        return array;
    }

    public static final <T> T[] toGenericArray(T t, int size) {
        final T[] res = (T[]) Array.newInstance(t.getClass(), size);
        return res;
    }

    @SafeVarargs
    public static <T> T[] toGenericArray(T... elems) {
        return elems;
    }

    public static final Object[][] addToGrowArray(Object[][] array, Object[] o, int gf) {
        int w = o.length;
        if (array == null) {
            array = new Object[gf][w];
        }
        if (w <= gf) {
            gf = w + 1;
        }
        int s = array.length;
        int lastIdx = -1;
        if (s > 0) {
            Integer iVal = (Integer) array[s - 1][0];
            lastIdx = (iVal != null ? iVal.intValue() : -1);
            array[s - 1][0] = 0;
        }
        lastIdx++;
        if (lastIdx < s - 1) {
            array[lastIdx] = o;
            array[s - 1][0] = lastIdx;
            return array;
        } else {
            // need to expand array
            int nS = s + gf;
            Object[][] newArray = new Object[nS][w];
            System.arraycopy(array, 0, newArray, 0, s);
            newArray[lastIdx] = o;
            newArray[nS - 1][0] = lastIdx;
            return newArray;
        }
    }

    public static final Object[][] removeFromGrowArray(Object[][] array, Object key, int keyColPos, int gf) {
        if (array == null || array.length == 0) {
            return null;
        }
        int s = array.length;
        Integer iVal = (Integer) array[s - 1][0];
        int sLen = array[0].length;
        int foundPos = contains(array, keyColPos, key);
        if (foundPos != -1) {
            Object[][] newArray = new Object[s][sLen];
            if (foundPos > 0) {
                System.arraycopy(array, 0, newArray, 0, foundPos);
            }
            int copLen = iVal - foundPos;
            if (copLen > 0) {
                System.arraycopy(array, foundPos + 1, newArray, foundPos, iVal - foundPos);
            }
            newArray[newArray.length - 1][0] = iVal - 1;
            return newArray;
        }
        return array;
    }

    public static final Object[] removeFromGrowArray(Object[] array, Object key) {
        if (array == null || array.length == 0) {
            return null;
        }
        int foundPos = contains(array, key);
        if (foundPos != -1) {
            array = removeFromGrowArray(array, foundPos);
        }
        return array;
    }

    /**
     * Search within an array for a given value. This method use Object.equals() to check for equality.
     * If no value found -1 is returned
     *
     * @param array
     * @param value
     * @param <V>
     * @return the index of the object or -1 if not found
     */
    public static final <V> int contains(V[] array, V value) {
        int foundAt = -1;
        if (array == null) {
            return foundAt;
        }
        int s = array.length;
        if (s <= 0) {
            return foundAt;
        }
        int length = s - 1;
        for (int y = length; y >= 0; y--) {
            if (Objects.equals(value, array[y])) {
                foundAt = y;
                break;
            }
        }
        return foundAt;
    }

    public static final Object[] removeFromGrowArray(Object[] array, int pos) {
        if (array == null || array.length == 0) {
            return null;
        }
        int s = array.length;
        Integer iVal = (Integer) array[s - 1];
        if (pos != -1) {
            Object[] newArray = new Object[s];
            if (pos > 0) {
                System.arraycopy(array, 0, newArray, 0, pos);
            }
            int copLen = iVal - pos;
            if (copLen > 0) {
                System.arraycopy(array, pos + 1, newArray, pos, iVal - pos);
            }
            newArray[newArray.length - 1] = iVal - 1;
            return newArray;
        }
        return array;
    }

    /**
     * replace the target String with the corresponding with string if target is null or has zero length
     *
     * @param target
     * @param with
     * @return
     */
    public static Object[] merge(Object[] target, Object[] with) {
        for (int i = 0; i < target.length; i++) {
            Object ar1Obj = target[i];
            Object ar2Obj = with[i];
            if (ar1Obj == null || "".equals(ar1Obj)) {
                target[i] = ar2Obj;
            }
        }
        return target;
    }

    public static <T> T[] removeFromArray(T[] array, T[] vals) {
        if (array == null || vals == null || vals.length == 0) {
            return array;
        }
        int s = array.length;
        T[] newArray = null;
        T foundObject = null;
        boolean addValue = true;
        int foundCount = 0;
        int pos = 0;
        for (int i = 0; i < s; i++) {
            for (int v = 0; v < vals.length; v++) {
                if (array[i] == vals[v] || (vals[v] != null)) {
                    if (vals[v].equals(array[i])) {
                        // found so we have to skip!
                        foundCount++;
                        foundObject = array[i];
                        addValue = false;
                        break;
                    }
                }
            }
            if (addValue) {
                if (pos < s) {
                    foundObject = array[i];
                    if (newArray == null) {
                        newArray = toGenericArray(foundObject, s);
                    }
                    newArray[pos++] = array[i];
                }
            }
            addValue = true;
        }
        if (foundObject != null) {
            T[] result = toGenericArray(foundObject, s - foundCount);
            System.arraycopy(newArray, 0, result, 0, s - foundCount);
            return result;
        } else {
            return array;
        }
    }

    public static final Object[] addToGrowArray(Object[] array, Object o, int initSize, double factor) {
        if (factor <= 1) {
            factor = 2;
        }
        if (array == null) {
            array = new Object[initSize];
        }
        int s = array.length;
        int lastIdx = -1;
        if (s > 0) {
            Integer iVal = (Integer) array[s - 1];
            lastIdx = (iVal != null ? iVal.intValue() : -1);
            array[s - 1] = 0;
        }
        lastIdx++;
        if (lastIdx < s - 1) {
            array[lastIdx] = o;
            array[s - 1] = lastIdx;
            return array;
        } else {
            // need to expand array
            Object[] newArray = new Object[(int) (s * factor)];
            System.arraycopy(array, 0, newArray, 0, s);
            newArray[lastIdx] = o;
            newArray[newArray.length - 1] = lastIdx;
            return newArray;
        }
    }

    public static final <T> String[][] toStringArrayArray(List list, Function<T, String[]> map) {
        int s = list.size();
        String[][] result = new String[s][];
        for (int i = 0; i < s; i++) {
            result[i] = map.apply((T) list.get(i));
        }
        return result;
    }

    public static final Object[] finalizeGrowArray(Class clazz, Object[][] array) {
        if (array == null || array.length == 0) {
            return array;
        }
        int s = array.length;
        int lastIdx = -1;
        if (s > 0) {
            Object last = array[s - 1];
            Integer iVal;
            if (last instanceof Object[]) {
                iVal = ((Integer) ((Object[]) last)[0]);
            } else {
                iVal = ((Integer) last);
            }
            lastIdx = (iVal != null ? iVal.intValue() : 0) + 1;
        }
        return Arrays.copyOf(array, lastIdx, clazz);
    }

    public static final boolean compare(Object[] a1, Object[] a2) {
        if (a1 == a2) {
            return true;
        } else if (a1 == null && a2 != null) {
            return false;
        } else if (a2 == null && a1 != null) {
            return false;
        } else if (a1.length != a2.length) {
            return false;
        }
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] == a2[i]) {
            } else if (a1[i] != null && !a1[i].equals(a2[i])) {
                return false;
            } else if (a1[i] == null && a2[i] != null) {
                return false;
            }
        }
        return true;
    }

    public static List<String> removeBlankOrEmpty(String[] vals) {
        if (!ArrayUtil.isEmpty(vals)) {
            return Arrays.asList(vals).stream().filter(org.apache.commons.lang3.StringUtils::isNotBlank).collect(Collectors.toList());
        }
        return null;
    }

    public static boolean isEmpty(final Object[] array) {
        return getLength(array) == 0;
    }

    public static int getLength(final Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static boolean isEmptyOrAllNull(Object[] array) {
        if (array == null) {
            return true;
        } else if (array.length == 0) {
            return true;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                return false;
            }
        }
        return true;
    }

    public static <T> T[] intersection(T[] array1, T[] array2) {
        return intersection(array1, array2, null);
    }

    public static <T> T[] intersection(T[] array1, T[] array2, Class clazz) {
        Object[] intersection = null;
        // List<T> intersection = new ArrayList<>();
        for (int i = 0; i < array1.length; i++) {
            T a1 = array1[i];
            if (a1 != null) {
                for (int j = 0; j < array2.length; j++) {
                    T a2 = array2[j];
                    if (a1.equals(a2)) {
                        if (contains(intersection, a2) == -1) {
                            intersection = addToGrowArray(intersection, a2, 10);
                            // intersection.add(a2);
                            break;
                        }
                    }
                }
            }
        }
        if (clazz != null) {
            if (intersection == null || intersection.length == 0) {
                return (T[]) Array.newInstance(clazz, 0);
            } else {
                return (T[]) finalizeGrowArray(clazz, intersection);
            }
        } else {
            return (T[]) finalizeGrowArray(clazz, intersection);
        }
    }

    public static final Object[] addToGrowArray(Object[] array, Object o, int gf) {
        if (gf <= 1) {
            gf = 2;
        }
        if (array == null) {
            array = new Object[gf];
        }
        int s = array.length;
        int lastIdx = -1;
        if (s > 0) {
            Integer iVal = (Integer) array[s - 1];
            lastIdx = (iVal != null ? iVal.intValue() : -1);
            array[s - 1] = 0;
        }
        lastIdx++;
        if (lastIdx < s - 1) {
            array[lastIdx] = o;
            array[s - 1] = lastIdx;
            return array;
        } else {
            // need to expand array
            Object[] newArray = new Object[s + gf];
            System.arraycopy(array, 0, newArray, 0, s);
            newArray[lastIdx] = o;
            newArray[newArray.length - 1] = lastIdx;
            return newArray;
        }
    }

    /**
     * Finalize the wrow array! It removes the buffer and size information from array!
     *
     * @param array
     * @return
     */
    public static final Object[] finalizeGrowArray(Class clazz, Object[] array) {
        if (array == null || array.length == 0) {
            return array;
        }
        int s = array.length;
        int lastIdx = -1;
        if (s > 0) {
            Integer iVal = ((Integer) array[s - 1]);
            lastIdx = (iVal != null ? iVal.intValue() : 0) + 1;
        }
        Object[] newArray = (Object[]) Array.newInstance(clazz, lastIdx);
        System.arraycopy(array, 0, newArray, 0, lastIdx);
        return newArray;
    }
}
