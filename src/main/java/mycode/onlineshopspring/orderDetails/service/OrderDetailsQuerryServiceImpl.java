package mycode.onlineshopspring.orderDetails.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.common.pagination.PaginationUtils;
import mycode.onlineshopspring.exceptions.OrderDetailsDoesntExistException;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsListResponse;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import mycode.onlineshopspring.orderDetails.repository.OrderDetailsRepository;
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
public class OrderDetailsQuerryServiceImpl implements OrderDetailsQuerryService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final OnlineShopMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsListResponse findAllOrderDetails(int page, int size) {
        Pageable pageable = PaginationUtils.sanitize(page, size, Sort.by("id"));
        Page<OrderDetails> detailsPage = fetchPage(orderDetailsRepository::findAll, pageable);
        List<OrderDetailsResponse> list = mapper.mapOrderDetailsListToResponseList(detailsPage.getContent());
        return new OrderDetailsListResponse(list,
                detailsPage.getTotalElements(), detailsPage.getTotalPages(),
                detailsPage.getNumber(), detailsPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsResponse findOrderDetailById(UUID id) {
        OrderDetails detail = orderDetailsRepository.findById(id).orElseThrow(OrderDetailsDoesntExistException::new);
        return mapper.mapOrderDetailsToResponse(detail);
    }
}
