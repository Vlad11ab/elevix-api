package mycode.onlineshopspring.orderDetails.dto;

import java.util.List;

public record OrderDetailsListResponse(
        List<OrderDetailsResponse> orderDetailsList,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize) {
}
