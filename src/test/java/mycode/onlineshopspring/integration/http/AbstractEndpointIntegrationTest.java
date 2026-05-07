package mycode.onlineshopspring.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import mycode.onlineshopspring.auth.admin.Admin;
import mycode.onlineshopspring.auth.admin.AdminRepository;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.auth.jwt.JwtService;
import mycode.onlineshopspring.auth.permission.Permission;
import mycode.onlineshopspring.auth.permission.PermissionRepository;
import mycode.onlineshopspring.auth.permission.Permissions;
import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.auth.user.UserRepository;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import mycode.onlineshopspring.orderDetails.repository.OrderDetailsRepository;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.orders.repository.OrdersRepository;
import mycode.onlineshopspring.products.models.Products;
import mycode.onlineshopspring.products.repository.ProductsRepository;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class AbstractEndpointIntegrationTest {

    protected static final String DEFAULT_PASSWORD = "password123";

    private final AtomicInteger sequence = new AtomicInteger();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PermissionRepository permissionRepository;

    @Autowired
    protected AdminRepository adminRepository;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected ProductsRepository productsRepository;

    @Autowired
    protected OrdersRepository ordersRepository;

    @Autowired
    protected OrderDetailsRepository orderDetailsRepository;

    protected String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    protected String bearerTokenFor(User user) {
        return "Bearer " + jwtService.generateAccessToken(user);
    }

    protected Admin persistAdmin() {
        String marker = nextMarker("admin");
        User user = User.builder()
                .email("admin-" + marker + "@shop.test")
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .enabled(true)
                .permissions(loadPermissions(Permissions.all()))
                .build();
        Admin admin = new Admin();
        admin.setUser(user);
        admin.setDisplayName("Admin " + marker);
        admin.setDepartment("Test Ops");
        admin.setNotes("Test admin " + marker);
        return adminRepository.saveAndFlush(admin);
    }

    protected Customer persistCustomer() {
        String marker = nextMarker("customer");
        User user = User.builder()
                .email("customer-" + marker + "@shop.test")
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .enabled(true)
                .permissions(loadPermissions(Permissions.customerDefaults()))
                .build();
        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFullName("Customer " + marker);
        customer.setBillingAddress("Billing Street " + marker);
        customer.setDefaultShippingAddress("Shipping Street " + marker);
        customer.setCountry("RO");
        customer.setPhone("0712345678");
        return customerRepository.saveAndFlush(customer);
    }

    protected Products persistProduct() {
        String marker = nextMarker("product");
        Products product = new Products();
        product.setSku("SKU-" + marker);
        product.setName("Product " + marker);
        product.setPrice(100);
        product.setWeight(1);
        product.setDescriptions("Test product " + marker);
        product.setCategory("test");
        product.setCreateDate(new Date());
        product.setStock(10);
        return productsRepository.saveAndFlush(product);
    }

    protected Orders persistOrder(Customer customer) {
        String marker = nextMarker("order");
        Orders order = new Orders();
        order.setAmmount(100);
        order.setShippingAdress("Shipping " + marker);
        order.setOrderAdress("Address " + marker);
        order.setOrderEmail(customer.getUser().getEmail());
        order.setOrder_date(LocalDate.now());
        order.setOrderStatus("PENDING");
        order.setCustomer(customer);
        return ordersRepository.saveAndFlush(order);
    }

    protected OrderDetails persistOrderDetail(Orders order, Products product) {
        OrderDetails detail = new OrderDetails();
        detail.setPrice(product.getPrice());
        detail.setSku(product.getSku());
        detail.setQuantity(1);
        detail.setOrder(order);
        detail.setProduct(product);
        return orderDetailsRepository.saveAndFlush(detail);
    }

    private Set<Permission> loadPermissions(String[] names) {
        Set<Permission> result = new HashSet<>();
        for (String name : names) {
            Permission p = permissionRepository.findByName(name).orElseGet(() -> {
                Permission np = new Permission();
                np.setName(name);
                return permissionRepository.save(np);
            });
            result.add(p);
        }
        return result;
    }

    private String nextMarker(String prefix) {
        return prefix + "-" + sequence.incrementAndGet();
    }
}
