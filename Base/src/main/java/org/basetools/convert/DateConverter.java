package org.basetools.convert;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Provides a converter used to transform {@link Date} into different other date formats.
 */
public final class DateConverter {

    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Default constructor with no external visibility.
     */
    private DateConverter() {
        super();
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
    @Deprecated
    public static Date convertFromXmlGregorianCalender(XMLGregorianCalendar inDateToConvert, XMLGregorianCalendar inDefaultDate) {
        return XMLGregorianDateConverter.toDate(inDateToConvert, inDefaultDate);
    }

    /**
     * Converts a date into a string.
     *
     * @param timestamp the date that should be converted.
     * @return the date converted into an ISO string.
     */
    public static String toIsoTimestampString(Date timestamp) throws ParseException {
        if (timestamp == null) {
            return null;
        } else {
            return TimestampConverter.format(timestamp);
        }
    }

    /**
     * Converts a date into a string.
     *
     * @param timestamp the date that should be converted.
     * @return the date converted into an ISO string.
     */
    public static String toIsoTimestampString(long timestamp) {
        try {
            return TimestampConverter.format(timestamp);
        } catch (ParseException pe) {
            return null;
        }
    }

    /**
     * Try to Convert a given {@link Object} to a {@link Date}. Null values will be handled using a default
     * or even returning null. Throws a {@link ConversionException}, if parsing has failed.
     *
     * @param dateString    the date that should be converted which can be null (in that case the default specified
     *                      date will be converted).
     * @param inDefaultDate the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link Date}, can be null.
     */
    public static Date convertToDate(String dateString, Date inDefaultDate) {
        if (dateString == null) {
            // if no date is given return default without any conversions!
            return inDefaultDate;
        } else {
            try {
                return parseISODate(dateString);
            } catch (ParseException pe) {
                throw new ConversionException("A problem occurred parsing an ISO date.", pe);
            }
        }
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    /**
     * Converts a given {@link LocalDate} to a {@link XMLGregorianCalendar}. Null values will be handled using a default
     * or even returning null. Throws a {@link ConversionException}, if conversion has failed.
     *
     * @param fromDate    the date that should be converted which can be null (in that case the default specified
     *                    date will be converted).
     * @param defaultDate the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link XMLGregorianCalendar}, can be null.
     */
    public static XMLGregorianCalendar toXMLGregorianCalender(Date fromDate, XMLGregorianCalendar defaultDate) {
        if (fromDate == null) {
            return defaultDate;
        } else {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(fromDate);
            try {
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException e) {
                throw new ConversionException("Invalid date '" + fromDate + "' to convert: {}",
                        e);
            }
        }
    }

    public static Date parseISODate(String dateStr) throws ParseException {
        if (dateStr == null) {
            return null;
        } else {
            return Iso8601Parser.toDate(dateStr);
        }
    }

    public static String format(Date aDate) throws ParseException {
        synchronized (ISO_DATE_FORMAT) {
            return ISO_DATE_FORMAT.format(aDate);
        }
    }

    public static String format(long aTime) throws ParseException {
        synchronized (ISO_DATE_FORMAT) {
            return ISO_DATE_FORMAT.format(aTime);
        }
    }

    public static Date toDate(Object dateObject) throws ParseException {

        if (dateObject == null) {
            return null;
        } else if (dateObject instanceof String) {
            Long longDate = getLong((String) dateObject);
            if (longDate != null) {
                return new Date(((Long) dateObject).longValue());
            } else {
                return parseISODate(dateObject.toString());
            }
        } else if (dateObject instanceof ZonedDateTime) {
            return ZonedDateTimeConverter.toDate((ZonedDateTime) dateObject, null);
        } else if (dateObject instanceof Long) {
            return new Date(((Long) dateObject).longValue());
        } else if (dateObject instanceof XMLGregorianCalendar) {
            return XMLGregorianDateConverter.toDate((XMLGregorianCalendar) dateObject);
        } else if (dateObject instanceof Timestamp) {
            return TimestampConverter.toDate((Timestamp) dateObject);
        } else if (dateObject instanceof LocalDate) {
            return LocalDateConverter.toDate((LocalDate) dateObject);
        } else if (dateObject instanceof Instant) {
            return InstantConverter.toDate((Instant) dateObject);
        }
        return null;
    }

    private static Long getLong(String dateObject) {
        try {
            return Long.valueOf(dateObject).longValue();
        } catch (Exception e) {
            //ignore it!
            return null;
        }
    }
}
