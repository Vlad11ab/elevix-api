package mycode.onlineshopspring.orders.dto;

import java.util.List;

public record OrdersListResponse(
        List<OrdersResponse> ordersList,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize) {
}
