package org.leber.convert;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

public class ZonedDateTimeConverter {
    /**
     * Converts a given {@link LocalDate} to a {@link Date}. Null values will be handled using a default
     * or even returning null.
     *
     * @param dateToConvert the date that should be converted which can be null (in that case the default specified
     *                      date will be converted).
     * @param defaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link Date}, can be null.
     */
    public static Date toDate(ZonedDateTime dateToConvert, Date defaultDate) {
        if (dateToConvert == null) {
            return defaultDate;
        } else {
            return new Date(dateToConvert.toInstant().toEpochMilli());
        }
    }
}
