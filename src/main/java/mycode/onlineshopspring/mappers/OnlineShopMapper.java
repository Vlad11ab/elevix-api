package mycode.onlineshopspring.mappers;

import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.customer.dto.CustomerDto;
import mycode.onlineshopspring.customer.dto.CustomerResponse;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsDto;
import mycode.onlineshopspring.orderDetails.dto.OrderDetailsResponse;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import mycode.onlineshopspring.orders.dto.OrdersDto;
import mycode.onlineshopspring.orders.dto.OrdersResponse;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.products.dto.ProductsDto;
import mycode.onlineshopspring.products.dto.ProductsResponse;
import mycode.onlineshopspring.products.models.Products;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OnlineShopMapper {

    private String nvl(String s) { return s == null ? "" : s; }
    private static String trim(String s) { return s == null ? null : s.trim(); }

    // --- PRODUCTS MAPPERS ---
    public ProductsResponse mapProductsToProductsResponse(Products e) {
        Objects.requireNonNull(e, "Products entity is null");
        return new ProductsResponse(e.getId(), nvl(e.getSku()), nvl(e.getName()), e.getPrice(), e.getWeight(), nvl(e.getDescriptions()), nvl(e.getCategory()), e.getCreateDate(), e.getStock());
    }
    public List<ProductsResponse> mapProductsListToResponseList(List<Products> list) {
        if (list == null) return List.of();
        return list.stream().map(this::mapProductsToProductsResponse).toList();
    }
    public Products mapProductsDtoToProducts(ProductsDto dto){
        Objects.requireNonNull(dto,"Products DTO is null");
        Products e=new Products();
        e.setSku(trim(dto.sku()));
        e.setName(trim(dto.name()));
        e.setPrice(dto.price());
        e.setWeight(dto.weight());
        e.setDescriptions(trim(dto.descriptions()));
        e.setCategory(trim(dto.category()));
        e.setCreateDate(dto.createDate());
        e.setStock(dto.stock());
        return e;
    }


    // --- ORDER DETAILS MAPPERS ---
    public OrderDetailsResponse mapOrderDetailsToResponse(OrderDetails e) {
        Objects.requireNonNull(e, "OrderDetails entity is null");
        ProductsResponse productResponse = e.getProduct() != null ? mapProductsToProductsResponse(e.getProduct()) : null;
        return new OrderDetailsResponse(e.getId(), e.getPrice(), nvl(e.getSku()), e.getQuantity(), productResponse);
    }
    public Set<OrderDetailsResponse> mapOrderDetailsSetToResponseSet(Set<OrderDetails> set) {
        if (set == null) return Set.of();
        return set.stream().map(this::mapOrderDetailsToResponse).collect(Collectors.toSet());
    }
    public List<OrderDetailsResponse> mapOrderDetailsListToResponseList(List<OrderDetails> list){
        if(list==null) return List.of();
        return list.stream().map(this::mapOrderDetailsToResponse).toList();
    }
    public OrderDetails mapOrderDetailsDtoToOrderDetails(OrderDetailsDto dto){
        Objects.requireNonNull(dto,"OrderDetails DTO is null");
        OrderDetails e=new OrderDetails();
        e.setPrice(dto.price());
        e.setSku(trim(dto.sku()));
        e.setQuantity(dto.quantity());
        return e;
    }
    public Set<OrderDetails> mapOrderDetailsDtoSetToEntitySet(Set<OrderDetailsDto> dtoSet){
        if(dtoSet==null) return new java.util.HashSet<>();
        return dtoSet.stream().map(this::mapOrderDetailsDtoToOrderDetails).collect(Collectors.toSet());
    }

    // --- ORDERS MAPPERS ---
    public OrdersResponse mapOrdersToOrdersResponse(Orders e) {
        Objects.requireNonNull(e, "Orders entity is null");
        Set<OrderDetailsResponse> detailsSet = mapOrderDetailsSetToResponseSet(e.getOrderDetailsSet());
        return new OrdersResponse(e.getId(), e.getAmmount(), nvl(e.getShippingAdress()), nvl(e.getOrderAdress()), nvl(e.getOrderEmail()), e.getOrder_date(), nvl(e.getOrderStatus()), detailsSet);
    }
    public Set<OrdersResponse> mapOrdersSetToResponseSet(Set<Orders> set) {
        if (set == null) return Set.of();
        return set.stream().map(this::mapOrdersToOrdersResponse).collect(Collectors.toSet());
    }
    public List<OrdersResponse> mapOrdersListToResponseList(List<Orders> list){
        if(list==null) return List.of();
        return list.stream().map(this::mapOrdersToOrdersResponse).toList();
    }
    public Orders mapOrdersDtoToOrders(OrdersDto dto){
        Objects.requireNonNull(dto,"Orders DTO is null");
        Orders e=new Orders();
        e.setAmmount(dto.ammount());
        e.setShippingAdress(trim(dto.shippingAdress()));
        e.setOrderAdress(trim(dto.orderAddress()));
        e.setOrderEmail(trim(dto.orderEmail()));
        e.setOrder_date(dto.order_date());
        e.setOrderStatus(trim(dto.orderStatus()));
        return e;
    }
    public Set<Orders> mapOrdersDtoSetToEntitySet(Set<OrdersDto> dtoSet){
        if(dtoSet==null) return new java.util.HashSet<>();
        return dtoSet.stream().map(this::mapOrdersDtoToOrders).collect(Collectors.toSet());
    }

    // --- CUSTOMER MAPPERS ---
    public CustomerResponse mapCustomerToCustomerResponse(Customer e) {
        Objects.requireNonNull(e, "Customer entity is null");
        Set<OrdersResponse> orderSet = mapOrdersSetToResponseSet(e.getOrderSet());
        String email = e.getUser() != null ? nvl(e.getUser().getEmail()) : "";
        return new CustomerResponse(e.getId(), email, nvl(e.getFullName()), nvl(e.getBillingAddress()), nvl(e.getDefaultShippingAddress()), nvl(e.getCountry()), nvl(e.getPhone()), orderSet);
    }
    public List<CustomerResponse> mapCustomerListToResponseList(List<Customer> list) {
        if (list == null) return List.of();
        return list.stream().map(this::mapCustomerToCustomerResponse).toList();
    }
    public Customer mapCustomerDtoToCustomer(CustomerDto dto){
        Objects.requireNonNull(dto,"Customer DTO is null");
        Customer e=new Customer();
        e.setFullName(trim(dto.fullName()));
        e.setBillingAddress(trim(dto.billingAddress()));
        e.setDefaultShippingAddress(trim(dto.defaultShippingAddress()));
        e.setCountry(trim(dto.country()));
        e.setPhone(trim(dto.phone()));
        return e;
    }
}
