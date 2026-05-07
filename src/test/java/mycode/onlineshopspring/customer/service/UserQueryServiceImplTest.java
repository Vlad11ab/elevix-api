package mycode.onlineshopspring.customer.service;

import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.customer.dto.CustomerListResponse;
import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.orders.dto.OrdersResponse;
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
class UserQueryServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OnlineShopMapper mapper;

    @InjectMocks
    private UserQueryServiceImpl service;

    private Customer sampleCustomer;
    private List<CustomerResponse> mappedResponse;
    private Page<Customer> page;

    @BeforeEach
    void setUp() {
        // ARRANGE
        UUID customerId = UUID.randomUUID();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("customer@example.com");
        user.setPassword("encoded");
        user.setEnabled(true);

        sampleCustomer = new Customer();
        sampleCustomer.setId(customerId);
        sampleCustomer.setUser(user);
        sampleCustomer.setFullName("John Doe");
        sampleCustomer.setBillingAddress("Billing");
        sampleCustomer.setDefaultShippingAddress("Shipping");
        sampleCustomer.setCountry("Country");
        sampleCustomer.setPhone("1234567890");

        page = new PageImpl<>(List.of(sampleCustomer), PageRequest.of(0, 1, Sort.by("id")), 1);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);

        mappedResponse = List.of(new CustomerResponse(
                customerId,
                "customer@example.com",
                "John Doe",
                "Billing",
                "Shipping",
                "Country",
                "1234567890",
                Set.of(new OrdersResponse(UUID.randomUUID(), 100, "Ship", "Addr", "email@example.com", LocalDate.now(), "CREATED", Set.of()))
        ));
        when(mapper.mapCustomerListToResponseList(page.getContent())).thenReturn(mappedResponse);
    }

    @Test
    void findAllCustomersSanitizesPaginationAndMapsResult() {
        // ACT
        CustomerListResponse response = service.findAllCustomers(-5, 0);

        // ASSERT
        assertEquals(mappedResponse, response.customers());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());
        assertEquals(0, response.pageNumber());
        assertEquals(1, response.pageSize());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageableCaptor.capture());
        Pageable requested = pageableCaptor.getValue();
        assertEquals(0, requested.getPageNumber());
        assertEquals(1, requested.getPageSize());
        assertEquals(Sort.by("id"), requested.getSort());
        verify(mapper).mapCustomerListToResponseList(page.getContent());
    }
}
