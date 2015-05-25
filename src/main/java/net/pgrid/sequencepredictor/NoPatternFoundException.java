package net.pgrid.sequencepredictor;

/**
 * Exception type indicating no pattern has been found.
 * @author Patrick Kramer
 */
public class NoPatternFoundException extends Exception {

    public NoPatternFoundException() {
    }

    public NoPatternFoundException(String message) {
        super(message);
    }

    public NoPatternFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPatternFoundException(Throwable cause) {
        super(cause);
    }
    
    
}
