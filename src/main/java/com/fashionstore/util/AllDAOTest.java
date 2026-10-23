
package com.fashionstore.util;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.fashionstore.dao.*;
import com.fashionstore.dao.impl.*;
import com.fashionstore.model.*;

public class AllDAOTest {

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("       ALL DAO TESTING STARTED");
        System.out.println("==================================");

        testUserDAO();
        testCategoryDAO();
        testProductDAO();
        testProductVariantDAO();
        testCartDAO();
        testCartItemDAO();
        testOrderDAO();
        testOrderItemDAO();

        System.out.println("\n==================================");
        System.out.println("       ALL DAO TESTING FINISHED");
        System.out.println("==================================");
    }

    // 1. USER DAO
    static void testUserDAO() {
        System.out.println("\n===== USER DAO =====");

        UserDAO dao = new UserDAOImpl();

        try {
            User user = dao.getUserById(1);

            System.out.println("User by ID: "
                    + (user != null ? user.getFullName() : "Not found"));

            System.out.println("Email exists: "
                    + dao.emailExists("rahul.test@example.com"));

            System.out.println("Phone exists: "
                    + dao.phoneExists("9000000001"));

            List<User> users = dao.getAllUsers();
            System.out.println("Total users: " + users.size());

        } catch (SQLException e) {
            reportError("UserDAO", e);
        }
    }

    // 2. CATEGORY DAO
    static void testCategoryDAO() {
        System.out.println("\n===== CATEGORY DAO =====");

        CategoryDAO dao = new CategoryDAOImpl();

        try {
            List<Category> categories = dao.getAllCategories();

            System.out.println("Total categories: " + categories.size());

            for (Category category : categories) {
                System.out.println(category);
            }

            System.out.println("Category by ID: "
                    + dao.getCategoryById(1));

            System.out.println("Category by name: "
                    + dao.getCategoryByName("Men Clothing"));

        } catch (SQLException e) {
            reportError("CategoryDAO", e);
        }
    }

    // 3. PRODUCT DAO
    static void testProductDAO() {
        System.out.println("\n===== PRODUCT DAO =====");

        ProductDAO dao = new ProductDAOImpl();

        try {
            List<Product> products = dao.getAllProducts();

            System.out.println("Total products: " + products.size());
            System.out.println("Product by ID: " + dao.getProductById(1));

            System.out.println("Products in category 1: "
                    + dao.getProductsByCategory(1).size());

            System.out.println("Hoodie search results: "
                    + dao.searchProducts("Hoodie").size());

            System.out.println("Latest products: "
                    + dao.getLatestProducts(5).size());

        } catch (SQLException e) {
            reportError("ProductDAO", e);
        }
    }

    // 4. PRODUCT VARIANT DAO
    static void testProductVariantDAO() {
        System.out.println("\n===== PRODUCT VARIANT DAO =====");

        ProductVariantDAO dao = new ProductVariantDAOImpl();

        try {
            System.out.println("Variant by ID: "
                    + dao.getVariantById(5));

            List<ProductVariant> variants =
                    dao.getVariantsByProductId(1);

            System.out.println("Variants for product 1: "
                    + variants.size());

            for (ProductVariant variant : variants) {
                System.out.println(variant);
            }

            System.out.println("Stock available for variant 5 (qty 2): "
                    + dao.isStockAvailable(5, 2));

            System.out.println("All variants: "
                    + dao.getAllVariants().size());

        } catch (SQLException e) {
            reportError("ProductVariantDAO", e);
        }
    }

    // 5. CART DAO
    static void testCartDAO() {
        System.out.println("\n===== CART DAO =====");

        CartDAO dao = new CartDAOImpl();

        try {
            Cart cart = dao.getCartByUserId(1);

            if (cart != null) {
                System.out.println("Cart ID: " + cart.getCartId());
                System.out.println("User ID: " + cart.getUserId());

                System.out.println("Cart by ID: "
                        + dao.getCartById(cart.getCartId()));
            } else {
                System.out.println("No cart found for user 1.");
            }

            System.out.println("Cart exists: "
                    + dao.cartExistsByUserId(1));

            System.out.println("Get or create cart: "
                    + dao.getOrCreateCartByUserId(1));

        } catch (SQLException e) {
            reportError("CartDAO", e);
        }
    }

    // 6. CART ITEM DAO
    static void testCartItemDAO() {
        System.out.println("\n===== CART ITEM DAO =====");

        CartItemDAO dao = new CartItemDAOImpl();

        // Cart ID 6 was found in your earlier database query.
        int cartId = 6;

        try {
            List<CartItem> items = dao.getCartItemsByCartId(cartId);

            System.out.println("Cart item records: " + items.size());

            for (CartItem item : items) {
                System.out.println(
                        "Item ID: " + item.getCartItemId()
                        + " | Variant ID: " + item.getVariantId()
                        + " | Quantity: " + item.getQuantity());
            }

            System.out.println("Total quantity: "
                    + dao.getCartItemCount(cartId));

            BigDecimal total = dao.getCartTotal(cartId);
            System.out.println("Cart total: Rs. " + total);

            if (!items.isEmpty()) {
                int variantId = items.get(0).getVariantId();

                System.out.println("Item by cart and variant: "
                        + dao.getCartItemByCartIdAndVariantId(
                                cartId, variantId));
            }

        } catch (SQLException e) {
            reportError("CartItemDAO", e);
        }
    }

    // 7. ORDER DAO
    static void testOrderDAO() {
        System.out.println("\n===== ORDER DAO =====");

        OrderDAO dao = new OrderDAOImpl();

        try {
            List<Order> orders = dao.getAllOrders();

            System.out.println("Total orders: " + orders.size());

            for (Order order : orders) {
                System.out.println(
                        "Order ID: " + order.getOrderId()
                        + " | User ID: " + order.getUserId()
                        + " | Total: Rs. " + order.getTotalAmount()
                        + " | Status: " + order.getOrderStatus());
            }

            if (!orders.isEmpty()) {
                Order firstOrder = orders.get(0);

                Order found = dao.getOrderById(firstOrder.getOrderId());

                System.out.println("Order by ID: "
                        + (found != null
                        ? found.getOrderId()
                        : "Not found"));

                System.out.println("Orders for user "
                        + firstOrder.getUserId() + ": "
                        + dao.getOrdersByUserId(firstOrder.getUserId()).size());
            }

        } catch (SQLException e) {
            reportError("OrderDAO", e);
        }
    }

    // 8. ORDER ITEM DAO
    static void testOrderItemDAO() {
        System.out.println("\n===== ORDER ITEM DAO =====");

        OrderItemDAO dao = new OrderItemDAOImpl();
        OrderDAO orderDAO = new OrderDAOImpl();

        try {
            List<Order> orders = orderDAO.getAllOrders();

            if (orders.isEmpty()) {
                System.out.println("No orders found; test skipped.");
                return;
            }

            int orderId = orders.get(0).getOrderId();

            List<OrderItem> items = dao.getOrderItemsByOrderId(orderId);

            System.out.println("Order ID: " + orderId);
            System.out.println("Order item count: " + items.size());

            for (OrderItem item : items) {
                System.out.println(
                        "Item ID: " + item.getOrderItemId()
                        + " | Variant ID: " + item.getVariantId()
                        + " | Product: " + item.getProductName()
                        + " | Size: " + item.getSize()
                        + " | Quantity: " + item.getQuantity()
                        + " | Subtotal: Rs. " + item.getSubtotal());
            }

            if (!items.isEmpty()) {
                int itemId = items.get(0).getOrderItemId();

                OrderItem found = dao.getOrderItemById(itemId);

                System.out.println("Order item by ID: "
                        + (found != null
                        ? found.getOrderItemId()
                        : "Not found"));
            }

        } catch (SQLException e) {
            reportError("OrderItemDAO", e);
        }
    }

    // COMMON ERROR HANDLER
    static void reportError(String daoName, SQLException e) {
        System.err.println(daoName + " test failed!");
        e.printStackTrace();
    }
}
