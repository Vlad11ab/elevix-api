package mycode.onlineshopspring.orders.dto;

import mycode.onlineshopspring.orderDetails.dto.OrderDetailsDto;

import java.time.LocalDate;
import java.util.Set;

public record OrdersDto(
        int ammount,
        String shippingAdress,
        String orderAddress,
        String orderEmail,
        LocalDate order_date,
        String orderStatus,
        Set<OrderDetailsDto> orderDetailsSet
) {
}
