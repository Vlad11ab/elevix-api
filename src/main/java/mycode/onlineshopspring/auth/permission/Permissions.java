package mycode.onlineshopspring.auth.permission;

public final class Permissions {

    private Permissions() {}

    // Admin-only — managing the user catalog
    public static final String USER_READ = "USER_READ";
    public static final String USER_WRITE = "USER_WRITE";

    // Product catalog — read is public to authenticated users; write is admin-only
    public static final String PRODUCT_READ = "PRODUCT_READ";
    public static final String PRODUCT_WRITE = "PRODUCT_WRITE";

    // Customers' own orders — both perms granted to customers; admin uses USER_READ to see all
    public static final String ORDER_READ = "ORDER_READ";
    public static final String ORDER_WRITE = "ORDER_WRITE";

    // Order details — same scoping as orders
    public static final String ORDER_DETAILS_READ = "ORDER_DETAILS_READ";
    public static final String ORDER_DETAILS_WRITE = "ORDER_DETAILS_WRITE";

    // Manage the permission catalog and user-permission grants — admin-only
    public static final String PERMISSION_READ = "PERMISSION_READ";
    public static final String PERMISSION_WRITE = "PERMISSION_WRITE";

    public static String[] all() {
        return new String[]{
                USER_READ, USER_WRITE,
                PRODUCT_READ, PRODUCT_WRITE,
                ORDER_READ, ORDER_WRITE,
                ORDER_DETAILS_READ, ORDER_DETAILS_WRITE,
                PERMISSION_READ, PERMISSION_WRITE
        };
    }

    public static String[] customerDefaults() {
        return new String[]{
                PRODUCT_READ,
                ORDER_READ, ORDER_WRITE,
                ORDER_DETAILS_READ, ORDER_DETAILS_WRITE
        };
    }
}
