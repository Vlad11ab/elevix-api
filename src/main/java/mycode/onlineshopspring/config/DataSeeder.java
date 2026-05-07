package mycode.onlineshopspring.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mycode.onlineshopspring.auth.admin.Admin;
import mycode.onlineshopspring.auth.admin.AdminRepository;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.auth.permission.Permission;
import mycode.onlineshopspring.auth.permission.PermissionRepository;
import mycode.onlineshopspring.auth.permission.Permissions;
import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.auth.user.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@shop.com";
    private static final String ADMIN_PASSWORD = "admin1234";
    private static final String CUSTOMER_EMAIL = "customer@shop.com";
    private static final String CUSTOMER_PASSWORD = "customer1234";

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedPermissions();
        seedAdminUser();
        seedDemoCustomer();
    }

    private void seedPermissions() {
        for (String name : Permissions.all()) {
            permissionRepository.findByName(name).orElseGet(() -> {
                Permission p = new Permission();
                p.setName(name);
                Permission saved = permissionRepository.save(p);
                log.info("Seeded permission: {}", name);
                return saved;
            });
        }
    }

    private void seedAdminUser() {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) return;

        Set<Permission> all = new HashSet<>(permissionRepository.findByNameIn(
                new HashSet<>(Arrays.asList(Permissions.all()))
        ));

        User adminUser = User.builder()
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .enabled(true)
                .permissions(all)
                .build();

        Admin admin = new Admin();
        admin.setUser(adminUser);
        admin.setDisplayName("Site Administrator");
        admin.setDepartment("Operations");
        admin.setNotes("Default seeded admin account.");

        adminRepository.save(admin);
        log.info("Seeded admin account: {} (password: {})", ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    private void seedDemoCustomer() {
        if (userRepository.existsByEmail(CUSTOMER_EMAIL)) return;

        Set<Permission> customerPerms = new HashSet<>(permissionRepository.findByNameIn(
                new HashSet<>(Arrays.asList(Permissions.customerDefaults()))
        ));

        User customerUser = User.builder()
                .email(CUSTOMER_EMAIL)
                .password(passwordEncoder.encode(CUSTOMER_PASSWORD))
                .enabled(true)
                .permissions(customerPerms)
                .build();

        Customer demoCustomer = new Customer();
        demoCustomer.setUser(customerUser);
        demoCustomer.setFullName("Demo Customer");
        demoCustomer.setBillingAddress("Demo Street 1");
        demoCustomer.setDefaultShippingAddress("Demo Street 1");
        demoCustomer.setCountry("RO");
        demoCustomer.setPhone("0712345678");

        customerRepository.save(demoCustomer);
        log.info("Seeded demo customer: {} (password: {})", CUSTOMER_EMAIL, CUSTOMER_PASSWORD);
    }
}
