package mycode.onlineshopspring.orders.service;

import mycode.onlineshopspring.orders.dto.OrdersListResponse;
import mycode.onlineshopspring.orders.dto.OrdersResponse;

import java.util.UUID;

public interface OrdersQuerryService {
    OrdersListResponse findAllOrders(int page, int size);
    OrdersResponse findOrderById(UUID id);
    OrdersListResponse findOrdersByCustomerId(UUID customerId, int page, int size);
    OrdersListResponse findOrdersByEmail(String email, int page, int size);
}
