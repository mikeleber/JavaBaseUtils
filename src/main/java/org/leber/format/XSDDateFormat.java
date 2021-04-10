package org.leber.format;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;

public class XSDDateFormat extends DateFormat {
    /**
     * Message retriever.
     */
    // private static final MessageRetriever MSG = ResourceKeys.MSG;
    /**
     * DateFormat for Zulu (UTC) form of an XML Schema dateTime string.
     */
    public static final DateFormat DATEFORMAT_XSD_ZULU = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static {
        DATEFORMAT_XSD_ZULU.setTimeZone(TimeZone.getDefault());
    }

    /**
     * @see DateFormat#parse(java.lang.String)
     */
    @Override
    public Date parse(String src, ParsePosition parse_pos) {
        Date date;
        // validate fixed portion of format
        int index = 0;
        try {
            if (src != null) {
                if ((src.charAt(0) == '+') || (src.charAt(0) == '-')) {
                    src = src.substring(1);
                }
                if (src.length() < 19) {
                    parse_pos.setIndex(src.length() - 1);
                    handleParseError(parse_pos, "TOO_FEW_CHARS");
                }
                validateChar(src, parse_pos, index = 4, '-', "EXPECTED_DASH");
                validateChar(src, parse_pos, index = 7, '-', "EXPECTED_DASH");
                validateChar(src, parse_pos, index = 10, 'T', "EXPECTED_CAPITAL_T");
                validateChar(src, parse_pos, index = 13, ':', "EXPECTED_COLON_IN_TIME");
                validateChar(src, parse_pos, index = 16, ':', "EXPECTED_COLON_IN_TIME");
            }
            // convert what we have validated so far
            try {
                synchronized (DATEFORMAT_XSD_ZULU) {
                    date = DATEFORMAT_XSD_ZULU.parse((src == null) ? null : (src.substring(0, 19) + ".000Z"));
                }
            } catch (Exception e) {
                throw new NumberFormatException(e.toString());
            }
            index = 19;
            // parse optional milliseconds
            if (src != null) {
                if ((index < src.length()) && (src.charAt(index) == '.')) {
                    int milliseconds = 0;
                    int start = ++index;
                    while ((index < src.length()) && Character.isDigit(src.charAt(index))) {
                        index++;
                    }
                    String decimal = src.substring(start, index);
                    if (decimal.length() == 3) {
                        milliseconds = Integer.parseInt(decimal);
                    } else if (decimal.length() < 3) {
                        milliseconds = Integer.parseInt((decimal + "000").substring(0, 3));
                    } else {
                        milliseconds = Integer.parseInt(decimal.substring(0, 3));
                        if (decimal.charAt(3) >= '5') {
                            ++milliseconds;
                        }
                    }
                    // add milliseconds to the current date
                    date.setTime(date.getTime() + milliseconds);
                }
                // parse optional timezone
                if (((index + 5) < src.length()) && ((src.charAt(index) == '+') || (src.charAt(index) == '-'))) {
                    validateCharIsDigit(src, parse_pos, index + 1, "EXPECTED_NUMERAL");
                    validateCharIsDigit(src, parse_pos, index + 2, "EXPECTED_NUMERAL");
                    validateChar(src, parse_pos, index + 3, ':', "EXPECTED_COLON_IN_TIMEZONE");
                    validateCharIsDigit(src, parse_pos, index + 4, "EXPECTED_NUMERAL");
                    validateCharIsDigit(src, parse_pos, index + 5, "EXPECTED_NUMERAL");
                    int hours = (((src.charAt(index + 1) - '0') * 10) + src.charAt(index + 2)) - '0';
                    int mins = (((src.charAt(index + 4) - '0') * 10) + src.charAt(index + 5)) - '0';
                    int millisecs = ((hours * 60) + mins) * 60 * 1000;
                    // subtract millisecs from current date to obtain GMT
                    if (src.charAt(index) == '+') {
                        millisecs = -millisecs;
                    }
                    date.setTime(date.getTime() + millisecs);
                    index += 6;
                }
                if ((index < src.length()) && (src.charAt(index) == 'Z')) {
                    index++;
                }
                if (index < src.length()) {
                    handleParseError(parse_pos, "TOO_MANY_CHARS");
                }
            }
        } catch (ParseException pe) {
            index = 0; // IMPORTANT: this tells DateFormat.parse() to throw a
            // ParseException
            parse_pos.setErrorIndex(index);
            date = null;
        }
        parse_pos.setIndex(index);
        return (date);
    }

    /**
     * @see DateFormat#format(java.util.Date)
     */
    @Override
    public StringBuffer format(Date date, StringBuffer append_buf, FieldPosition field_pos) {
        String str;
        synchronized (DATEFORMAT_XSD_ZULU) {
            str = DATEFORMAT_XSD_ZULU.format(date);
        }
        if (append_buf == null) {
            append_buf = new StringBuffer();
        }
        append_buf.append(str);
        return (append_buf);
    }

    private void validateChar(String str, ParsePosition parse_pos, int index, char expected, String error_reason) throws ParseException {
        if (str.charAt(index) != expected) {
            handleParseError(parse_pos, error_reason);
        }
    }

    private void validateCharIsDigit(String str, ParsePosition parse_pos, int index, String error_reason) throws ParseException {
        if (!Character.isDigit(str.charAt(index))) {
            handleParseError(parse_pos, error_reason);
        }
    }

    private void handleParseError(ParsePosition parse_pos, String error_reason) throws ParseException {
        throw new ParseException("INVALID_XSD_DATETIME", parse_pos.getErrorIndex());
    }
}
