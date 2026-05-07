package mycode.onlineshopspring.products.service;

import mycode.onlineshopspring.products.dto.ProductsDto;
import mycode.onlineshopspring.products.dto.ProductsResponse;

import java.util.UUID;

public interface ProductsCommandService {
    ProductsResponse createProduct(ProductsDto dto);
    ProductsResponse updateProduct(UUID id, ProductsDto dto);
    void deleteProduct(UUID id);
}
