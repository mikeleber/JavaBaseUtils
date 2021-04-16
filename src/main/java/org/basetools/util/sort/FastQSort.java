package org.basetools.util.sort;

import org.basetools.log.LoggerFactory;
import org.basetools.util.compare.StringCompare;
import org.slf4j.Logger;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/*
 * @(#)QSortAlgorithm.java      1.3   29 Feb 1996 James Gosling
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted.
 * Please refer to the file http://www.javasoft.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://www.javasoft.com/licensing.html for further important
 * licensing information for the Java (tm) Technology.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

/**
 * A quick sort demonstration algorithm SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @version @(#)QSortAlgorithm.java 1.3, 29 Feb 1996 extended with TriMedian and InsertionSort by Denis Ahrens with all the tips from Robert Sedgewick (Algorithms in C++). It uses TriMedian and InsertionSort for lists shorts than 4.<fuhrmann@cs.tu-berlin.de>
 */
public final class FastQSort {
    public static final int ASC = 0;
    public static final int DES = 1;
    public static final int NATURAL = 2;
    private static final FastQSort _instance = new FastQSort();
    private final Logger logger = LoggerFactory.getLogger(FastQSort.class);
    /**
     * This is a generic version of C.A.R Hoare's Quick Sort algorithm. This will handle arrays that are already sorted, and arrays with duplicate keys. <BR>
     * If you think of a one dimensional array as going from the lowest index on the left to the highest index on the right then the parameters to this function are lowest index or left and highest index or right. The first time you call this function it will be with the parameters 0, a.length - 1.
     *
     * @param a
     * an integer array
     * @param lo0
     * left boundary of array partition
     * @param hi0
     * right boundary of array partition
     */
    Comparator _comparator = String.CASE_INSENSITIVE_ORDER;
    int[] _index;

    public static void sortList(List toSort, Comparator comparator) {
        if (toSort != null) {
            FastQSort sort = new FastQSort();
            if (comparator != null) {
                sort.sort(toSort, comparator);
            }
        }
    }

    public static Object[] sortArray(Object[] toSort, Comparator comparator) {
        if (toSort != null) {
            FastQSort sort = new FastQSort();
            if (comparator != null) {
                sort.sort(toSort, comparator);
            }
        }
        return toSort;
    }

    /**
     * Shellsort, using a sequence suggested by Gonnet.
     *
     * @param a an array of Comparable items.
     */
    public static void shellsort(Comparable[] a) {
        for (int gap = a.length / 2; gap > 0; gap = gap == 2 ? 1 : (int) (gap / 2.2)) {
            for (int i = gap; i < a.length; i++) {
                Comparable tmp = a[i];
                int j = i;
                for (; j >= gap && tmp.compareTo(a[j - gap]) < 0; j -= gap) {
                    a[j] = a[j - gap];
                }
                a[j] = tmp;
            }
        }
    }

    public static Comparator getStringComparator() {
        return Collator.getInstance();
    }

    public static Comparator getStringCaseInsensitiveComparator() {
        return String.CASE_INSENSITIVE_ORDER;
    }

