package mycode.onlineshopspring.auth.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh token is invalid or expired.");
    }
}
