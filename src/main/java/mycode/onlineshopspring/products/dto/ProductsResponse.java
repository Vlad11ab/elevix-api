package mycode.onlineshopspring.products.dto;

import java.util.Date;
import java.util.UUID;

public record ProductsResponse(
        UUID id,
        String sku,
        String name,
        int price,
        int weight,
        String descriptions,
        String category,
        Date createDate,
        int stock
) {
}
