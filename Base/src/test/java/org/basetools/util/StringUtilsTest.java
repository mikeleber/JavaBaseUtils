package org.basetools.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void toTokens() {
        String[] result = StringUtils.toTokens( ":89_2-ADD");
        String[] result1 = StringUtils.toTokens( "DOM2:89_2-ADD");
        String[] result2 = StringUtils.toTokens( "DOM2:89-ADD");
        String[] result3 = StringUtils.toTokens( "DOM2:89_2");
        String[] result4 = StringUtils.toTokens( "DOM2");
        String[] result5 = StringUtils.toTokens( ":89");
        String[] result6 = StringUtils.toTokens( "_2");
        String[] result7 = StringUtils.toTokens( "-ADD");
        System.out.println(result);
        System.out.println(result1);
        System.out.println(result2);

    }
}