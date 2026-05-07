package mycode.onlineshopspring.auth.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException() {
        super("Email is already in use.");
    }
}