    public static Comparator getNumberCompare() {
        return new Comparator() {
            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                return false;
            }

            @Override
            public int compare(Object o1, Object o2) {
                Number n1 = (Number) o1;
                Number n2 = (Number) o2;
                if (n1.doubleValue() > n2.doubleValue()) {
                    return 1;
                } else if (n1.doubleValue() < n2.doubleValue()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    public static Comparator getStringArrCompare(int col, boolean natural, boolean asNumber, int order) {
        return getInstance().new StringArrComparator(col, natural, asNumber, order == ASC);
    }

    public static Comparator getStringSequenceCompare(String[] seq) {
        return getInstance().new StringSequenceCompare(seq);
    }

    private synchronized static FastQSort getInstance() {
        // don't make it public it's not threadsave!
        return _instance;
    }

    private void QuickSort(List a, int l, int r) throws Exception {
        int M = 4;
        int i;
        int j;
        Object v;
        if ((r - l) > M) {
            // * @param o1 the first object to be compared.
            // * @param o2 the second object to be compared.
            // * @return a negative integer, zero, or a positive integer as the
            // * first argument is less than, equal to, or greater than the
            // * second. //
            i = (r + l) / 2;
            // if (a.get(l)>a.get(i)) swap(a,l,i); // Tri-Median Methode!
            // if (a.get(l)>a.get(r)) swap(a,l,r);
            // if (a.get(i)>a.get(r)) swap(a,i,r);
            if (_comparator.compare(a.get(l), a.get(i)) > 0) {
                swap(a, l, i); // Tri-Median Methode!
            }
            if (_comparator.compare(a.get(l), a.get(r)) > 0) {
                swap(a, l, r); // Tri-Median Methode!
            }
            if (_comparator.compare(a.get(i), a.get(r)) > 0) {
                swap(a, i, r); // Tri-Median Methode!
            }
            j = r - 1;
            swap(a, i, j);
            i = l;
            v = a.get(j);
            for (; ; ) {
                // while(a.get(++i)<v);
                // while(a(--j)>v);
                while (_comparator.compare(a.get(++i), v) < 0) {
                    //doNothing ;
                }
                while (_comparator.compare(a.get(--j), v) > 0) {
                    //doNothing;
                }
                if (j < i) {
                    break;
                }
                swap(a, i, j);
            }
            swap(a, i, r - 1);
            QuickSort(a, l, j);
            QuickSort(a, i + 1, r);
        }
    }

    private void QuickSort(Object[] a, int l, int r) throws Exception {
        int M = 4;
        int i;
        int j;
        Object v;
        if ((r - l) > M) {
            // * @param o1 the first object to be compared.
            // * @param o2 the second object to be compared.
            // * @return a negative integer, zero, or a positive integer as the
            // * first argument is less than, equal to, or greater than the
            // * second. //
            i = (r + l) / 2;
            // if (a.get(l)>a.get(i)) swap(a,l,i); // Tri-Median Methode!
            // if (a.get(l)>a.get(r)) swap(a,l,r);
            // if (a.get(i)>a.get(r)) swap(a,i,r);
            if (_comparator.compare(a[l], a[i]) > 0) {
                swap(a, l, i); // Tri-Median Methode!
            }
            if (_comparator.compare(a[l], a[r]) > 0) {
                swap(a, l, r); // Tri-Median Methode!
            }
            if (_comparator.compare(a[i], a[r]) > 0) {
                swap(a, i, r); // Tri-Median Methode!
            }
            j = r - 1;
            swap(a, i, j);
            i = l;
            v = a[j];
            for (; ; ) {
                // while(a[++i)<v);
                // while(a(--j)>v);
                while (_comparator.compare(a[++i], v) < 0) {
                    //doNothing;
                }
                while (_comparator.compare(a[--j], v) > 0) {
                    //doNothing;
                }
                if (j < i) {
                    break;
                }
                swap(a, i, j);
            }
            swap(a, i, r - 1);
            QuickSort(a, l, j);
            QuickSort(a, i + 1, r);
        }
    }

    private void swap(List a, int i, int j) {
        // swap value
        Object T;
        T = a.get(i);
        a.set(i, a.get(j));
        a.set(j, T);
        // swap additional indexes
        int iT;
        iT = _index[i];
        _index[i] = _index[j];
        _index[j] = iT;
    }

    private void swap(Object[] a, int i, int j) {
        // swap value
        Object T;
        T = a[i];
        a[i] = a[j];
        a[j] = T;
        // swap additional indexes
        int iT;
        iT = _index[i];
        _index[i] = _index[j];
        _index[j] = iT;
    }

    private void InsertionSort(List a, int lo0, int hi0) throws Exception {
        int i;
        int j;
        Object v;
        for (i = lo0 + 1; i <= hi0; i++) {
            v = a.get(i);
            int vi = _index[i];
            j = i;
            // while ((j>lo0) && (a.get(j-1)>v))
            while ((j > lo0) && (_comparator.compare(a.get(j - 1), v) > 0)) {
                a.set(j, a.get(j - 1));
                _index[j] = _index[j - 1];
                j--;
            }
            a.set(j, v);
            _index[j] = vi;
        }
    }

    private void InsertionSort(Object[] a, int lo0, int hi0) throws Exception {
        int i;
        int j;
        Object v;
        for (i = lo0 + 1; i <= hi0; i++) {
            v = a[i];
            int vi = _index[i];
            j = i;
            // while ((j>lo0) && (a[j-1)>v))
            while ((j > lo0) && (_comparator.compare(a[j - 1], v) > 0)) {
                a[j] = a[j - 1];
                _index[j] = _index[j - 1];
                j--;
            }
            a[j] = v;
            _index[j] = vi;
        }
    }

    public int[] sort(List a, Comparator comparator) {
        try {
            initIndex(a.size());
            _comparator = comparator;
            QuickSort(a, 0, a.size() - 1);
            InsertionSort(a, 0, a.size() - 1);
        } catch (Exception e) {
            logger.error("sortError", e);
        }
        return _index;
    }

    public int[] sort(List a, Comparator comparator, int direction) throws Exception {
        initIndex(a.size());
        _comparator = comparator;
        QuickSort(a, 0, a.size() - 1);
        InsertionSort(a, 0, a.size() - 1);
        if (direction == DES) {
            List aClone = new Vector(a);
            a.clear();
            for (int i = aClone.size() - 1; i >= 0; i--) {
                a.add(aClone.get(i));
            }
        }
        return _index;
    }

    public int[] sort(Object[] a, Comparator comparator) {
        try {
            initIndex(a.length);
            _comparator = comparator;
            QuickSort(a, 0, a.length - 1);
            InsertionSort(a, 0, a.length - 1);
        } catch (Exception e) {
            return null;
        }
        return _index;
    }

    public int[] sort(List a, int[] indexList, Comparator comparator) throws Exception {
        _index = indexList;
        _comparator = comparator;
        QuickSort(a, 0, a.size() - 1);
        InsertionSort(a, 0, a.size() - 1);
        return _index;
    }

    public int[] sort(Object[] a, int[] indexList, Comparator comparator) throws Exception {
        _index = indexList;
        _comparator = comparator;
        QuickSort(a, 0, a.length - 1);
        InsertionSort(a, 0, a.length - 1);
        return _index;
    }

    private void initIndex(int size) {
        _index = new int[size];
        for (int i = 0; i < size; i++) {
            _index[i] = i;
        }
    }

    public class StringArrComparator implements Comparator {
        Comparator _stringComp = String.CASE_INSENSITIVE_ORDER;
        boolean _asc = true;
        boolean _asNumber = false;
        private int _col = -1;

        public StringArrComparator(int col, boolean natural, boolean asNumber, boolean asc) {
            _col = col;
            if (natural) {
                _stringComp = StringCompare.getNaturalComparator();
            }
            _asNumber = asNumber;
            _asc = asc;
        }

        @Override
        public int compare(Object arg0, Object arg1) throws ClassCastException {
            if (_col < ((String[]) arg0).length && _col < ((String[]) arg1).length) {
                if (_asNumber) {
                    int aInt = Integer.valueOf(((String[]) arg0)[_col]).intValue();
                    int bInt = Integer.valueOf(((String[]) arg1)[_col]).intValue();
                    if (_asc) {
                        return aInt - bInt;
                    } else {
                        return bInt - aInt;
                    }
                } else {
                    int result = _stringComp.compare(((String[]) arg0)[_col], ((String[]) arg1)[_col]);
                    if (!_asc) {
                        return result * (-1);
                    } else {
                        return result;
                    }
                }
            }
            return -1;
        }
    }

    public class StringSequenceCompare implements Comparator<String> {
        List<String> sequence;

        StringSequenceCompare(String[] seq) {
            sequence = Arrays.asList(seq);
        }

        @Override
        public int compare(String t1, String t2) {
            if (t1 == null && t2 == null) {
                return 0;
            } else if (t1 == null) {
                return -1;
            } else if (t2 == null) {
                return 1;
            }
            if (t1.equals(t2)) {
                return 0;
            }
            return sequence.indexOf(t1) - sequence.indexOf(t2);
        }
    }
}
