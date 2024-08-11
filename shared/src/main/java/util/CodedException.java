package util;

public class CodedException extends Exception {
    final private int statusCode;

    public CodedException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
