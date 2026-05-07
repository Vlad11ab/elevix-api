package mycode.onlineshopspring.auth.exceptions;

import lombok.Getter;

@Getter
public class MissingPermissionException extends RuntimeException {

    private final String requiredPermission;

    public MissingPermissionException(String requiredPermission) {
        super("Missing permission: " + requiredPermission + ". Ask an administrator to grant it.");
        this.requiredPermission = requiredPermission;
    }
}
