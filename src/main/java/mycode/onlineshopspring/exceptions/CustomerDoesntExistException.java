package mycode.onlineshopspring.exceptions;
import mycode.onlineshopspring.constants.ShopConstants;
public class CustomerDoesntExistException extends RuntimeException{
    public CustomerDoesntExistException(){super(ShopConstants.CUSTOMER_DOESNT_EXIST);}
}
// Repetați structura pentru ProductDoesntExistException, OrderDoesntExistException, etc.