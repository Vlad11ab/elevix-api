package mycode.onlineshopspring.orders.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.common.pagination.PaginationUtils;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.exceptions.CustomerDoesntExistException;
import mycode.onlineshopspring.exceptions.OrderDoesntExistException;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.orders.dto.OrdersListResponse;
import mycode.onlineshopspring.orders.dto.OrdersResponse;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static mycode.onlineshopspring.common.pagination.PaginationUtils.fetchPage;

@Service
@RequiredArgsConstructor
public class OrdersQuerryServiceImpl implements OrdersQuerryService {

    private final OrdersRepository ordersRepository;
    private final CustomerRepository customerRepository;
    private final OnlineShopMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public OrdersListResponse findAllOrders(int page, int size) {
        Pageable pageable = PaginationUtils.sanitize(page, size, Sort.by("id"));
        Page<Orders> ordersPage = fetchPage(ordersRepository::findAll, pageable);
        List<OrdersResponse> list = mapper.mapOrdersListToResponseList(ordersPage.getContent());
        return new OrdersListResponse(list,
                ordersPage.getTotalElements(), ordersPage.getTotalPages(),
                ordersPage.getNumber(), ordersPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersResponse findOrderById(UUID id) {
        Orders order = ordersRepository.findById(id).orElseThrow(OrderDoesntExistException::new);
        return mapper.mapOrdersToOrdersResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersListResponse findOrdersByCustomerId(UUID customerId, int page, int size) {
        Pageable pageable = PaginationUtils.sanitize(page, size, Sort.by("id"));
        Page<Orders> ordersPage = ordersRepository.findByCustomerId(customerId, pageable);
        return toListResponse(ordersPage);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersListResponse findOrdersByEmail(String email, int page, int size) {
        Customer customer = customerRepository.findByUserEmail(email).orElseThrow(CustomerDoesntExistException::new);
        return findOrdersByCustomerId(customer.getId(), page, size);
    }

    private OrdersListResponse toListResponse(Page<Orders> ordersPage) {
        List<OrdersResponse> list = mapper.mapOrdersListToResponseList(ordersPage.getContent());
        return new OrdersListResponse(list,
                ordersPage.getTotalElements(), ordersPage.getTotalPages(),
                ordersPage.getNumber(), ordersPage.getSize());
    }
}
