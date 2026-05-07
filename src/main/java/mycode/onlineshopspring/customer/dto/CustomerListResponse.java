package mycode.onlineshopspring.customer.dto;

import java.util.List;

public record CustomerListResponse(
        List<CustomerResponse> customers,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize) {
}
