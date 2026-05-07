package mycode.onlineshopspring.exceptions;
import mycode.onlineshopspring.constants.ShopConstants;
public class ProductDoesntExistException extends RuntimeException{
    public ProductDoesntExistException(){super(ShopConstants.PRODUCT_DOESNT_EXIST);}
}
// Repetați structura pentru ProductDoesntExistException, OrderDoesntExistException, etc.