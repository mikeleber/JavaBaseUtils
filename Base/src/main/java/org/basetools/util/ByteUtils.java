package org.basetools.util;

public class ByteUtils {
    public static byte toByte(boolean val) {
        return (byte) (val ? 1 : 0);
    }

    public static byte toByte(int val) {
        return (byte) (val == 1 ? 1 : 0);
    }
}
