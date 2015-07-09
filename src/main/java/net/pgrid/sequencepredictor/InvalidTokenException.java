package net.pgrid.sequencepredictor;

/**
 * Exception thrown to indicate a token could not be parsed.
 * @author Patrick Kramer
 */
public class InvalidTokenException extends Exception {

    private String token = null;
    
    public InvalidTokenException() {
    }
    
    public InvalidTokenException(String message) {
        super(message);
    }
    
    /**
     * Initializes a new instance of the {@code InvalidTokenException} class
     * with the invalid token and an explanation indicating why the token is 
     * invalid.
     * 
     * @param token       The invalid token.
     * @param explanation An explanation indicating why the token is invalid.
     */
    public InvalidTokenException(String token, String explanation) {
        super(createMessage(token, explanation));
        this.token = token;
    }

    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Initializes a new instance of the {@code InvalidTokenException} class
     * with the invalid token and an explanation indicating why the token is 
     * invalid.
     * 
     * @param token       The invalid token.
     * @param explanation An explanation indicating why the token is invalid.
     * @param cause       The Throwable that caused this Exception to be thrown.
     */
    public InvalidTokenException(String token, String explanation, Throwable cause) {
        super(createMessage(token, explanation), cause);
        this.token = token;
    }
    
    /**
     * Returns the invalid token that caused this Exception to be thrown, if
     * and only if this Exception was initialized using the 
     * {@code InvalidTokenException(String, String)} constructor.
     * <p>
     * Otherwise, this method always returns null.
     * </p>
     * @return The invalid token that caused this Exception. 
     */
    public String getToken() {
        return token;
    }
    
    /**
     * Creates an Exception message from the provided token and explanation.
     * @param token       The invalid token.
     * @param explanation An explanation indicating why the token is invalid.
     * @return            The created Exception message.
     */
    private static String createMessage(String token, String explanation) {
        return "Invalid Token \"" + token + "\": " + explanation;
    }
}
