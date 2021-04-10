package org.leber.util;

public class HashCodeUtil {

    public static final int createHashCode(String val) {
        int hash = 0;
        if (val != null) {
            hash = val.length() << 16;
        }
        // System.out.println("" + val + " :" + hash);
        return hash;
    }

    public static final int createHashCode(int contextVal, String val) {
        int hash = 0;
        if (val != null) {
            hash = val.length() << 16;
        }
        hash = hash + contextVal;
        // System.out.println("" + val + " :" + hash);
        return hash;
    }
}
