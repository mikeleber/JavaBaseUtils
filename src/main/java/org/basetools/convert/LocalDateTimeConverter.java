package org.basetools.convert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Provides a converter used to transform local dates into different other date formats.
 */
public final class LocalDateTimeConverter {

    /**
     * Default constructor with no external visibility.
     */
    private LocalDateTimeConverter() {
        super();
    }

    /**
     * Creates a String representation of the given startTime in following format: 	'2011-12-03T10:15:30+01:00[Europe/Paris]'
     *
     * @param date
     * @return
     */
    public static String toISODateTimeString(LocalDateTime date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
