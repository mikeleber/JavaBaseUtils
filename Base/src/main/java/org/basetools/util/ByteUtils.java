package org.basetools.util;

public class ByteUtils {
    public static byte toByte(boolean val) {
        return (byte) (val ? 1 : 0);
    }

    public static byte toByte(Boolean val, byte defaultVal) {
        if (val == null) return defaultVal;
        else
            return (byte) (val ? 1 : 0);
    }

    public static int bitwiseSubstract(int mask, int value) {
        return mask & ~value;
    }

    public static int bitwiseAdd(int mask, int value) {
        return mask | value;
    }

    public static String intToByteString(int number, int groupSize) {
        StringBuilder result = new StringBuilder();

        for (int i = 31; i >= 0; i--) {
            int mask = 1 << i;
            result.append((number & mask) != 0 ? "1" : "0");

            if (i % groupSize == 0)
                result.append(" ");
        }
        result.replace(result.length() - 1, result.length(), "");

        return result.toString();
    }

    public static byte toByte(int val) {
        return (byte) (val == 1 ? 1 : 0);
    }

    public static final boolean matchesBinary(long what, long with) {
        return (what & with) != 0;
    }

    public static final long combineBinary(long cumulated, long with) {
        return cumulated | with;
    }

    public static final boolean matchesBinary(int what, int with) {
        return (what & with) != 0;
    }

    public static final int combineBinary(int cumulated, int with) {
        return cumulated | with;
    }

    public static final boolean matchesBinary(short what, short with) {
        return (what & with) != 0;
    }

    public static final short combineBinary(short cumulated, short with) {
        return (short) (cumulated | with);
    }

}
