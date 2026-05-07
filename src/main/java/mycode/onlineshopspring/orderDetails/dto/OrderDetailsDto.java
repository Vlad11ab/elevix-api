package mycode.onlineshopspring.orderDetails.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderDetailsDto(
        int price,
        String sku,
        int quantity,
        @NotNull(message = "Product ID is required") UUID productId
) {
}
