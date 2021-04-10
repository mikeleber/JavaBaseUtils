package org.leber.convert;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Provides a converter used to transform {@link Date} into different other date formats.
 */
public final class TimestampConverter {
    private static final SimpleDateFormat ISO_TIMESTAMP_FORMAT_ZONED = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final SimpleDateFormat ISO_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Default constructor with no external visibility.
     */
    private TimestampConverter() {
        super();
    }

    /**
     * To iso timestamp string with the format "yyyy-MM-dd'T'HH:mm:ss.SSS" using the default timezone.
     *
     * @param timestamp the timestamp
     * @return
     */
    public static String toIsoTimestampString(long timestamp) {
        return ISO_TIMESTAMP_FORMAT_ZONED.format(new Date(timestamp));
    }

    /**
     * To iso timestamp string string.
     *
     * @param timestamp the timestamp
     * @param zoned
     * @return timestamp with format "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" for zoned, "yyyy-MM-dd'T'HH:mm:ss.SSS" for not zoned
     */
    public static String toIsoTimestampString(Timestamp timestamp, boolean zoned) {
        if (zoned) {
            return ISO_TIMESTAMP_FORMAT_ZONED.format(timestamp);
        } else {
            return ISO_TIMESTAMP_FORMAT.format(timestamp);
        }
    }

    /**
     * Creates a timestamp formatted as a ISO string.
     *
     * @return the current date/time converted into an ISO string.
     */
    public static String currentTimeAsIsoTimestamp() {

        return ISO_TIMESTAMP_FORMAT_ZONED.format(new Date());
    }

    public static LocalDateTime toLocalDateTime(Timestamp date) {
        if (date == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    public static LocalDate toLocalDate(Timestamp date) {
        if (date == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    public static Date toDate(Timestamp timestamp) {
        return DateConverter.removeTime(new Date((timestamp).getTime()));
    }

    /**
     * Parse from iso timestamp date.
     *
     * @param dateString the date string
     * @return
     * @throws ParseException the parse exception
     */
    public static Date parseFromIsoTimestamp(String dateString) throws ParseException {
        synchronized (ISO_TIMESTAMP_FORMAT_ZONED) {
            return ISO_TIMESTAMP_FORMAT_ZONED.parse(dateString);
        }
    }

    /**
     * Format string.
     *
     * @param aDate the a date
     * @return
     * @throws ParseException the parse exception
     */
    public static String format(Date aDate) throws ParseException {
        synchronized (ISO_TIMESTAMP_FORMAT_ZONED) {
            return ISO_TIMESTAMP_FORMAT_ZONED.format(aDate);
        }
    }

    /**
     * Format string.
     *
     * @param aTime the a time
     * @return
     * @throws ParseException the parse exception
     */
    public static String format(long aTime) throws ParseException {
        synchronized (ISO_TIMESTAMP_FORMAT_ZONED) {
            return ISO_TIMESTAMP_FORMAT_ZONED.format(aTime);
        }
    }
}
