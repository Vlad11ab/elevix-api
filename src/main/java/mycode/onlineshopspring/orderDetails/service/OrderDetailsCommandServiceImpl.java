package mycode.onlineshopspring.orderDetails.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.exceptions.OrderDetailsDoesntExistException;
import mycode.onlineshopspring.exceptions.ProductDoesntExistException;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsDto;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import mycode.onlineshopspring.orderDetails.repository.OrderDetailsRepository;
import mycode.onlineshopspring.products.models.Products;
import mycode.onlineshopspring.products.repository.ProductsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderDetailsCommandServiceImpl implements OrderDetailsCommandService {

    private final OrderDetailsRepository repository;
    private final ProductsRepository productsRepository;
    private final OnlineShopMapper mapper;

    @Transactional
    @Override
    public OrderDetailsResponse createOrderDetails(OrderDetailsDto dto) {
        Products product = productsRepository.findById(dto.productId()).orElseThrow(ProductDoesntExistException::new);
        OrderDetails e = mapper.mapOrderDetailsDtoToOrderDetails(dto);
        e.setProduct(product);
        OrderDetails saved = repository.save(e);
        return mapper.mapOrderDetailsToResponse(saved);
    }

    @Transactional
    @Override
    public void deleteOrderDetail(UUID id) {
        OrderDetails detail = repository.findById(id).orElseThrow(OrderDetailsDoesntExistException::new);
        repository.delete(detail);
    }
}
