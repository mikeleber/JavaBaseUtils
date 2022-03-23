package org.basetools.convert;

import org.basetools.util.StringUtils;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * The type Iso 8601 parser.
 */
public class Iso8601Parser {
    private Iso8601Parser() {
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        toDate("1999");
        toDate("1999-12");
        toDate("1999-12-11");
        toDate("1999-12-11T11:22");
        toDate("1999-12-11T11:22:33");
        toDate("1999-12-11T11:22:33:Z");
        toDate("1999-12-11T11:22:33:'Z'");
        toDate("2016-10-19T14:15:36+08:00");
        toDate("2016-10-19T14:15:36+1000");
        toDate("2016-10-19T14:15:36.1000+1000");
    }

    /**
     * To date date.
     *
     * @param toParse the to parse
     * @return the date
     */
    public static Date toDate(String toParse) {
        return toCalendar(toParse).getTime();
    }

    /**
     * To calendar calendar.
     *
     * @param toParse the to parse
     * @return the calendar
     */
    public static Calendar toCalendar(String toParse) {
        int indexOfT = toParse.indexOf('T');
        if (indexOfT == -1) {
            return buildCalendarWithDateOnly(toParse, toParse);
        }
        Calendar result = buildCalendarWithDateOnly(toParse.substring(0, indexOfT), toParse);
        return parseHour(result, toParse.substring(indexOfT + 1));
    }

    private static Calendar buildCalendarWithDateOnly(String dateStr, String originalDate) {
        Calendar result = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        result.setMinimalDaysInFirstWeek(4);
        result.setFirstDayOfWeek(Calendar.MONDAY);
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        String basicFormatDate = dateStr.replaceAll("-", "");

        if (basicFormatDate.indexOf('W') != -1) {
            return parseWeekDate(result, basicFormatDate);
        } else if (basicFormatDate.length() == 7) {
            return parseOrdinalDate(result, basicFormatDate);
        } else {
            return parseCalendarDate(result, basicFormatDate, originalDate);
        }
    }

    private static Calendar parseHour(Calendar result, String hourStr) {
        String basicFormatHour = hourStr.replace(":", "");

        int indexOfZ = basicFormatHour.indexOf('Z');
        if (indexOfZ != -1) {
            parseHourWithoutHandlingTimeZone(result, basicFormatHour.substring(0, indexOfZ));
        } else {
            int indexOfSign = getIndexOfSign(basicFormatHour);
            if (indexOfSign == -1) {
                parseHourWithoutHandlingTimeZone(result, basicFormatHour);
                result.setTimeZone(TimeZone.getDefault());
            } else {
                parseHourWithoutHandlingTimeZone(result, basicFormatHour.substring(0, indexOfSign));
                result.setTimeZone(TimeZone.getTimeZone("GMT" + basicFormatHour.substring(indexOfSign)));
            }
        }
        return result;
    }

    private static Calendar parseWeekDate(Calendar result, String basicFormatDate) {
        result.set(Calendar.YEAR, Integer.parseInt(basicFormatDate.substring(0, 4)));
        result.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(basicFormatDate.substring(5, 7)));
        result.set(Calendar.DAY_OF_WEEK, basicFormatDate.length() == 7
                ? Calendar.MONDAY
                : Calendar.SUNDAY + Integer.parseInt(basicFormatDate.substring(7)));
        return result;
    }

    private static Calendar parseOrdinalDate(Calendar calendar, String basicFormatOrdinalDate) {
        calendar.set(Calendar.YEAR, Integer.parseInt(basicFormatOrdinalDate.substring(0, 4)));
        calendar.set(Calendar.DAY_OF_YEAR, Integer.parseInt(basicFormatOrdinalDate.substring(4)));
        return calendar;
    }

    private static Calendar parseCalendarDate(Calendar result, String basicFormatDate, String originalDate) {
        if (basicFormatDate.length() == 2) {
            return parseCalendarDateWithCenturyOnly(result, basicFormatDate);
        } else if (basicFormatDate.length() == 4) {
            return parseCalendarDateWithYearOnly(result, basicFormatDate);
        } else {
            return parseCalendarDateWithPrecisionGreaterThanYear(result, basicFormatDate, originalDate);
        }
    }

    private static void parseHourWithoutHandlingTimeZone(Calendar calendar, String basicFormatHour) {
        basicFormatHour = basicFormatHour.replace(',', '.');
        int indexOfDot = basicFormatHour.indexOf('.');
        double fractionalPart = 0;
        if (indexOfDot != -1) {
            fractionalPart = Double.parseDouble("0" + basicFormatHour.substring(indexOfDot));
            basicFormatHour = basicFormatHour.substring(0, indexOfDot);
        }

        if (basicFormatHour.length() >= 2) {
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(basicFormatHour.substring(0, 2)));
        }

        if (basicFormatHour.length() > 2) {
            calendar.set(Calendar.MINUTE, Integer.parseInt(basicFormatHour.substring(2, 4)));
        } else {
            fractionalPart *= 60;
        }

        if (basicFormatHour.length() > 4) {
            calendar.set(Calendar.SECOND, Integer.parseInt(basicFormatHour.substring(4, 6)));
        } else {
            fractionalPart *= 60;
        }

        calendar.set(Calendar.MILLISECOND, (int) (fractionalPart * 1000));
    }

    private static int getIndexOfSign(String str) {
        int index = str.indexOf('+');
        return index != -1 ? index : str.indexOf('-');
    }

    private static Calendar parseCalendarDateWithCenturyOnly(Calendar result, String basicFormatDate) {
        result.set(Integer.parseInt(basicFormatDate) * 100, 0, 1);
        return result;
    }

    private static Calendar parseCalendarDateWithYearOnly(Calendar result, String basicFormatDate) {
        result.set(Integer.parseInt(basicFormatDate), 0, 1);
        return result;
    }

    private static Calendar parseCalendarDateWithPrecisionGreaterThanYear(Calendar result, String basicFormatDate, String originalDate) {
        int year = Integer.parseInt(basicFormatDate.substring(0, 4));
        int month = Integer.parseInt(basicFormatDate.substring(4, 6)) - 1;
        if (basicFormatDate.length() == 6) {
            result.set(year, month, 1);
            return result;
        }

        if (basicFormatDate.length() == 8) {
            result.set(year, month, Integer.parseInt(basicFormatDate.substring(6)));
            return result;
        }
        throw new RuntimeException("Can't parse " + originalDate);
    }

    /**
     * To instant .
     *
     * @param toParse the to parse. returns null if toParse is empty or null;
     * @return the instant
     */
    public static Instant toInstant(String toParse) {
        if (!StringUtils.isEmpty(toParse)) {
            return toCalendar(toParse).toInstant();
        } else {
            return null;
        }
    }
}