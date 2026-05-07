package mycode.onlineshopspring.products.service;

import mycode.onlineshopspring.products.dto.ProductsListResponse;
import mycode.onlineshopspring.products.dto.ProductsResponse;

import java.util.UUID;

public interface ProductsQuerryService {
    ProductsListResponse findAllProducts(int page, int size);
    ProductsResponse findProductById(UUID id);
}
