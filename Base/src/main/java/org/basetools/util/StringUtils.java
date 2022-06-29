package org.basetools.util;

import org.basetools.util.hash.MurmurHash;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Stack;
import java.util.StringTokenizer;

public class StringUtils {
    public static final char CHAR_SPACE = (char) 0x20;
    public static final char CHAR_EMPTY = (char) 0x0;
    public static final char CHAR_NEWLINE = (char) 10;
    public static final String newLine = System.getProperty("line.separator");
    public static final DecimalFormat FROM_FLOATINGNUMBER_FORMAT = new DecimalFormat();

    static {
        FROM_FLOATINGNUMBER_FORMAT.setGroupingUsed(false);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        FROM_FLOATINGNUMBER_FORMAT.setDecimalFormatSymbols(symbols);
        FROM_FLOATINGNUMBER_FORMAT.setMaximumFractionDigits(340);
    }

    /**
     * Pre-pend the given character to the String until the result is the desired length. If a String is longer than the desired length, it will not be truncated, however no padding will be added.
     *
     * @param text
     * @param len       target length.
     * @param character
     * @return padded String.
     * @throws NullPointerException
     */
    public static final String prepad(String text, int len, char character) {

        int needed = len - text.length();
        if (needed <= 0) {
            return text;
        }
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < needed; i++) {
            sb.append(character);
        }
        sb.append(text);
        return (sb.toString());
    }

    public static String replaceWhiteSpace(String toClean, char replacement, boolean collapseSpaces) {
        int size = toClean.length();
        StringBuilder result = new StringBuilder(toClean.length());
        char old = CHAR_EMPTY;
        for (int i = 0; i < size; i++) {
            boolean skip = false;
            char act = toClean.charAt(i);
            if (Character.isWhitespace(act)) {
                act = replacement;
                if (collapseSpaces && act == old) {
                    skip = true;
                }
            }
            if (!skip) {
                result.append(act);
            }
            old = act;
        }
        return result.toString();
    }

    public static final String toString(Object[] sourceList, String delimiter) {
        return toString(sourceList, delimiter, false);
    }

    public static final String toString(Object[] keys, Object[] values, String keyValueDelimiter, String delimiter) {
        if (keys == null) {
            return null;
        }
        int s = keys.length;
        boolean isFirstIteration = true;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s; i++) {
            if (isFirstIteration) {
                isFirstIteration = false;
            } else if (delimiter != null) {
                result.append(delimiter);
            }
            result.append(keys[i]);
            if (keyValueDelimiter != null) {
                result.append(keyValueDelimiter);
            }
            if (values != null) {
                result.append(values[i]);
            }
        }
        return result.toString();
    }

    public static final String toString(Object[] values, String delimiter, boolean skipNullElements) {
        if (values == null) {
            return null;
        }
        int s = values.length;
        boolean isFirstIteration = true;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s; i++) {
            if (skipNullElements && values[i] == null) {
            } else {
                if (isFirstIteration) {
                    isFirstIteration = false;
                } else if (delimiter != null) {
                    result.append(delimiter);
                }
                result.append(values[i]);
            }
        }
        return result.toString();
    }

    public static boolean isXML(String string) {
        return string != null && string.trim().startsWith("<") && string.trim().endsWith(">");
    }

    public static ContentType getDataType(String string) {
        if (string == null) return ContentType.unknown;
        String content = string.trim();
        if (content.startsWith("<") && content.endsWith(">")) return ContentType.xml;
        if (content.startsWith("{") && content.endsWith("}")) {
            if (content.indexOf("$schema") >= 0) return ContentType.jsonSchema;
            else return ContentType.json;
        }

        return ContentType.unknown;
    }

    public static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        // Extract these first so we detect NPEs the same as the java.lang.String version
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;

        // Check for invalid parameters
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }

        // Check that the regions are long enough
        if (srcLen < length || otherLen < length) {
            return false;
        }

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The same check as in String.regionMatches():
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @deprecated use Apache Stringutils.stripEnd
     */
    public static final String trimRight(String content) {
        int len = content.length();
        int st = 0;
        int off = 0; /* avoid getfield opcode */
        char[] val = content.toCharArray(); /* avoid getfield opcode */
        while ((st < len) && (val[off + len - 1] <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < content.length())) ? content.substring(st, len) : content;
    }

    /**
     * @deprecated use Apache Stringutils.stripStart
     */
    public static final String trimLeft(String content) {
        int len = content.length();
        int st = 0;
        int off = 0; /* avoid getfield opcode */
        char[] val = content.toCharArray(); /* avoid getfield opcode */
        while ((st < len) && (val[off + st] <= ' ')) {
            st++;
        }
        return ((st > 0) || (len < content.length())) ? content.substring(st, len) : content;
    }

    public static final void trimAtEnd(StringBuilder sb, int count) {
        if (sb != null && sb.length() >= count) {
            for (int i = 0; i < count; i++) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }

    public static String prependIfMissing(final String str, final CharSequence prefix, final CharSequence toPrepend, final boolean ignoreCase, final CharSequence... prefixes) {
        if (str == null || EmptyUtil.isEmpty(prefix) || startsWith(str, prefix, ignoreCase)) {
            return str;
        }
        if (prefixes != null && prefixes.length > 0) {
            for (final CharSequence p : prefixes) {
                if (startsWith(str, p, ignoreCase)) {
                    return str;
                }
            }
        }
        return (toPrepend != null ? toPrepend.toString() : prefix.toString()) + str;
    }

    public static boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == prefix;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
    }

    public static String[] getTokensArray(String strData, String strDelimiters, boolean returnDelims) {

        String[] strTokenArray = null;

        try {

            if (strData == null || strDelimiters == null) return strTokenArray;

            //create tokenizer object
            StringTokenizer st = new StringTokenizer(strData, strDelimiters, returnDelims);

            /*
             * Create the array having same size as total
             * number of tokens
             */
            strTokenArray = new String[st.countTokens()];

            //iterate through all the tokens
            int count = 0;
            while (st.hasMoreTokens()) {

                //add to an array
                strTokenArray[count++] = st.nextToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strTokenArray;
    }

    public static final String toPath(Stack stack) {
        int sCount = stack.size();
        StringBuilder result = new StringBuilder();
        for (int s = (sCount - 1); s >= 0; s--) {
            result.append("/");
            result.append(stack.get(s));
        }
        return result.toString();
    }

    public static String convertExponentialValue(String number) throws NumberFormatException {
        if (number != null && (number.indexOf("e") > -1 || number.indexOf("E") > -1)) {
            number = FROM_FLOATINGNUMBER_FORMAT.format(Double.parseDouble(number));
            if (number.endsWith(".0")) {
                number = number.substring(0, number.length() - 2);
            }
        }
        return number;
    }

    public static StringBuilder wrapQuoted(StringBuilder builder, CharSequence aString) {
        return builder.append(wrapQuoted(aString));
    }

    public static CharSequence wrapQuoted(CharSequence aString) {

        if (org.apache.commons.lang3.StringUtils.isEmpty(aString)) {
            return aString;
        }

        return "\"" + aString + "\"";
    }

    public static final boolean isTrue(String val) {
        if (val == null || val.equalsIgnoreCase("false")) {
            return false;
        }
        return (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("y") || val.equalsIgnoreCase("yes") || val.equalsIgnoreCase("1") || val.equalsIgnoreCase("1.0"));
    }

    public static final String removeBetween(String text, String startQuali, String endQuali) {
        return removeBetween(text, startQuali, endQuali, false);
    }

    public static final String removeBetween(String text, String startQuali, String endQuali, boolean onlyNumeric) {
        return removeBetween(text, startQuali, endQuali, onlyNumeric, false);
    }

    public static final String removeBetween(String text, String startQuali, String endQuali, boolean onlyNumeric, boolean retNullIfNotNumeric) {
        int sqLen = startQuali.length();
        int eqLen = endQuali.length();
        int comStart = text.indexOf(startQuali);
        if (comStart == -1) {
            return text;
        }
        StringBuilder cleared = new StringBuilder(text.length());
        int start = 0;
        while (comStart != -1) {
            cleared.append(text.substring(start, comStart));
            int comEnd = text.indexOf(endQuali, comStart);
            if (comEnd == -1) {
                // no end forget about it!
                break;
            } else {
                if (onlyNumeric) {
                    String qualiContent = text.substring(comStart + sqLen, comEnd);
                    if (!org.apache.commons.lang3.StringUtils.isNumeric(qualiContent)) {
                        if (retNullIfNotNumeric) {
                            return null;
                        } else {
                            cleared.append(text.substring(comStart, comEnd + eqLen));
                        }
                    }
                }
                comStart = text.indexOf(startQuali, comEnd + eqLen);
                start = comEnd + eqLen;
                if (comStart == -1) {
                    cleared.append(text.substring(comEnd + eqLen));
                }
            }
        }
        return cleared.toString();
    }

    /**
     * Create a fixed result stringarray with size of tokens.
     * String delims = ":_-";
     *
     * @param token
     * @return
     */
    public static String[] tokenizeSegmented(String token) {
        String delims = "_-:#";
        String[] results = new String[delims.length() + 1];
        int size = token.length();
        int pos = 0;
        int segmentPos = 0;
        int segStart = 0, segEnd = 0;
        while (pos < size) {
            char curChar = token.charAt(pos++);
            switch (curChar) {
                case '€':
                    segEnd = pos - 1;
                    results[segmentPos] = (segStart != segEnd ? token.substring(segStart, segEnd) : null);
                    segmentPos = 3;
                    segStart = pos;
                    break;

                case '_':
                    segEnd = pos - 1;
                    results[segmentPos] = (segStart != segEnd ? token.substring(segStart, segEnd) : null);
                    segmentPos = 2;
                    segStart = pos;
                    break;
                case '-':
                    segEnd = pos - 1;
                    results[segmentPos] = (segStart != segEnd ? token.substring(segStart, segEnd) : null);
                    segmentPos = 1;
                    segStart = pos;
                    break;
                case '§':
                    segEnd = pos - 1;
                    results[segmentPos] = (segStart != segEnd ? token.substring(segStart, segEnd) : null);
                    segmentPos = 4;
                    segStart = pos;
                    break;
            }
        }
        if (segStart > segEnd) {
            //End reached write last segment
            results[segmentPos] = (segStart != token.length() ? token.substring(segStart, token.length()) : null);
        } else if (segStart == 0 && segEnd == 0) {
            //No delim found in string, string is first segment
            results[0] = token;
        }

        return results;
    }

    public static String[] tokenizeSegmented(String delims, String token) {
        String[] results = new String[delims.length() + 1];
        int size = token.length();
        int pos = 0;
        int segmentPos = 0;
        int segStart = 0, segEnd = 0;
        while (pos < size) {
            char curChar = token.charAt(pos++);

            for (int d = 0; d < delims.length(); d++) {
                if (delims.charAt(d) == curChar) {
                    segEnd = pos - 1;
                    results[segmentPos] = (segStart != segEnd ? token.substring(segStart, segEnd) : null);
                    segmentPos = d + 1;
                    segStart = pos;
                    break;
                }
            }
        }
        if (segStart > segEnd) {
            //End reached write last segment
            results[segmentPos] = (segStart != token.length() ? token.substring(segStart, token.length()) : null);
        } else if (segStart == 0 && segEnd == 0) {
            //No delim found in string, string is first segment
            results[0] = token;
        }

        return results;
    }

    public static int hash(int prime, String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = prime * h + s.charAt(i);
        }
        return h;
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static int hashMurmur(String s) {
        return MurmurHash.hash32(s);
    }

    public enum ContentType {unknown, xml, jsonSchema, json, yaml}

    public static final int countChars(CharSequence src, char toCount, boolean retDelims) {
        int numOfElements = 0;
        int nowPos = 0;
        int maxLength = src.length();
        while (nowPos < maxLength) {
            while (nowPos < src.length()) {
                char c = src.charAt(nowPos);
                if (!retDelims && toCount != c) {
                    break;
                }
                nowPos++;
            }
            if (nowPos >= maxLength) {
                break;
            }
            int startPos = nowPos;
            while (nowPos < src.length()) {
                char c = src.charAt(nowPos);
                if (toCount == c) {
                    break;
                }
                nowPos++;
            }
            if (retDelims && (startPos == nowPos)) {
                char c = src.charAt(nowPos);
                if (toCount != c) {
                    nowPos++;
                }
            }
            numOfElements++;
        }
        return numOfElements;
    }
}
