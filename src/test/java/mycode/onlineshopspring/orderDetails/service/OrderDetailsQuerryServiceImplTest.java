package mycode.onlineshopspring.orderDetails.service;

import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsListResponse;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import mycode.onlineshopspring.orderDetails.repository.OrderDetailsRepository;
import mycode.onlineshopspring.products.dto.ProductsResponse;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDetailsQuerryServiceImplTest {

    @Mock
    private OrderDetailsRepository orderDetailsRepository;

    @Mock
    private OnlineShopMapper mapper;

    @InjectMocks
    private OrderDetailsQuerryServiceImpl service;

    private OrderDetails detail;
    private List<OrderDetailsResponse> mappedResponse;
    private Page<OrderDetails> page;

    @BeforeEach
    void setUp() {
        // ARRANGE
        UUID detailId = UUID.randomUUID();
        detail = new OrderDetails();
        detail.setId(detailId);
        detail.setPrice(22);
        detail.setSku("SKU-ITEM");
        detail.setQuantity(5);

        page = new PageImpl<>(List.of(detail), PageRequest.of(0, 1, Sort.by("id")), 1);
        when(orderDetailsRepository.findAll(any(Pageable.class))).thenReturn(page);

        mappedResponse = List.of(new OrderDetailsResponse(detailId, 22, "SKU-ITEM", 5,
                new ProductsResponse(UUID.randomUUID(), "SKU", "Prod", 10, 1, "Desc", "Cat", new java.util.Date(), 10)));
        when(mapper.mapOrderDetailsListToResponseList(page.getContent())).thenReturn(mappedResponse);
    }

    @Test
    void findAllOrderDetailsSanitizesPaginationAndMapsResult() {
        // ACT
        OrderDetailsListResponse response = service.findAllOrderDetails(-2, 0);

        // ASSERT
        assertEquals(mappedResponse, response.orderDetailsList());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());
        assertEquals(0, response.pageNumber());
        assertEquals(1, response.pageSize());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderDetailsRepository).findAll(pageableCaptor.capture());
        Pageable requested = pageableCaptor.getValue();
        assertEquals(0, requested.getPageNumber());
        assertEquals(1, requested.getPageSize());
        assertEquals(Sort.by("id"), requested.getSort());
        verify(mapper).mapOrderDetailsListToResponseList(page.getContent());
    }
}
