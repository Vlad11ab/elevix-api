package mycode.onlineshopspring.admin.exceptions;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException() {
        super("Admin does not exist in the database.");
    }
}
