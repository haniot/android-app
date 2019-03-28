package br.edu.uepb.nutes.haniot.exception;

public class LocalPreferenceException extends RuntimeException {
    public LocalPreferenceException() {
    }

    public LocalPreferenceException(String message) {
        super(message);
    }

    public LocalPreferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
