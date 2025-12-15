package org.basetools.util;

import org.basetools.util.array.ArrayUtil;
import org.basetools.util.hash.MurmurHash;
import org.basetools.util.xml.XMLChar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
    public static final char CHAR_SPACE = (char) 0x20;
    public static final char CHAR_EMPTY = (char) 0x0;
    public static final char CHAR_NEWLINE = (char) 10;
    public static final String newLine = System.getProperty("line.separator");
    public static final DecimalFormat FROM_FLOATINGNUMBER_FORMAT = new DecimalFormat();
    private static final DecimalFormat expDecimalFormat = new DecimalFormat("#");

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

    public static final String convertExponentialValue(double value) throws NumberFormatException {
        expDecimalFormat.setMaximumFractionDigits(0);
        return expDecimalFormat.format(value);
    }

    public static final String replaceWhiteSpace(String toClean, char replacement, boolean collapseSpaces) {
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

    public static final boolean isXML(String string) {
        return string != null && string.trim().startsWith("<") && string.trim().endsWith(">");
    }

    public static final ContentType getDataType(String string) {
        if (string == null) return ContentType.unknown;
        String content = string.trim();
        if (content.startsWith("<") && content.endsWith(">")) return ContentType.xml;
        if (content.startsWith("{") && content.endsWith("}")) {
            if (content.indexOf("$schema") >= 0) return ContentType.xjson;
            else return ContentType.json;
        }

        return ContentType.unknown;
    }

    public static final boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start, final int length) {
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

    public static final String[] split(String aString, String splitter) {
        if (aString == null) {
            return null;
        }
        return aString.split(splitter);
    }

    public static final String prependIfMissing(final String str, final CharSequence prefix, final CharSequence toPrepend, final boolean ignoreCase, final CharSequence... prefixes) {
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

    public static final boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == prefix;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
    }

    public static final String[] getTokensArray(String strData, String strDelimiters, boolean returnDelims) {

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

    public static final String convertExponentialValue(String number) throws NumberFormatException {
        if (number != null && (number.indexOf("e") > -1 || number.indexOf("E") > -1)) {
            number = FROM_FLOATINGNUMBER_FORMAT.format(Double.parseDouble(number));
            if (number.endsWith(".0")) {
                number = number.substring(0, number.length() - 2);
            }
        }
        return number;
    }

    public static final StringBuilder wrapQuoted(StringBuilder builder, CharSequence aString) {
        return builder.append(wrapQuoted(aString));
    }

    public static final CharSequence wrapQuoted(CharSequence aString) {

        if (aString == null || aString.length() == 0) {
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
                    if (!StringUtils.isNumeric(qualiContent)) {
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

    public static final boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a fixed result stringarray with size of tokens.
     * String delims = ":_-";
     *
     * @param token
     * @return
     */
    public static final String[] tokenizeSegmented(String token) {
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
    public static String[] tokenize(String s, String delimiter) {
        if (s == null) {
            return new String[0];
        }
        final java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(s, delimiter);
        final String[] token = new String[tokenizer.countTokens()];
        int k = 0;
        while (tokenizer.hasMoreTokens()) {
            token[k++] = tokenizer.nextToken(delimiter);
        }
        return token;
    }

    public static final String[] tokenizeSegmented(String delims, String token) {
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

    public static final int hash(int prime, String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = prime * h + s.charAt(i);
        }
        return h;
    }

    public static final boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static final boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static final CharSequence getNonEmpty(final CharSequence cs, final CharSequence defaultCs) {
        return !isEmpty(cs) ? cs : defaultCs;
    }

    public static final String getNonEmpty(final String cs, final String defaultCs) {
        return !isEmpty(cs) ? cs : defaultCs;
    }

    public static final int hashMurmur(String s) {
        return MurmurHash.hash32(s);
    }

    public static final String replace(String val, String what, String with) {
        if (val == null) return val;
        return val.replaceAll(what, with);
    }

    public static final boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

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

    public enum ContentType {
        unknown, xml, xsd, xjson, json, yaml, csv, txt, avro, nls;

        public static ContentType valueOf(String name, ContentType defaultVal) {
            for (ContentType aType : ContentType.values()) {
                if (aType.name().equalsIgnoreCase(name)) {
                    return aType;
                }
            }
            return defaultVal;
        }

    }

    public static final String EMPTY_STRING = "";


    static {
        FROM_FLOATINGNUMBER_FORMAT.setGroupingUsed(false);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        FROM_FLOATINGNUMBER_FORMAT.setDecimalFormatSymbols(symbols);
        FROM_FLOATINGNUMBER_FORMAT.setMaximumFractionDigits(340);
    }


    public static final String getRightPart(String content, char delim) {
        int currPos = content.length();
        int length = currPos;
        int off = 0; /* avoid getfield opcode */
        char[] val = content.toCharArray(); /* avoid getfield opcode */
        while ((currPos >= 0) && (val[off + currPos - 1] != delim)) {
            currPos--;
        }
        return currPos > 0 ? content.substring(currPos, length) : content;
    }


    public static final String intToString(int number, int groupSize) {
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

    public static final String[] splitByRegex(String value, String regex) {
        Object[] resultArray = null;
        String[] x = Pattern.compile(regex).split(value);
        for (int i = 0; i < x.length; i++) {
            resultArray = ArrayUtil.addToGrowArray(resultArray, x[i], 10);
        }
        return (String[]) ArrayUtil.finalizeGrowArray(String.class, resultArray);
    }


    /**
     * Split every string in tupple if starts with pattern and returns the right part.
     * getList(["col-1","row-1","col-2"],"col-",'-')-> 1,2
     *
     * @param tupples
     * @param pattern
     * @param valSplitter
     * @return
     */
    public static List<String> getList(String[] tupples, String pattern, char valSplitter) {
        return Arrays.stream(tupples).filter(name -> name.toLowerCase().startsWith(pattern.toLowerCase())).map(clazz -> StringUtils.getRightPart(clazz, valSplitter)).collect(Collectors.toList());
    }

    /**
     * Split every string in tupple if starts with pattern and returns the first right part.
     * getList(["col-1","row-1","col-2"],"col-",'-')-> 1
     *
     * @param tupples
     * @param pattern
     * @param valSplitter
     * @return
     */
    public static String getFirstMatch(String[] tupples, String pattern, char valSplitter) {
        for (String tup : tupples) {
            if (tup != null && tup.startsWith(pattern)) {
                return StringUtils.getRightPart(tup, valSplitter);
            }
        }
        return null;
    }

    public static final String shredder(String text, int length, int startAt) {
        String base = text.substring(startAt);
        String trail = text.substring(0, startAt);
        length = length - startAt;
        if (length <= 0) {
            return trail;
        } else if (base.length() > length) {
            int difFactor = (int) Math.ceil((double) base.length() / length);
            StringBuilder newSeqName = new StringBuilder(length);
            for (int s = 0; s < base.length(); s = s + difFactor) {
                newSeqName.append(base.charAt(s));
            }
            if (newSeqName.length() < length) {
                int addChars = length - newSeqName.length();
                newSeqName.append(base.substring(base.length() - addChars, base.length()));
            }
            base = newSeqName.toString();
        }
        return trail + base;
    }

    public static final String toStringForEmpty(Object value, String defaultValue) {
        String simpleValue = Objects.toString(value, null);
        if (org.basetools.util.StringUtils.isEmpty(simpleValue)) {
            simpleValue = null;
        }
        return simpleValue;
    }

    public static final <T> String toString(List<T> elements, Function<T, String> toStringer) {
        StringBuilder result = new StringBuilder();
        for (T element : elements) {
            result.append(toStringer.apply(element));
        }
        return result.toString();
    }

    public static final <T> String toString(T[] elements, Function<T, String> toStringer) {
        StringBuilder result = new StringBuilder();
        for (T element : elements) {
            if (toStringer == null) {
                result.append(Objects.toString(element, null));
            } else {
                result.append(toStringer.apply((T) element));
            }
        }
        return result.toString();
    }

    public static final String toString(short[] elements, String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            result.append(Objects.toString(elements[i], null));
            if (elements.length > i + 1) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static final boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        return org.apache.commons.lang3.StringUtils.contains(seq, searchSeq);
    }

    public static final String trim(String expression) {
        return expression != null ? expression.trim() : null;
    }

    public static final String toLowerCase(String text, String defaultVal) {
        return text != null ? text.toLowerCase() : defaultVal;
    }
    public static String cleanXSDName(String name) {
        if (name.length() == 0) {
            return name;
        } else {
            char ch = name.charAt(0);
            StringBuilder result = new StringBuilder();
            for (int i = 1; i < name.length(); ++i) {
                ch = name.charAt(i);
                if (!XMLChar.isName(ch)) {
                    //return false;
                } else {
                    result.append(ch);
                }
            }

            return result.toString();

        }
    }
}
