package org.basetools.util;

import org.junit.jupiter.api.Test;

class ByteUtilsTest {

    @Test
    void bitwiseSubstract() {
        int n1248163264 = 1 | 2 | 4 | 8 | 16 | 32 | 64 | 128;

        System.out.println("    " + ByteUtils.intToByteString(n1248163264, 2));
        //System.out.println("  " + ByteUtils.intToByteString(cn1248163264, 2));
        System.out.println("1=  " + ByteUtils.intToByteString(ByteUtils.bitwiseSubstract(n1248163264, -1 * (-1)), 2));
        System.out.println("2=  " + ByteUtils.intToByteString(ByteUtils.bitwiseSubstract(n1248163264, -1 * (-2)), 2));
        System.out.println("4=  " + ByteUtils.intToByteString(ByteUtils.bitwiseSubstract(n1248163264, -1 * (-4)), 2));
        System.out.println("8=  " + ByteUtils.intToByteString(ByteUtils.bitwiseSubstract(n1248163264, -1 * (-8)), 2));
        System.out.println("16= " + ByteUtils.intToByteString(ByteUtils.bitwiseSubstract(n1248163264, -1 * (-16)), 2));
        System.out.println("16= " + ByteUtils.intToByteString(ByteUtils.bitwiseSubstract(n1248163264, Math.abs(-16)), 2));
        System.out.println("-----------------------------------");


    }
}