package org.basetools.parser;

import org.basetools.convert.LocalDateConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Provides a parser used to convert strings into date objects.
 */
public class DateParser {

    /**
     * Defines the pattern we use to part a base date.
     */
    private static final String BASE_DATE_FORMAT = "yyyyMMdd";

    /**
     * Provides the format used by base to format a date as string.
     */
    private static final DateTimeFormatter BASE_DATE_FORMATTER = DateTimeFormatter.ofPattern(BASE_DATE_FORMAT);

    /**
     * Default constructor hiding visibility.
     */
    private DateParser() {
        super();
    }

    /**
     * Parses a date as specified base in the format of "yyyyMMdd".
     *
     * @param inDateToParse the text that should be parsed, can be null or empty.
     * @return either the parsed date or null.
     */
    public static Date fromBaseDate(String inDateToParse) {

        if (inDateToParse == null) {
            return null;
        }

        if (inDateToParse.isEmpty()) {
            return null;
        }

        try {
            final LocalDate parsedDate = LocalDate.parse(inDateToParse, BASE_DATE_FORMATTER);
            return LocalDateConverter.toDate(parsedDate);
        } catch (DateTimeParseException aParseProblem) {
            // convert to illegal argument to be less dependent
            throw new IllegalArgumentException(String.format("Could not parse date, expectedFormat=%s, dateToParse=%s", BASE_DATE_FORMAT, inDateToParse),
                    aParseProblem);
        }
    }
}
