package org.basetools.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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

    public static String replaceWhiteSpace(String toClean, char replacement, boolean collapseSpaces) {
        int size = toClean.length();
        StringBuffer result = new StringBuffer(toClean.length());
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

    public static String convertExponentialValue(String number) throws NumberFormatException {
        if (number != null && (number.indexOf("e") > -1 || number.indexOf("E") > -1)) {
            number = FROM_FLOATINGNUMBER_FORMAT.format(Double.parseDouble(number));
            if (number.endsWith(".0")) {
                number = number.substring(0, number.length() - 2);
            }
        }
        return number;
    }

    public static CharSequence wrapQuoted(CharSequence aString) {

        if (org.apache.commons.lang3.StringUtils.isEmpty(aString)) {
            return aString;
        }

        return "\"" + aString + "\"";
    }

    public static StringBuilder wrapQuoted(StringBuilder builder, CharSequence aString) {
        return builder.append(wrapQuoted(aString));
    }

    public static final boolean isTrue(String val) {
        if (val == null || val.equalsIgnoreCase("false")) {
            return false;
        }
        return (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("y") || val.equalsIgnoreCase("yes") || val.equalsIgnoreCase("1") || val.equalsIgnoreCase("1.0"));
    }
}
