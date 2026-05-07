package mycode.onlineshopspring.orders.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.exceptions.CustomerDoesntExistException;
import mycode.onlineshopspring.exceptions.OrderDoesntExistException;
import mycode.onlineshopspring.exceptions.ProductDoesntExistException;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsDto;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import mycode.onlineshopspring.orders.dto.OrdersDto;
import mycode.onlineshopspring.orders.dto.OrdersResponse;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
import mycode.onlineshopspring.products.models.Products;
import mycode.onlineshopspring.products.repository.ProductsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdersCommandServiceImpl implements OrdersCommandService {

    private final OrdersRepository repository;
    private final CustomerRepository customerRepository;
    private final ProductsRepository productsRepository;
    private final OnlineShopMapper mapper;

    @Transactional
    @Override
    public OrdersResponse createOrder(UUID customerId, OrdersDto dto) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(CustomerDoesntExistException::new);
        return saveOrderFor(customer, dto);
    }

    @Transactional
    @Override
    public OrdersResponse createOrderForCurrentUser(String email, OrdersDto dto) {
        Customer customer = customerRepository.findByUserEmail(email).orElseThrow(CustomerDoesntExistException::new);
        return saveOrderFor(customer, dto);
    }

    private OrdersResponse saveOrderFor(Customer customer, OrdersDto dto) {
        Orders order = mapper.mapOrdersDtoToOrders(dto);
        order.setCustomer(customer);

        Set<OrderDetailsDto> detailDtos = dto.orderDetailsSet();
        if (detailDtos != null && !detailDtos.isEmpty()) {
            attachOrderDetails(order, detailDtos);
        }

        Orders saved = repository.save(order);
        return mapper.mapOrdersToOrdersResponse(saved);
    }

    @Transactional
    @Override
    public OrdersResponse updateOrderStatus(UUID id, String status) {
        Orders order = repository.findById(id).orElseThrow(OrderDoesntExistException::new);
        order.setOrderStatus(status);
        Orders saved = repository.save(order);
        return mapper.mapOrdersToOrdersResponse(saved);
    }

    @Transactional
    @Override
    public void deleteOrder(UUID id) {
        Orders order = repository.findById(id).orElseThrow(OrderDoesntExistException::new);
        repository.delete(order);
    }

    private void attachOrderDetails(Orders order, Set<OrderDetailsDto> detailDtos) {
        Set<UUID> productIds = detailDtos.stream().map(OrderDetailsDto::productId).collect(Collectors.toSet());
        List<Products> products = productsRepository.findAllById(productIds);
        Map<UUID, Products> productsById = products.stream().collect(Collectors.toMap(Products::getId, p -> p));

        if (productsById.size() != productIds.size()) {
            throw new ProductDoesntExistException();
        }

        Set<OrderDetails> detailEntities = new HashSet<>();
        for (OrderDetailsDto detailDto : detailDtos) {
            OrderDetails detail = mapper.mapOrderDetailsDtoToOrderDetails(detailDto);
            Products product = productsById.get(detailDto.productId());
            if (product == null) throw new ProductDoesntExistException();
            detail.setProduct(product);
            detail.setOrder(order);
            detailEntities.add(detail);
        }
        order.getOrderDetailsSet().addAll(detailEntities);
    }
}
