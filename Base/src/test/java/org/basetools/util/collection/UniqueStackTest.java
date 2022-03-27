package org.basetools.util.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UniqueStackTest {

    @Test
    void pushUnique() {
        UniqueStack stack = new UniqueStack();
        stack.push("a");
        stack.push("b");
        stack.push("c");
        stack.push("a");
        Assertions.assertEquals(3, stack.size());
        Assertions.assertEquals("a", stack.pop());
        Assertions.assertEquals("c", stack.pop());
        Assertions.assertEquals("b", stack.pop());
    }
}