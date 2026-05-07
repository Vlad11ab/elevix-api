package mycode.onlineshopspring.products.service;

import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.products.dto.ProductsListResponse;
import mycode.onlineshopspring.products.dto.ProductsResponse;
import mycode.onlineshopspring.products.models.Products;
import mycode.onlineshopspring.products.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductsQuerryServiceImplTest {

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private OnlineShopMapper mapper;

    @InjectMocks
    private ProductsQuerryServiceImpl service;

    private Products product;
    private List<ProductsResponse> mappedResponse;
    private Page<Products> page;

    @BeforeEach
    void setUp() {
        // ARRANGE
        UUID productId = UUID.randomUUID();
        product = new Products();
        product.setId(productId);
        product.setSku("SKU-1");
        product.setName("Product");
        product.setPrice(100);
        product.setWeight(10);
        product.setDescriptions("Desc");
        product.setCategory("Cat");
        product.setCreateDate(new Date());
        product.setStock(5);

        page = new PageImpl<>(List.of(product), PageRequest.of(0, 1, Sort.by("id")), 1);
        when(productsRepository.findAll(any(Pageable.class))).thenReturn(page);

        mappedResponse = List.of(new ProductsResponse(productId, "SKU-1", "Product", 100, 10, "Desc", "Cat", new Date(), 5));
        when(mapper.mapProductsListToResponseList(page.getContent())).thenReturn(mappedResponse);
    }

    @Test
    void findAllProductsSanitizesPaginationAndMapsResult() {
        // ACT
        ProductsListResponse response = service.findAllProducts(-3, 0);

        // ASSERT
        assertEquals(mappedResponse, response.productsList());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());
        assertEquals(0, response.pageNumber());
        assertEquals(1, response.pageSize());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productsRepository).findAll(pageableCaptor.capture());
        Pageable requested = pageableCaptor.getValue();
        assertEquals(0, requested.getPageNumber());
        assertEquals(1, requested.getPageSize());
        assertEquals(Sort.by("id"), requested.getSort());
        verify(mapper).mapProductsListToResponseList(page.getContent());
    }
}
