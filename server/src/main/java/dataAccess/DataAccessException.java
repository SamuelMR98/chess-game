package dataAccess;

/**
 * Represents an exception that occurs when accessing data
 */
public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }
}
