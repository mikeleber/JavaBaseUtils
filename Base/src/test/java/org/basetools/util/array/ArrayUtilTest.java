package org.basetools.util.array;

import org.basetools.util.array.ArrayUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArrayUtilTest {

    @Test
    void givenArrayAddElementAtPos0() {
        Object [] expected = new Object[]{"1","2","3","4","5","6"};
        Object [] src = new Object[]{"2","3","4","5","6"};
        Object [] merged= ArrayUtil.add(Object.class,src,"1",0);
        Assertions.assertArrayEquals(expected,merged);
    }
    @Test
    void givenArrayAddElementAtPos1() {
        Object [] expected = new Object[]{"1","2","3","4","5","6"};
        Object [] src = new Object[]{"1","3","4","5","6"};
        Object [] merged=ArrayUtil.add(Object.class,src,"2",1);
        Assertions.assertArrayEquals(expected,merged);
    }
}