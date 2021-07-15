package io.tunabytes.bytecode;

public class ReadClassException extends RuntimeException {

    public ReadClassException(String message) {
        super(message);
    }

    public ReadClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
