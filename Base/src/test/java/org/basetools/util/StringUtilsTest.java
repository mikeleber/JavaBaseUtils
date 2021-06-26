package org.basetools.util;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void toTokens() {
        String[] result = StringUtils.tokenizeSegmented( ":89_2-ADD");
        String[] result1 = StringUtils.tokenizeSegmented( "DOM2:89_2-ADD");
        String[] result2 = StringUtils.tokenizeSegmented( "DOM2:89-ADD");
        String[] result3 = StringUtils.tokenizeSegmented( "DOM2:89_2");
        String[] result4 = StringUtils.tokenizeSegmented( "DOM2");
        String[] result5 = StringUtils.tokenizeSegmented( ":89");
        String[] result6 = StringUtils.tokenizeSegmented( "_2");
        String[] result7 = StringUtils.tokenizeSegmented( "-ADD");
        System.out.println(result);
        String[] result01 = StringUtils.tokenizeSegmented(":_-", ":89_2-ADD");
        String[] result11 = StringUtils.tokenizeSegmented( ":_-","DOM2:89_2-ADD");
        String[] result21 = StringUtils.tokenizeSegmented( ":_-","DOM2:89-ADD");
        String[] result31 = StringUtils.tokenizeSegmented(":_-", "DOM2:89_2");
        String[] result41 = StringUtils.tokenizeSegmented(":_-", "DOM2");
        String[] result51 = StringUtils.tokenizeSegmented(":_-", ":89");
        String[] result61 = StringUtils.tokenizeSegmented(":_-", "_2");
        String[] result71 = StringUtils.tokenizeSegmented( ":_-","-ADD");
        System.out.println(result);
        System.out.println(result1);
        System.out.println(result2);

    }
}