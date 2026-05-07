package mycode.onlineshopspring.auth.permission.exceptions;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(String identifier) {
        super("Permission not found: " + identifier);
    }
}
