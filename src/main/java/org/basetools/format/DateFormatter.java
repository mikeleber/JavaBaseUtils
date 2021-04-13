package org.basetools.format;

import org.basetools.convert.InstantConverter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatter {

    public static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter ISO_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static final DateTimeFormatter ISO_TIMESTAMP_FORMAT_WITHOUT_ZONE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private DateFormatter() {
        // Utility classes shouldn't be instantiatable
    }

    /**
     * Creates a String representation of the given {@link LocalDateTime} in following format:
     * {@code '2011-12-03T10:15:30'}.
     */
    public static String toISODateTimeString(LocalDateTime date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Creates a String representation of the given startTime in following format:
     * {@code '2011-12-03T10:15:30+01:00[Europe/Paris]'}.
     */
    public static String toISODateTimeString(Instant current) {
        LocalDateTime lDate = InstantConverter.toLocalDateTime(current);
        return lDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * To iso timestamp string with the format "yyyy-MM-dd'T'HH:mm:ss.SSS" using the default timezone.
     *
     * @param timestamp The timestamp
     */
    public static String toIsoTimestampString(long timestamp) {
        return toIsoTimestampString(new Date(timestamp));
    }

    public static String toIsoTimestampString(Date date) {
        return ISO_TIMESTAMP_FORMAT.format(date.toInstant()
                .atZone(ZoneId.systemDefault()));
    }

    /**
     * To iso timestamp string string.
     *
     * @param timestamp The timestamp
     * @param zoned     Whether to return the timestamp with or without timezone information
     * @return timestamp with format "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" for zoned, "yyyy-MM-dd'T'HH:mm:ss.SSS" otherwise
     */
    public static String toIsoTimestampString(Timestamp timestamp, boolean zoned) {
        if (zoned) {
            return toIsoTimestampString(timestamp.getTime());
        } else {
            return ISO_TIMESTAMP_FORMAT_WITHOUT_ZONE.format(timestamp.toInstant()
                    .atZone(ZoneId.systemDefault()));
        }
    }

    /**
     * Creates a timestamp formatted as a ISO string.
     *
     * @return the current date/time converted into an ISO string.
     */
    public static String currentTimeAsIsoTimestamp() {
        return toIsoTimestampString(new Date());
    }

    public static String toIsoDateString(Date aDate) {
        return ISO_DATE_FORMAT.format(aDate.toInstant()
                .atZone(ZoneId.systemDefault()));
    }

    public static String toIsoDateString(long aTime) {
        return toIsoDateString(new Date(aTime));
    }
}
