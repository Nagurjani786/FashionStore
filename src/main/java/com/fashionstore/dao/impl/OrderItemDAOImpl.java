
package com.fashionstore.dao.impl;

import com.fashionstore.dao.OrderItemDAO;
import com.fashionstore.model.OrderItem;
import com.fashionstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAOImpl implements OrderItemDAO {

    @Override
    public boolean addOrderItem(OrderItem orderItem)
            throws SQLException {

        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);

        return addOrderItems(items);
    }

    @Override
    public boolean addOrderItems(List<OrderItem> orderItems)
            throws SQLException {

        if (orderItems == null || orderItems.isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO order_items "
                + "(order_id, variant_id, product_name, size, color, "
                + "quantity, unit_price, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            boolean originalAutoCommit = con.getAutoCommit();

            try {
                con.setAutoCommit(false);

                try (PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    for (OrderItem item : orderItems) {

                        if (item.getQuantity() <= 0) {
                            throw new SQLException(
                                    "Order item quantity must be positive.");
                        }

                        ps.setInt(1, item.getOrderId());
                        ps.setInt(2, item.getVariantId());
                        ps.setString(3, item.getProductName());
                        ps.setString(4, item.getSize());
                        ps.setString(5, item.getColor());
                        ps.setInt(6, item.getQuantity());
                        ps.setBigDecimal(7, item.getUnitPrice());
                        ps.setBigDecimal(8, item.getSubtotal());

                        ps.addBatch();
                    }

                    int[] results = ps.executeBatch();

                    for (int result : results) {
                        if (result == Statement.EXECUTE_FAILED) {
                            throw new SQLException(
                                    "Failed to insert an order item.");
                        }
                    }
                }

                con.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(originalAutoCommit);
            }
        }
    }

    @Override
    public OrderItem getOrderItemById(int orderItemId)
            throws SQLException {

        String sql = "SELECT * FROM order_items "
                + "WHERE order_item_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderItemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrderItem(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId)
            throws SQLException {

        List<OrderItem> items = new ArrayList<>();

        String sql = "SELECT * FROM order_items "
                + "WHERE order_id = ? ORDER BY order_item_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapOrderItem(rs));
                }
            }
        }

        return items;
    }

    @Override
    public boolean deleteOrderItem(int orderItemId)
            throws SQLException {

        String sql = "DELETE FROM order_items "
                + "WHERE order_item_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderItemId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteOrderItemsByOrderId(int orderId)
            throws SQLException {

        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            return ps.executeUpdate() > 0;
        }
    }

    private OrderItem mapOrderItem(ResultSet rs)
            throws SQLException {

        OrderItem item = new OrderItem();

        item.setOrderItemId(rs.getInt("order_item_id"));
        item.setOrderId(rs.getInt("order_id"));
        item.setVariantId(rs.getInt("variant_id"));
        item.setProductName(rs.getString("product_name"));
        item.setSize(rs.getString("size"));
        item.setColor(rs.getString("color"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setSubtotal(rs.getBigDecimal("subtotal"));

        return item;
    }
}
