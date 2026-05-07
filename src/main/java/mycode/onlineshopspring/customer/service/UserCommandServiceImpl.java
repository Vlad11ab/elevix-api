package mycode.onlineshopspring.customer.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.customer.dto.CustomerDto;
import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.exceptions.CustomerDoesntExistException;
import mycode.onlineshopspring.mappers.OnlineShopMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final CustomerRepository repository;
    private final OnlineShopMapper mapper;

    @Transactional
    @Override
    public CustomerResponse updateCustomer(UUID id, CustomerDto dto) {
        Customer existing = repository.findById(id).orElseThrow(CustomerDoesntExistException::new);
        return applyAndSave(existing, dto);
    }

    @Transactional
    @Override
    public CustomerResponse updateCustomerByEmail(String email, CustomerDto dto) {
        Customer existing = repository.findByUserEmail(email).orElseThrow(CustomerDoesntExistException::new);
        return applyAndSave(existing, dto);
    }

    @Transactional
    @Override
    public void deleteCustomer(UUID id) {
        Customer existing = repository.findById(id).orElseThrow(CustomerDoesntExistException::new);
        repository.delete(existing);
    }

    private CustomerResponse applyAndSave(Customer existing, CustomerDto dto) {
        existing.setFullName(dto.fullName());
        existing.setPhone(dto.phone());
        existing.setCountry(dto.country());
        existing.setBillingAddress(dto.billingAddress());
        existing.setDefaultShippingAddress(dto.defaultShippingAddress());
        Customer saved = repository.save(existing);
        return mapper.mapCustomerToCustomerResponse(saved);
    }
}
