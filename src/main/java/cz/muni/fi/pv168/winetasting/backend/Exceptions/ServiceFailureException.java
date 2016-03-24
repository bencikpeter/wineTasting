package cz.muni.fi.pv168.winetasting.backend.Exceptions;

/**
 * Created by lukas on 3/23/16.
 */
public class ServiceFailureException extends RuntimeException {
    public ServiceFailureException() {
    }

    public ServiceFailureException(String message) {
        super(message);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
