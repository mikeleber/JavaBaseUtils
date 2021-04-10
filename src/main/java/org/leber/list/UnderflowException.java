package org.leber.list;

public class UnderflowException extends RuntimeException {
    public UnderflowException(String message) {
        super(message);
    }
}