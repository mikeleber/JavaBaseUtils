package org.basetools.util.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArrayUtilTest {

    @Test
    void givenArrayAddElementAtPos0() {
        Object[] expected = new Object[]{"1", "2", "3", "4", "5", "6"};
        Object[] src = new Object[]{"2", "3", "4", "5", "6"};
        Object[] merged = ArrayUtil.add(Object.class, src, "1", 0);
        Assertions.assertArrayEquals(expected, merged);
    }

    @Test
    void givenArrayAddElementAtPos1() {
        Object[] expected = new Object[]{"1", "2", "3", "4", "5", "6"};
        Object[] src = new Object[]{"1", "3", "4", "5", "6"};
        Object[] merged = ArrayUtil.add(Object.class, src, "2", 1);
        Assertions.assertArrayEquals(expected, merged);
    }

    @Test
    void intersectionWithAll() {
        Object[] expected = new Object[]{"1",  "4", "5"};
        Object[] src = new Object[]{"1", "2", "4", "5", "6"};
        Object[] src2 = new Object[]{"1", "3", "4", "5", "7"};
        Object[] merged = ArrayUtil.intersection(src, src2, String.class);
        Assertions.assertArrayEquals(expected, merged);
    }

    @Test
    void intersectionWithNone() {
        Object[] expected = new Object[0];
        Object[] src = new Object[]{"1", "3", "4", "5", "6"};
        Object[] src2 = new Object[]{"7", "8", "9"};
        Object[] merged = ArrayUtil.intersection(src, src2, String.class);
        Assertions.assertArrayEquals(expected, merged);
    }

    @Test
    void intersectionWith567() {
        Object[] expected = new Object[]{"5", "6", "7"};
        Object[] src = new Object[]{"1", "3", "4", "5", "6", "7"};
        Object[] src2 = new Object[]{"5", "6", "7", "8", "9"};
        Object[] merged = ArrayUtil.intersection(src, src2, String.class);
        Assertions.assertArrayEquals(expected, merged);
    }

    @Test
    void removeFromArray() {
        Object[] expected = new Object[]{"1", "3", "4"};
        Object[] src = new Object[]{"1", "3", "4", "5", "6", "7"};
        Object[] src2 = new Object[]{"5", "6", "7"};
        Object[] merged = ArrayUtil.removeFromArray(src, src2);
        Assertions.assertArrayEquals(expected, merged);
    }

    @Test
    void removeFromArrayNotFound() {
        Object[] expected = new Object[]{"1", "3", "4"};
        Object[] src = new Object[]{"1", "3", "4"};
        Object[] src2 = new Object[]{"5", "6", "7"};
        Object[] merged = ArrayUtil.removeFromArray(src, src2);
        Assertions.assertArrayEquals(expected, merged);
    }
}