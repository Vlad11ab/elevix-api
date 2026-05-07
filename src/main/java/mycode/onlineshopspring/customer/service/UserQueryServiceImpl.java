package mycode.onlineshopspring.customer.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.common.pagination.PaginationUtils;
import mycode.onlineshopspring.customer.dto.CustomerListResponse;
import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.exceptions.CustomerDoesntExistException;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
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
public class UserQueryServiceImpl implements UserQuerryService {

    private final CustomerRepository customerRepository;
    private final OnlineShopMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CustomerListResponse findAllCustomers(int page, int size) {
        Pageable pageable = PaginationUtils.sanitize(page, size, Sort.by("id"));
        Page<Customer> customerPage = fetchPage(customerRepository::findAll, pageable);
        List<CustomerResponse> customers = mapper.mapCustomerListToResponseList(customerPage.getContent());
        return new CustomerListResponse(customers,
                customerPage.getTotalElements(), customerPage.getTotalPages(),
                customerPage.getNumber(), customerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id).orElseThrow(CustomerDoesntExistException::new);
        return mapper.mapCustomerToCustomerResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findCustomerByEmail(String email) {
        Customer customer = customerRepository.findByUserEmail(email).orElseThrow(CustomerDoesntExistException::new);
        return mapper.mapCustomerToCustomerResponse(customer);
    }
}
