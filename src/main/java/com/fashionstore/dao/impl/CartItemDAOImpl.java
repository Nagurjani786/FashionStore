
package com.fashionstore.dao.impl;

import com.fashionstore.dao.CartItemDAO;
import com.fashionstore.model.CartItem;
import com.fashionstore.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAOImpl implements CartItemDAO {

    private static final String INSERT_CART_ITEM_SQL =
        "INSERT INTO cart_items (cart_id, variant_id, quantity) "
        + "VALUES (?, ?, ?)";

    private static final String UPDATE_QUANTITY_SQL =
        "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";

    private static final String UPDATE_QUANTITY_BY_CART_VARIANT_SQL =
        "UPDATE cart_items SET quantity = ? "
        + "WHERE cart_id = ? AND variant_id = ?";

    private static final String DELETE_CART_ITEM_SQL =
        "DELETE FROM cart_items WHERE cart_item_id = ?";

    private static final String DELETE_BY_CART_VARIANT_SQL =
        "DELETE FROM cart_items WHERE cart_id = ? AND variant_id = ?";

    private static final String GET_CART_ITEM_BY_ID_SQL =
        "SELECT * FROM cart_items WHERE cart_item_id = ?";

    private static final String GET_CART_ITEM_BY_CART_VARIANT_SQL =
        "SELECT * FROM cart_items WHERE cart_id = ? AND variant_id = ?";

    private static final String GET_CART_ITEMS_SQL =
        "SELECT * FROM cart_items WHERE cart_id = ? "
        + "ORDER BY cart_item_id";

    private static final String CLEAR_CART_SQL =
        "DELETE FROM cart_items WHERE cart_id = ?";

    private static final String COUNT_CART_ITEMS_SQL =
        "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE cart_id = ?";

    private static final String GET_CART_TOTAL_SQL =
        "SELECT COALESCE(SUM(ci.quantity * p.price), 0) "
        + "FROM cart_items ci "
        + "JOIN product_variants pv ON ci.variant_id = pv.variant_id "
        + "JOIN products p ON pv.product_id = p.product_id "
        + "WHERE ci.cart_id = ?";

    @Override
    public boolean addCartItem(CartItem cartItem) throws SQLException {
        if (cartItem.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     INSERT_CART_ITEM_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cartItem.getCartId());
            ps.setInt(2, cartItem.getVariantId());
            ps.setInt(3, cartItem.getQuantity());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        cartItem.setCartItemId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateCartItemQuantity(
            int cartItemId, int quantity) throws SQLException {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(UPDATE_QUANTITY_SQL)) {

            ps.setInt(1, quantity);
            ps.setInt(2, cartItemId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateCartItemQuantityByCartAndVariant(
            int cartId, int variantId, int quantity) throws SQLException {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(UPDATE_QUANTITY_BY_CART_VARIANT_SQL)) {

            ps.setInt(1, quantity);
            ps.setInt(2, cartId);
            ps.setInt(3, variantId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean removeCartItem(int cartItemId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(DELETE_CART_ITEM_SQL)) {

            ps.setInt(1, cartItemId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean removeCartItemByCartAndVariant(
            int cartId, int variantId) throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(DELETE_BY_CART_VARIANT_SQL)) {

            ps.setInt(1, cartId);
            ps.setInt(2, variantId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public CartItem getCartItemById(int cartItemId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_CART_ITEM_BY_ID_SQL)) {

            ps.setInt(1, cartItemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCartItem(rs);
                }
            }
        }

        return null;
    }

    @Override
    public CartItem getCartItemByCartIdAndVariantId(
            int cartId, int variantId) throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_CART_ITEM_BY_CART_VARIANT_SQL)) {

            ps.setInt(1, cartId);
            ps.setInt(2, variantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCartItem(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<CartItem> getCartItemsByCartId(int cartId)
            throws SQLException {

        List<CartItem> items = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_CART_ITEMS_SQL)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapCartItem(rs));
                }
            }
        }

        return items;
    }

    @Override
    public boolean clearCart(int cartId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(CLEAR_CART_SQL)) {

            ps.setInt(1, cartId);

            // An empty cart is already clear.
            ps.executeUpdate();
            return true;
        }
    }

    @Override
    public int getCartItemCount(int cartId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(COUNT_CART_ITEMS_SQL)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    @Override
    public BigDecimal getCartTotal(int cartId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_CART_TOTAL_SQL)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private CartItem mapCartItem(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();

        item.setCartItemId(rs.getInt("cart_item_id"));
        item.setCartId(rs.getInt("cart_id"));
        item.setVariantId(rs.getInt("variant_id"));
        item.setQuantity(rs.getInt("quantity"));

        return item;
    }
}
