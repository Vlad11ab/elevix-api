package mycode.onlineshopspring.orders.service;

import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;
import mycode.onlineshopspring.orders.dto.OrdersListResponse;
import mycode.onlineshopspring.orders.dto.OrdersResponse;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersQuerryServiceImplTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OnlineShopMapper mapper;

    @InjectMocks
    private OrdersQuerryServiceImpl service;

    private Orders sampleOrder;
    private List<OrdersResponse> mappedResponse;
    private Page<Orders> page;

    @BeforeEach
    void setUp() {
        // ARRANGE
        UUID orderId = UUID.randomUUID();
        sampleOrder = new Orders();
        sampleOrder.setId(orderId);
        sampleOrder.setAmmount(220);
        sampleOrder.setShippingAdress("Ship 1");
        sampleOrder.setOrderAdress("Addr 1");
        sampleOrder.setOrderEmail("order@example.com");
        sampleOrder.setOrder_date(LocalDate.now());
        sampleOrder.setOrderStatus("CREATED");

        page = new PageImpl<>(List.of(sampleOrder), PageRequest.of(0, 1, Sort.by("id")), 1);
        when(ordersRepository.findAll(any(Pageable.class))).thenReturn(page);

        mappedResponse = List.of(new OrdersResponse(
                orderId,
                220,
                "Ship 1",
                "Addr 1",
                "order@example.com",
                LocalDate.now(),
                "CREATED",
                Set.of(new OrderDetailsResponse(UUID.randomUUID(), 10, "SKU", 2,
                        new ProductsResponse(UUID.randomUUID(), "SKU-P", "Prod", 10, 1, "Desc", "Cat", new java.util.Date(), 5)))
        ));
        when(mapper.mapOrdersListToResponseList(page.getContent())).thenReturn(mappedResponse);
    }

    @Test
    void findAllOrdersSanitizesPaginationAndMapsResult() {
        // ACT
        OrdersListResponse response = service.findAllOrders(-1, 0);

        // ASSERT
        assertEquals(mappedResponse, response.ordersList());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());
        assertEquals(0, response.pageNumber());
        assertEquals(1, response.pageSize());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(ordersRepository).findAll(pageableCaptor.capture());
        Pageable requested = pageableCaptor.getValue();
        assertEquals(0, requested.getPageNumber());
        assertEquals(1, requested.getPageSize());
        assertEquals(Sort.by("id"), requested.getSort());
        verify(mapper).mapOrdersListToResponseList(page.getContent());
    }
}
