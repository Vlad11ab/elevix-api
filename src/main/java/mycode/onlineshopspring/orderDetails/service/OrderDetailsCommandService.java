package mycode.onlineshopspring.orderDetails.service;

import mycode.onlineshopspring.orderDetails.dto.OrderDetailsDto;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;

import java.util.UUID;

public interface OrderDetailsCommandService {
    OrderDetailsResponse createOrderDetails(OrderDetailsDto dto);
    void deleteOrderDetail(UUID id);
}
