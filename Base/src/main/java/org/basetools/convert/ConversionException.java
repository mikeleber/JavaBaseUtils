package org.basetools.convert;

/**
 * Provides a specific exception we use to encapsulate all kinds of exceptions that might occur during conversion.
 */
public class ConversionException extends RuntimeException {

    /**
     * SUID.
     */
    private static final long serialVersionUID = -4297238243543205993L;

    /**
     * See {@link Exception#Exception()}
     */
    public ConversionException() {
        super();
    }

    /**
     * See {@link Exception#Exception(String)}
     */
    public ConversionException(String inMessage) {
        super(inMessage);
    }

    /**
     * See {@link Exception#Exception(String, Throwable)}
     */
    public ConversionException(String inMessage, Throwable inCause) {
        super(inMessage,
                inCause);
    }

    /**
     * See {@link Exception#Exception(String, Throwable, boolean, boolean)}
     */
    public ConversionException(String inMessage, Throwable inCause, boolean inEnableSuppression, boolean inWritableStackTrace) {
        super(inMessage,
                inCause,
                inEnableSuppression,
                inWritableStackTrace);
    }

    /**
     * See {@link Exception#Exception(Throwable)}
     */
    public ConversionException(Throwable inCause) {
        super(inCause);
    }
}
