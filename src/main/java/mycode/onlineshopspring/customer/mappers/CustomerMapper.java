package mycode.onlineshopspring.customer.mappers;

import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.customer.dto.CustomerDto;
import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.orders.dto.OrdersResponse;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CustomerMapper {


    private static String nvl(String s) { return s == null ? "" : s; }
    private static String trim(String s) { return s == null ? null : s.trim(); }


}
