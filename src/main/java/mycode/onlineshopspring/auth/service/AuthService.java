package mycode.onlineshopspring.auth.service;

import lombok.RequiredArgsConstructor;
import mycode.onlineshopspring.auth.dto.AuthResponse;
import mycode.onlineshopspring.auth.dto.LoginRequest;
import mycode.onlineshopspring.auth.dto.RefreshRequest;
import mycode.onlineshopspring.auth.dto.RegisterRequest;
import mycode.onlineshopspring.auth.exceptions.EmailAlreadyUsedException;
import mycode.onlineshopspring.auth.exceptions.InvalidRefreshTokenException;
import mycode.onlineshopspring.auth.jwt.JwtService;
import mycode.onlineshopspring.auth.permission.Permission;
import mycode.onlineshopspring.auth.permission.PermissionRepository;
import mycode.onlineshopspring.auth.permission.Permissions;
import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.auth.user.UserRepository;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException();
        }

        Set<Permission> defaultPermissions = new HashSet<>(
                permissionRepository.findByNameIn(Set.of(Permissions.customerDefaults()))
        );

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .permissions(defaultPermissions)
                .build();

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFullName(request.fullName());
        customer.setBillingAddress(request.billingAddress());
        customer.setDefaultShippingAddress(request.defaultShippingAddress());
        customer.setCountry(request.country());
        customer.setPhone(request.phone());

        customerRepository.save(customer);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isRefreshToken(token)) {
            throw new InvalidRefreshTokenException();
        }
        String email = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new InvalidRefreshTokenException();
        }
        return buildAuthResponse((User) userDetails);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        List<String> permissions = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return new AuthResponse(accessToken, refreshToken, user.getEmail(), permissions);
    }
}
