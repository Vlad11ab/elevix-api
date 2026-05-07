package mycode.onlineshopspring.customer.service;

import mycode.onlineshopspring.customer.dto.CustomerDto;
import mycode.onlineshopspring.customer.dto.CustomerResponse;

import java.util.UUID;

public interface UserCommandService {
    CustomerResponse updateCustomer(UUID id, CustomerDto dto);
    CustomerResponse updateCustomerByEmail(String email, CustomerDto dto);
    void deleteCustomer(UUID id);
}
