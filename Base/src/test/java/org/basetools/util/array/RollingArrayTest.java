package org.basetools.util.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * RollingArray Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Feb. 18, 2021</pre>
 */
public class RollingArrayTest {

    @Test
    void addNode() {
        RollingArray q = new RollingArray(String.class, 10);

        q.push("1");
        System.out.println(q.toString());
        q.push("22");
        System.out.println(q.toString());
        q.push("3");
        System.out.println(q.toString());
        q.push("44");
        System.out.println(q.toString());
        q.push("5");
        System.out.println(q.toString());
        q.push("66");
        System.out.println(q.toString());

        StringBuffer sb = new StringBuffer();
        while (!q.isEmpty()) {
            sb.append(q.pop());
        }
        Assertions.assertEquals(0, q.size());
    }

    @Test
    void testTraverse() {
        RollingArray q = new RollingArray(String.class, 5);

        q.push("1");
        System.out.println(q.toString());
        q.push("22");
        System.out.println(q.toString());
        q.push("3");
        System.out.println(q.toString());
        q.push("44");
        System.out.println(q.toString());
        q.push("5");
        System.out.println(q.toString());
        q.push("66");
        System.out.println(q.toString());

        q.traverse(a -> System.out.println(a));
    }
}
