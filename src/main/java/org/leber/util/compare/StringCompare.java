package org.leber.util.compare;

import java.text.Collator;
import java.util.Comparator;

public class StringCompare {
    /**
     * Returns a comparator that compares contained numbers based on their numeric values and compares other parts using the current locale's order rules.
     * <p>
     * For example in German locale this will be a comparator that handles umlauts correctly and ignores upper/lower case differences.
     * </p>
     *
     * @return <p>
     * A string comparator that uses the current locale's order rules and handles embedded numbers correctly.
     * </p>
     * @see #getNaturalComparator(java.text.Collator)
     */
    public static final Comparator getNaturalComparator() {
        Collator collator = Collator.getInstance();
        return getNaturalComparator(collator);
    }

    /**
     * Returns a comparator that compares contained numbers based on their numeric values and compares other parts using the given collator.
     *
     * @param collator used for locale specific comparison of text (non-number) subwords - must not be null
     * @return <p>
     * A string comparator that uses the given Collator to compare subwords and handles embedded numbers correctly.
     * </p>
     * @see #getNaturalComparator()
     */
    public static final Comparator getNaturalComparator(Collator collator) {
        if (collator == null) {
            // it's important to explicitly handle this here - else the bug will
            // manifest anytime later in possibly
            // unrelated code that tries to use the comparator
            throw new NullPointerException("collator must not be null");
        }
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return compareNatural(collator, (String) o1, (String) o2);
            }
        };
    }

    /**
     * <p>
     * Compares two strings using the given collator and comparing contained numbers based on their numeric values.
     * </p>
     *
     * @param s first string
     * @param t second string
     * @return zero iff <code>s</code> and <code>t</code> are equal, a value less than zero iff <code>s</code> lexicographically precedes <code>t</code> and a value larger than zero iff <code>s</code> lexicographically follows <code>t</code>
     */
    public static final int compareNatural(Collator collator, String s, String t) {
        return compareNatural(s, t, true, collator);
    }

    /**
     * @param s             first string
     * @param t             second string
     * @param caseSensitive treat characters differing in case only as equal - will be ignored if a collator is given
     * @param collator      used to compare subwords that aren't numbers - if null, characters will be compared individually based on their Unicode value
     * @return zero iff <code>s</code> and <code>t</code> are equal, a value less than zero iff <code>s</code> lexicographically precedes <code>t</code> and a value larger than zero iff <code>s</code> lexicographically follows <code>t</code>
     */
    public static final int compareNatural(String s, String t, boolean caseSensitive, Collator collator) {
        int sIndex = 0;
        int tIndex = 0;
        int sLength = s.length();
        int tLength = t.length();
        while (true) {
            // both character indices are after a subword (or at zero)
            // Check if one string is at end
            if (sIndex == sLength && tIndex == tLength) {
                return 0;
            }
            if (sIndex == sLength) {
                return -1;
            }
            if (tIndex == tLength) {
                return 1;
            }
            // Compare sub word
            char sChar = s.charAt(sIndex);
            char tChar = t.charAt(tIndex);
            boolean sCharIsDigit = Character.isDigit(sChar);
            boolean tCharIsDigit = Character.isDigit(tChar);
            if (sCharIsDigit && tCharIsDigit) {
                // Compare numbers
                // skip leading 0s
                int sLeadingZeroCount = 0;
                while (sChar == '0') {
                    ++sLeadingZeroCount;
                    ++sIndex;
                    if (sIndex == sLength) {
                        break;
                    }
                    sChar = s.charAt(sIndex);
                }
                int tLeadingZeroCount = 0;
                while (tChar == '0') {
                    ++tLeadingZeroCount;
                    ++tIndex;
                    if (tIndex == tLength) {
                        break;
                    }
                    tChar = t.charAt(tIndex);
                }
                boolean sAllZero = sIndex == sLength || !Character.isDigit(sChar);
                boolean tAllZero = tIndex == tLength || !Character.isDigit(tChar);
                if (sAllZero && tAllZero) {
                    continue;
                }
                if (sAllZero && !tAllZero) {
                    return -1;
                }
                if (tAllZero) {
                    return 1;
                }
                int diff = 0;
                do {
                    if (diff == 0) {
                        diff = sChar - tChar;
                    }
                    ++sIndex;
                    ++tIndex;
                    if (sIndex == sLength && tIndex == tLength) {
                        return diff != 0 ? diff : sLeadingZeroCount - tLeadingZeroCount;
                    }
                    if (sIndex == sLength) {
                        if (diff == 0) {
                            return -1;
                        }
                        return Character.isDigit(t.charAt(tIndex)) ? -1 : diff;
                    }
                    if (tIndex == tLength) {
                        if (diff == 0) {
                            return 1;
                        }
                        return Character.isDigit(s.charAt(sIndex)) ? 1 : diff;
                    }
                    sChar = s.charAt(sIndex);
                    tChar = t.charAt(tIndex);
                    sCharIsDigit = Character.isDigit(sChar);
                    tCharIsDigit = Character.isDigit(tChar);
                    if (!sCharIsDigit && !tCharIsDigit) {
                        // both number sub words have the same length
                        if (diff != 0) {
                            return diff;
                        }
                        break;
                    }
                    if (!sCharIsDigit) {
                        return -1;
                    }
                    if (!tCharIsDigit) {
                        return 1;
                    }
                } while (true);
            } else {
                // Compare words
                if (collator != null) {
                    // To use the collator the whole subwords have to be
                    // compared - character-by-character comparision
                    // is not possible. So find the two subwords first
                    int aw = sIndex;
                    int bw = tIndex;
                    do {
                        ++sIndex;
                    } while (sIndex < sLength && !Character.isDigit(s.charAt(sIndex)));
                    do {
                        ++tIndex;
                    } while (tIndex < tLength && !Character.isDigit(t.charAt(tIndex)));
                    String as = s.substring(aw, sIndex);
                    String bs = t.substring(bw, tIndex);
                    int subwordResult = collator.compare(as, bs);
                    if (subwordResult != 0) {
                        return subwordResult;
                    }
                } else {
                    // No collator specified. All characters should be ascii
                    // only. Compare character-by-character.
                    do {
                        if (sChar != tChar) {
                            if (caseSensitive) {
                                return sChar - tChar;
                            }
                            sChar = Character.toUpperCase(sChar);
                            tChar = Character.toUpperCase(tChar);
                            if (sChar != tChar) {
                                sChar = Character.toLowerCase(sChar);
                                tChar = Character.toLowerCase(tChar);
                                if (sChar != tChar) {
                                    return sChar - tChar;
                                }
                            }
                        }
                        ++sIndex;
                        ++tIndex;
                        if (sIndex == sLength && tIndex == tLength) {
                            return 0;
                        }
                        if (sIndex == sLength) {
                            return -1;
                        }
                        if (tIndex == tLength) {
                            return 1;
                        }
                        sChar = s.charAt(sIndex);
                        tChar = t.charAt(tIndex);
                        sCharIsDigit = Character.isDigit(sChar);
                        tCharIsDigit = Character.isDigit(tChar);
                    } while (!sCharIsDigit && !tCharIsDigit);
                }
            }
        }
    }
}
