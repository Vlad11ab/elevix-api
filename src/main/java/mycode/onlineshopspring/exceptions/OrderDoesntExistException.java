package mycode.onlineshopspring.exceptions;

import mycode.onlineshopspring.constants.ShopConstants;

public class OrderDoesntExistException extends RuntimeException {
    public OrderDoesntExistException() {
        super(ShopConstants.ORDER_DOESNT_EXIST);
    }
}
