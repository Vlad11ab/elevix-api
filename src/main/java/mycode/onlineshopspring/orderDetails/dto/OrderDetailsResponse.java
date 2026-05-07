package mycode.onlineshopspring.orderDetails.dto;

import mycode.onlineshopspring.products.dto.ProductsResponse;

import java.util.UUID;

public record OrderDetailsResponse(
        UUID id,
        int price,
        String sku,
        int quantity,
        ProductsResponse product
) {
}
