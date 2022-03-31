package org.basetools.util.sort;

import org.junit.jupiter.api.Test;

class FastQSortTest {

    @Test
    void getStringSequenceCompare() {
        String[] sTest = new String[10];
        String[] sSeq = new String[]{"b", "c", "a", "z"};
        int s = 0;
        sTest[s++] = "a";
        sTest[s++] = "b";
        sTest[s++] = "c";
        sTest[s++] = "c";
        sTest[s++] = "z";
        sTest[s++] = "c";
        sTest[s++] = "b";
        sTest[s++] = "a";
        sTest[s++] = "z";
        sTest[s++] = "null";
        FastQSort sort = new FastQSort();
        sort.sort(sTest, FastQSort.getStringSequenceCompare(sSeq));
        System.out.println(sSeq);
    }
}