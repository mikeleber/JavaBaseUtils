package org.basetools.util.util.xml.diffing;

public class XmlDifferException extends Exception {

    private static final String MESSAGE = "Error during xml diffing!";

    public XmlDifferException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
