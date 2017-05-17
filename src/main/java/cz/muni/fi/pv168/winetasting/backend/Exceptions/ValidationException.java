package cz.muni.fi.pv168.winetasting.backend.Exceptions;

/**
 * This exception is thrown when validation of entity fails.
 *
 * @author lukas
 */
public class ValidationException extends RuntimeException {

    /**
     * Creates a new instance of
     * ValidationException without detail message.
     */
    public ValidationException() {

    }

    /**
     * Constructs an instance of
     * ValidationException with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ValidationException(String msg) {
        super(msg);
    }
}


