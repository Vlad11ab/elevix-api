package mycode.onlineshopspring.repository;

import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import mycode.onlineshopspring.orderDetails.repository.OrderDetailsRepository;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
import mycode.onlineshopspring.products.models.Products;
import mycode.onlineshopspring.products.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RepositoryPaginationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @BeforeEach
    void setUp() {
        // ARRANGE — two products, two customers (each with a User), three orders, three order details
        Products productOne = createProduct("SKU-100", "Laptop", 1200);
        Products productTwo = createProduct("SKU-200", "Phone", 800);
        entityManager.persist(productOne);
        entityManager.persist(productTwo);

        Customer customerOne = createCustomer("Ana Pop", "ana@example.com");
        Customer customerTwo = createCustomer("Dan Ionescu", "dan@example.com");
        entityManager.persist(customerOne);
        entityManager.persist(customerTwo);

        Orders orderOne = createOrder("Addr1", "Ship1", "ana@example.com", customerOne);
        Orders orderTwo = createOrder("Addr2", "Ship2", "dan@example.com", customerTwo);
        Orders orderThree = createOrder("Addr3", "Ship3", "dan@example.com", customerTwo);
        entityManager.persist(orderOne);
        entityManager.persist(orderTwo);
        entityManager.persist(orderThree);

        OrderDetails detailOne = createOrderDetails("SKU-100", 2, orderOne, productOne);
        OrderDetails detailTwo = createOrderDetails("SKU-200", 1, orderTwo, productTwo);
        OrderDetails detailThree = createOrderDetails("SKU-200", 3, orderThree, productTwo);
        entityManager.persist(detailOne);
        entityManager.persist(detailTwo);
        entityManager.persist(detailThree);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void customerRepositoryReturnsPagedResults() {
        // ACT
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id"));
        Page<Customer> page = customerRepository.findAll(pageable);

        // ASSERT
        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertFalse(page.getContent().get(0).getOrderSet().isEmpty(), "Orders should be eagerly loaded");
    }

    @Test
    void ordersRepositoryReturnsPagedResults() {
        // ACT
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        Page<Orders> page = ordersRepository.findAll(pageable);

        // ASSERT
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertFalse(page.getContent().get(0).getOrderDetailsSet().isEmpty());
    }

    @Test
    void orderDetailsRepositoryReturnsPagedResults() {
        // ACT
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        Page<OrderDetails> page = orderDetailsRepository.findAll(pageable);

        // ASSERT
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertNotNull(page.getContent().get(0).getProduct());
    }

    @Test
    void productsRepositoryReturnsPagedResults() {
        // ACT
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id"));
        Page<Products> page = productsRepository.findAll(pageable);

        // ASSERT
        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertTrue(page.getContent().get(0).getStock() > 0);
    }

    private Products createProduct(String sku, String name, int price) {
        Products product = new Products();
        product.setSku(sku);
        product.setName(name);
        product.setPrice(price);
        product.setWeight(10);
        product.setDescriptions("Desc " + name);
        product.setCategory("Category");
        product.setCreateDate(new Date());
        product.setStock(5);
        return product;
    }

    private Customer createCustomer(String fullName, String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded-password");
        user.setEnabled(true);

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFullName(fullName);
        customer.setBillingAddress("Billing");
        customer.setDefaultShippingAddress("Shipping");
        customer.setCountry("Romania");
        customer.setPhone("0700000000");
        return customer;
    }

    private Orders createOrder(String orderAddress, String shippingAddress, String orderEmail, Customer customer) {
        Orders order = new Orders();
        order.setAmmount(300);
        order.setOrderAdress(orderAddress);
        order.setShippingAdress(shippingAddress);
        order.setOrderEmail(orderEmail);
        order.setOrder_date(LocalDate.now());
        order.setOrderStatus("CREATED");
        order.setCustomer(customer);
        return order;
    }

    private OrderDetails createOrderDetails(String sku, int quantity, Orders order, Products product) {
        OrderDetails detail = new OrderDetails();
        detail.setPrice(150);
        detail.setSku(sku);
        detail.setQuantity(quantity);
        detail.setOrder(order);
        detail.setProduct(product);
        return detail;
    }
}
