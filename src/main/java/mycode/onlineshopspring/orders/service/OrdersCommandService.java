package mycode.onlineshopspring.orders.service;

import mycode.onlineshopspring.orders.dto.OrdersDto;
import mycode.onlineshopspring.orders.dto.OrdersResponse;

import java.util.UUID;

public interface OrdersCommandService {
    OrdersResponse createOrder(UUID customerId, OrdersDto dto);
    OrdersResponse createOrderForCurrentUser(String email, OrdersDto dto);
    OrdersResponse updateOrderStatus(UUID id, String status);
    void deleteOrder(UUID id);
}
