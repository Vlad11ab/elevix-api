package mycode.onlineshopspring.auth.permission.exceptions;

public class PermissionAlreadyExistsException extends RuntimeException {
    public PermissionAlreadyExistsException(String name) {
        super("Permission already exists: " + name);
    }
}
