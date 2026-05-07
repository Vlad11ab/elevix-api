package mycode.onlineshopspring.products.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.exceptions.ProductDoesntExistException;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.products.dto.ProductsDto;
import mycode.onlineshopspring.products.dto.ProductsResponse;
import mycode.onlineshopspring.products.models.Products;
import mycode.onlineshopspring.products.repository.ProductsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductsCommandServiceImpl implements ProductsCommandService {

    private final ProductsRepository repository;
    private final OnlineShopMapper mapper;

    @Transactional
    @Override
    public ProductsResponse createProduct(ProductsDto dto) {
        Products e = mapper.mapProductsDtoToProducts(dto);
        Products saved = repository.save(e);
        return mapper.mapProductsToProductsResponse(saved);
    }

    @Transactional
    @Override
    public ProductsResponse updateProduct(UUID id, ProductsDto dto) {
        Products existing = repository.findById(id).orElseThrow(ProductDoesntExistException::new);
        existing.setSku(dto.sku());
        existing.setName(dto.name());
        existing.setPrice(dto.price());
        existing.setWeight(dto.weight());
        existing.setDescriptions(dto.descriptions());
        existing.setCategory(dto.category());
        existing.setStock(dto.stock());
        if (dto.createDate() != null) existing.setCreateDate(dto.createDate());
        Products saved = repository.save(existing);
        return mapper.mapProductsToProductsResponse(saved);
    }

    @Transactional
    @Override
    public void deleteProduct(UUID id) {
        Products existing = repository.findById(id).orElseThrow(ProductDoesntExistException::new);
        repository.delete(existing);
    }
}
