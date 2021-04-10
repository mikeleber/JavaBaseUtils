package org.leber.convert;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Provides a converter used to transform local dates into different other date formats.
 */
public final class LocalDateConverter {

    /**
     * Default constructor with no external visibility.
     */
    private LocalDateConverter() {
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
    @Deprecated
    public static LocalDate convertFromXmlGregorianCalender(XMLGregorianCalendar inDateToConvert, XMLGregorianCalendar inDefaultDate) {
        return XMLGregorianDateConverter.toLocalDate(inDateToConvert, inDefaultDate);
    }

    /**
     * Converts a given {@link LocalDate} to a {@link XMLGregorianCalendar}. Null values will be handled using a default
     * or even returning null.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param inDefaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link XMLGregorianCalendar}, can be null.
     */
    @Deprecated
    public static XMLGregorianCalendar convertToXmlGregorianCalender(LocalDate inDateToConvert, LocalDate inDefaultDate) {
        return toGregorianCalender(inDateToConvert, inDefaultDate);
    }

    /**
     * Converts a given {@link LocalDate} to a {@link XMLGregorianCalendar}. Null values will be handled using a default
     * or even returning null. Throws a {@link ConversionException}, if conversion has failed.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param inDefaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link XMLGregorianCalendar}, can be null.
     */
    public static XMLGregorianCalendar toGregorianCalender(LocalDate inDateToConvert, LocalDate inDefaultDate) {
        if (inDateToConvert == null) {

            if (inDefaultDate == null) {
                return null;
            }
            try {
                // return the default date
                return DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(inDefaultDate.toString());
            } catch (DatatypeConfigurationException aCalendarProblem) {

                throw new ConversionException("Invalid default date '" + inDefaultDate + "' to convert: {}",
                        aCalendarProblem);
            }
        } else {
            try {
                return DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(inDateToConvert.toString());
            } catch (DatatypeConfigurationException aCalendarProblem) {

                throw new ConversionException("Invalid date '" + inDateToConvert + "' to convert: {}",
                        aCalendarProblem);
            }
        }
    }

    /**
     * Converts a given {@link LocalDate} to a {@link Date}. Null values will be handled using a default
     * or even returning null.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param inDefaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link Date}, can be null.
     */
    @Deprecated
    public static Date convertToDate(LocalDate inDateToConvert, LocalDate inDefaultDate) {
        return toDate(inDateToConvert, inDefaultDate);
    }

    /**
     * Converts a given {@link LocalDate} to a {@link Date}. Null values will be handled using a default
     * or even returning null.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param inDefaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link Date}, can be null.
     */
    public static Date toDate(LocalDate inDateToConvert, LocalDate inDefaultDate) {
        if (inDateToConvert == null) {

            if (inDefaultDate == null) {
                return null;
            }

            return Date.from(inDefaultDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        } else {

            return Date.from(inDateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    /**
     * Converts a given {@link LocalDate} to a {@link Date}.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @return the converted date, now as {@link Date}, can be null.
     */
    public static Date toDate(LocalDate inDateToConvert) {
        return toDate(inDateToConvert, (Date) null);
    }

    /**
     * Converts a given {@link LocalDate} to a {@link Date}. Null values will be handled using a default
     * or even returning null.
     *
     * @param inDateToConvert the date that should be converted which can be null (in that case the default specified
     *                        date will be converted).
     * @param inDefaultDate   the default value we will use if no date to convert has been specified.
     * @return the converted date, now as {@link Date}, can be null.
     */
    public static Date toDate(LocalDate inDateToConvert, Date inDefaultDate) {
        if (inDateToConvert == null) {

            return inDefaultDate;
        } else {

            return toDate(inDateToConvert, (LocalDate) null);
        }
    }
}
