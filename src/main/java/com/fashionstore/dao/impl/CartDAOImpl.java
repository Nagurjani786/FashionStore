
package com.fashionstore.dao.impl;

import com.fashionstore.dao.CartDAO;
import com.fashionstore.model.Cart;
import com.fashionstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class CartDAOImpl implements CartDAO {

    private static final String CREATE_CART_SQL =
        "INSERT INTO cart (user_id) VALUES (?)";

    private static final String GET_CART_BY_ID_SQL =
        "SELECT * FROM cart WHERE cart_id = ?";

    private static final String GET_CART_BY_USER_ID_SQL =
        "SELECT * FROM cart WHERE user_id = ?";

    private static final String DELETE_CART_SQL =
        "DELETE FROM cart WHERE cart_id = ?";

    private static final String CHECK_CART_BY_USER_ID_SQL =
        "SELECT 1 FROM cart WHERE user_id = ?";

    @Override
    public boolean createCart(int userId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     CREATE_CART_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    return rs.next();
                }
            }
        }

        return false;
    }

    @Override
    public Cart getCartById(int cartId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_CART_BY_ID_SQL)) {

            ps.setInt(1, cartId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCart(rs);
                }
            }
        }

        return null;
    }

    @Override
    public Cart getCartByUserId(int userId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_CART_BY_USER_ID_SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCart(rs);
                }
            }
        }

        return null;
    }

    @Override
    public Cart getOrCreateCartByUserId(int userId) throws SQLException {
        Cart cart = getCartByUserId(userId);

        if (cart != null) {
            return cart;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     CREATE_CART_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return getCartById(rs.getInt(1));
                    }
                }
            }
        }

        return null;
    }

    @Override
    public boolean deleteCart(int cartId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(DELETE_CART_SQL)) {

            ps.setInt(1, cartId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cartExistsByUserId(int userId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(CHECK_CART_BY_USER_ID_SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Cart mapCart(ResultSet rs) throws SQLException {
        Cart cart = new Cart();

        cart.setCartId(rs.getInt("cart_id"));
        cart.setUserId(rs.getInt("user_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        cart.setCreatedAt(createdAt);
        cart.setUpdatedAt(updatedAt);

        return cart;
    }
}
