package mycode.onlineshopspring.customer.dto;

import mycode.onlineshopspring.orders.dto.OrdersResponse;

import java.util.Set;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String email,
        String fullName,
        String billingAddress,
        String defaultShippingAddress,
        String country,
        String phone,
        Set<OrdersResponse> orderSet
) {
}
