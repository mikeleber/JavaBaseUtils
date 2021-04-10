package org.leber.convert;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Provides a converter used to transform local dates into different other date formats.
 */
public final class XMLGregorianDateConverter {

    /**
     * Default constructor with no external visibility.
     */
    private XMLGregorianDateConverter() {
        super();
    }

    /**
     * Converts a given {@link XMLGregorianCalendar} to a {@link LocalDate}. Null values will be handled using a default or
     * even returning null.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param inDefaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link LocalDate}, can be null.
     */
    public static LocalDate toLocalDate(XMLGregorianCalendar inDateToConvert, XMLGregorianCalendar inDefaultDate) {
        if (inDateToConvert == null) {

            if (inDefaultDate == null) {
                return null;
            }

            // months start with 0 in calendar but with 1 in local dates
            GregorianCalendar aCalendar = inDefaultDate.toGregorianCalendar();
            return LocalDate.of(aCalendar.get(Calendar.YEAR), aCalendar.get(Calendar.MONTH) + 1, aCalendar.get(Calendar.DAY_OF_MONTH));
        } else {
            // months start with 0 in calendar but with 1 in local dates
            GregorianCalendar aCalendar = inDateToConvert.toGregorianCalendar();
            return LocalDate.of(aCalendar.get(Calendar.YEAR), aCalendar.get(Calendar.MONTH) + 1, aCalendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * Converts a given {@link XMLGregorianCalendar} to a {@link LocalDate}. Null values will be handled using a default or
     * even returning null.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param defaultDate     the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link LocalDate}, can be null.
     */
    public static LocalDate toLocalDate(XMLGregorianCalendar inDateToConvert, LocalDate defaultDate) {
        if (inDateToConvert == null) {
            return defaultDate;
        } else {
            return toLocalDate(inDateToConvert, (XMLGregorianCalendar) null);
        }
    }

    /**
     * Converts a given {@link XMLGregorianCalendar} to a {@link Date}.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @return the converted date, now as {@link Date}, can be null.
     */
    public static Date toDate(XMLGregorianCalendar inDateToConvert) {
        return toDate(inDateToConvert, (Date) null);
    }

    /**
     * Converts a given {@link XMLGregorianCalendar} to a {@link Date}. Null values will be handled using a default
     * or even returning null.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param inDefaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link Date}, can be null.
     */
    public static Date toDate(XMLGregorianCalendar inDateToConvert, XMLGregorianCalendar inDefaultDate) {
        if (inDateToConvert == null) {

            if (inDefaultDate == null) {
                return null;
            }

            // return the default date
            return inDefaultDate.toGregorianCalendar()
                    .getTime();
        } else {
            return inDateToConvert.toGregorianCalendar()
                    .getTime();
        }
    }

    /**
     * Converts a given {@link XMLGregorianCalendar} to a {@link Date}. Null values will be handled using a default
     * or even returning null.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param inDefaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link Date}, can be null.
     */
    public static Date toDate(XMLGregorianCalendar inDateToConvert, Date inDefaultDate) {
        if (inDateToConvert == null) {
            return inDefaultDate;
        } else {
            return toDate(inDateToConvert, (XMLGregorianCalendar) null);
        }
    }

    /**
     * Converts a given {@link XMLGregorianCalendar} to a {@link ZonedDateTime}. Null values will be handled using a default
     * or even returning null.
     *
     * @param dateToConvert   the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param defaultDateTime the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link ZonedDateTime}, can be null.
     */
    public static ZonedDateTime toZonedDateTime(XMLGregorianCalendar dateToConvert, ZonedDateTime defaultDateTime) {
        if (dateToConvert == null) {
            return defaultDateTime;
        } else {
            return dateToConvert.toGregorianCalendar().toZonedDateTime();
        }
    }

    /**
     * Converts a given {@link XMLGregorianCalendar} to a {@link ZonedDateTime}. Null values will be handled using a default
     * or even returning null.
     *
     * @param dateToConvert the date that should be converted which can be null (in that case the default specified
     *                      date will be converted).
     * @return the converted date, now as {@link ZonedDateTime}, can be null.
     */
    public static ZonedDateTime toZonedDateTime(XMLGregorianCalendar dateToConvert) {
        return toZonedDateTime(dateToConvert, null);
    }
}
