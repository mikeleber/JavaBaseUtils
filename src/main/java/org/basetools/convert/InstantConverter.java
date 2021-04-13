package org.basetools.convert;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * Provides a converter used to transform {@link Date} into different other date formats.
 */
public final class InstantConverter {

    /**
     * Default constructor with no external visibility.
     */
    private InstantConverter() {
        super();
    }

    /**
     * To date date.
     *
     * @param instant the instant
     * @return the date
     */
    public static Date toDate(Instant instant) {
        Objects.requireNonNull(instant);
        return LocalDateConverter.toDate(instant.atZone(ZoneId.systemDefault()).toLocalDate(), (Date) null);
    }

    /**
     * To local date local date.
     *
     * @param instant the instant
     * @return the local date
     */
    public static LocalDate toLocalDate(Instant instant) {
        Objects.requireNonNull(instant);
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * To local date time local date time.
     *
     * @param instant the instant
     * @return the local date time
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        Objects.requireNonNull(instant);
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * See also {@link java.time.Instant#parse(CharSequence)}.
     *
     * @param dateString the date string
     * @return the instant or null if dateString is null
     */
    public static Instant fromString(CharSequence dateString) {
        if (dateString != null) {
            return Instant.parse(dateString);
        } else {
            return null;
        }
    }

    /**
     * Creates a String representation of the given startTime in following format: 	'2011-12-03T10:15:30+01:00[Europe/Paris]'
     *
     * @param current the current
     * @return string
     */
    public static String toISODateTimeString(Instant current) {
        LocalDateTime lDate = InstantConverter.toLocalDateTime(current);
        return lDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
