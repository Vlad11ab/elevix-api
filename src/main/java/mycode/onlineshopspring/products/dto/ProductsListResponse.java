package mycode.onlineshopspring.products.dto;

import java.util.List;

public record ProductsListResponse(
        List<ProductsResponse> productsList,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize) {
}
