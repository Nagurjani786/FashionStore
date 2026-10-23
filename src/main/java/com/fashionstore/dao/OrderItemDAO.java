
package com.fashionstore.dao;

import com.fashionstore.model.OrderItem;
import java.sql.SQLException;
import java.util.List;

public interface OrderItemDAO {

    // Add a single item to an order
    boolean addOrderItem(OrderItem orderItem) throws SQLException;

    // Add multiple items to an order
    boolean addOrderItems(List<OrderItem> orderItems)
            throws SQLException;

    // Retrieve an order item by ID
    OrderItem getOrderItemById(int orderItemId) throws SQLException;

    // Retrieve all items belonging to an order
    List<OrderItem> getOrderItemsByOrderId(int orderId)
            throws SQLException;

    // Delete a single order item
    boolean deleteOrderItem(int orderItemId) throws SQLException;

    // Delete all items belonging to an order
    boolean deleteOrderItemsByOrderId(int orderId)
            throws SQLException;
}
