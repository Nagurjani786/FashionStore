
package com.fashionstore.dao.impl;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.model.Order;
import com.fashionstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public boolean placeOrder(Order order) throws SQLException {

        String sql = "INSERT INTO orders "
                + "(user_id, total_amount, delivery_name, delivery_phone, "
                + "delivery_address, delivery_city, delivery_state, "
                + "delivery_postal_code, payment_method, order_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getUserId());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, order.getDeliveryName());
            ps.setString(4, order.getDeliveryPhone());
            ps.setString(5, order.getDeliveryAddress());
            ps.setString(6, order.getDeliveryCity());
            ps.setString(7, order.getDeliveryState());
            ps.setString(8, order.getDeliveryPostalCode());

            String paymentMethod = order.getPaymentMethod();
            ps.setString(9,
                    paymentMethod == null || paymentMethod.isBlank()
                            ? "COD" : paymentMethod);

            String orderStatus = order.getOrderStatus();
            ps.setString(10,
                    orderStatus == null || orderStatus.isBlank()
                            ? "PLACED" : orderStatus);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setOrderId(keys.getInt(1));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Order getOrderById(int orderId) throws SQLException {

        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId)
            throws SQLException {

        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM orders "
                + "WHERE user_id = ? ORDER BY order_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }

        return orders;
    }

    @Override
    public List<Order> getAllOrders() throws SQLException {

        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM orders ORDER BY order_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        }

        return orders;
    }

    @Override
    public boolean updateOrderStatus(int orderId, String orderStatus)
            throws SQLException {

        String sql = "UPDATE orders SET order_status = ? "
                + "WHERE order_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, orderStatus);
            ps.setInt(2, orderId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cancelOrder(int orderId) throws SQLException {

        String sql = "UPDATE orders "
                + "SET order_status = 'CANCELLED' "
                + "WHERE order_id = ? "
                + "AND order_status NOT IN ('CANCELLED', 'DELIVERED')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            return ps.executeUpdate() > 0;
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {

        Order order = new Order();

        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setDeliveryName(rs.getString("delivery_name"));
        order.setDeliveryPhone(rs.getString("delivery_phone"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setDeliveryCity(rs.getString("delivery_city"));
        order.setDeliveryState(rs.getString("delivery_state"));
        order.setDeliveryPostalCode(
                rs.getString("delivery_postal_code"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setOrderedAt(rs.getTimestamp("order_date"));

        return order;
    }
}
