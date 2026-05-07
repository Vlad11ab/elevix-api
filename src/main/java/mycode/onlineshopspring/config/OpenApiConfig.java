package mycode.onlineshopspring.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Value("${server.port:8082}")
    private int serverPort;

    @Bean
    public OpenAPI onlineShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Shop API")
                        .description("""
                                RESTful API for the Online Shop application — JWT-secured, permission-based authorization.

                                ## Quick start
                                1. **Register** a customer account at `POST /api/auth/register` (or use the seeded accounts below).
                                2. **Login** at `POST /api/auth/login` to receive `accessToken` + `refreshToken`.
                                3. Click the **Authorize** button (top right) and paste the access token to call any secured endpoint.
                                4. When the access token expires (15 min), call `POST /api/auth/refresh` with the refresh token.

                                ## Seeded accounts (dev)
                                | Account  | Email              | Password     | Profile  | Permissions          |
                                |----------|--------------------|--------------|----------|----------------------|
                                | Admin    | admin@shop.com     | admin1234    | Admin    | All 10 permissions   |
                                | Customer | customer@shop.com  | customer1234 | Customer | Customer defaults    |

                                ## Domain model
                                The `auth` package owns three entities:
                                - **`User`** — the authentication identity (email, password, set of permissions).
                                - **`Customer`** — shopping profile (`@OneToOne User`); has orders.
                                - **`Admin`** — staff profile (`@OneToOne User`); used for backoffice users.

                                ## Permissions
                                Authorization is **permission-based**, not role-based. Each user owns a `Set<Permission>` directly.

                                | Permission              | Holders            | What it grants                                              |
                                |-------------------------|--------------------|-------------------------------------------------------------|
                                | `USER_READ`             | Admin              | List all customers/orders/admins/users                      |
                                | `USER_WRITE`            | Admin              | Create/update/delete customers, admins; change order status |
                                | `PRODUCT_READ`          | Admin + Customer   | Read product catalog                                        |
                                | `PRODUCT_WRITE`         | Admin              | Create/update/delete products                               |
                                | `ORDER_READ`            | Admin + Customer   | Read own orders (`/me`)                                     |
                                | `ORDER_WRITE`           | Admin + Customer   | Create own orders (`/me`)                                   |
                                | `ORDER_DETAILS_READ`    | Admin + Customer   | Read own order line items                                   |
                                | `ORDER_DETAILS_WRITE`   | Admin + Customer   | Modify own order line items                                 |
                                | `PERMISSION_READ`       | Admin              | List the permission catalog and per-user permissions        |
                                | `PERMISSION_WRITE`      | Admin              | Create/delete permissions; grant/revoke to users            |

                                ## Endpoint shape
                                - `/api/auth/**` — public (register, login, refresh).
                                - `/api/customers/me`, `/api/orders/me`, `/api/admins/me` — any authenticated user, scoped to self.
                                - `/api/customers`, `/api/orders` (list), `/api/admins`, `/api/users` — admin only.
                                - `/api/customers/{id}`, `/api/orders/{id}` — admin OR resource owner.
                                - `/api/products` — read = any authenticated user; write = admin.
                                - `/api/permissions` — admin-only catalog (PERMISSION_READ / PERMISSION_WRITE).
                                - `/api/users/{id}/permissions` — admin grants/revokes permissions to a user.

                                ## Error responses
                                All errors return JSON with `timestamp`, `status`, `error`, `message`, `path`. Authorization
                                errors additionally include `yourPermissions` (the caller's current permissions) so it's clear
                                exactly what is missing.

                                | Status | When                                                |
                                |--------|-----------------------------------------------------|
                                | 400    | Validation failed (see `errors` field)              |
                                | 401    | Missing/invalid/expired token                       |
                                | 403    | Authenticated but missing the required permission   |
                                | 404    | Resource not found                                  |
                                | 409    | Conflict (duplicate email or permission name)       |

                                ## Pagination
                                List endpoints accept `page` (0-based) and `size` query params.
                                """)
                        .version("1.1.0")
                        .contact(new Contact()
                                .name("Online Shop Team")
                                .email("admin@onlineshop.ro")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local")))
                .tags(List.of(
                        new Tag().name("Auth").description("Public — register, login, refresh"),
                        new Tag().name("Customers").description("Customer profiles (self via /me, admin for others)"),
                        new Tag().name("Admins").description("Admin staff profiles (admin-only)"),
                        new Tag().name("Users").description("Admin — list users, manage their permissions"),
                        new Tag().name("Permissions").description("Admin — catalog of permissions"),
                        new Tag().name("Products").description("Product catalog"),
                        new Tag().name("Orders").description("Orders (self via /me, admin for full access)"),
                        new Tag().name("Order Details").description("Line items (admin)")
                ))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste only the JWT access token here (without the 'Bearer ' prefix). Get one from POST /api/auth/login.")));
    }
}
