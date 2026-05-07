package mycode.onlineshopspring.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import mycode.onlineshopspring.admin.exceptions.AdminNotFoundException;
import mycode.onlineshopspring.auth.exceptions.EmailAlreadyUsedException;
import mycode.onlineshopspring.auth.exceptions.InvalidRefreshTokenException;
import mycode.onlineshopspring.auth.exceptions.MissingPermissionException;
import mycode.onlineshopspring.auth.permission.exceptions.PermissionAlreadyExistsException;
import mycode.onlineshopspring.auth.permission.exceptions.PermissionNotFoundException;
import mycode.onlineshopspring.auth.user.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerDoesntExistException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFound(CustomerDoesntExistException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, "Customer not found", ex.getMessage(), req);
    }

    @ExceptionHandler(ProductDoesntExistException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductDoesntExistException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, "Product not found", ex.getMessage(), req);
    }

    @ExceptionHandler(OrderDoesntExistException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderDoesntExistException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, "Order not found", ex.getMessage(), req);
    }

    @ExceptionHandler(OrderDetailsDoesntExistException.class)
    public ResponseEntity<Map<String, Object>> handleOrderDetailsNotFound(OrderDetailsDoesntExistException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, "Order details not found", ex.getMessage(), req);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailUsed(EmailAlreadyUsedException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.CONFLICT, "Email already used", ex.getMessage(), req);
    }

    @ExceptionHandler(PermissionAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handlePermissionExists(PermissionAlreadyExistsException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.CONFLICT, "Permission already exists", ex.getMessage(), req);
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePermissionNotFound(PermissionNotFoundException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, "Permission not found", ex.getMessage(), req);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, "User not found", ex.getMessage(), req);
    }

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAdminNotFound(AdminNotFoundException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, "Admin not found", ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefresh(InvalidRefreshTokenException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid refresh token", ex.getMessage(), req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Bad credentials", "Invalid email or password.", req);
    }

    @ExceptionHandler(MissingPermissionException.class)
    public ResponseEntity<Map<String, Object>> handleMissingPermission(MissingPermissionException ex, HttpServletRequest req) {
        Map<String, Object> body = baseBody(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req);
        body.put("requiredPermission", ex.getRequiredPermission());
        body.put("yourPermissions", currentAuthorities());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Catches @PreAuthorize denials inside the dispatcher so they get the same JSON shape
     * as filter-level denials (which the AccessDeniedHandler covers).
     */
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleAccessDenied(RuntimeException ex, HttpServletRequest req) {
        Map<String, Object> body = baseBody(HttpStatus.FORBIDDEN, "Forbidden",
                "You do not have the required permission for this operation. Check your account permissions.", req);
        body.put("yourPermissions", currentAuthorities());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Validation failed",
                "Request contains invalid fields. See details below.", req);

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fieldErrors.put(e.getField(), e.getDefaultMessage()));
        body.put("errors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(baseBody(status, error, message, req));
    }

    private Map<String, Object> baseBody(HttpStatus status, String error, String message, HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", req.getRequestURI());
        return body;
    }

    private List<String> currentAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return List.of();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
