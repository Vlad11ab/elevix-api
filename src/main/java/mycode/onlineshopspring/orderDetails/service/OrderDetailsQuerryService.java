package mycode.onlineshopspring.orderDetails.service;

import mycode.onlineshopspring.orderDetails.dto.OrderDetailsListResponse;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;

import java.util.UUID;

public interface OrderDetailsQuerryService {
    OrderDetailsListResponse findAllOrderDetails(int page, int size);
    OrderDetailsResponse findOrderDetailById(UUID id);
}
