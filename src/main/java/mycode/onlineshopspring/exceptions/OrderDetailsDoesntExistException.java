package mycode.onlineshopspring.exceptions;

import mycode.onlineshopspring.constants.ShopConstants;

public class OrderDetailsDoesntExistException extends RuntimeException {
    public OrderDetailsDoesntExistException() {
        super(ShopConstants.ORDER_DETAILS_DOESNT_EXIST);
    }
}
