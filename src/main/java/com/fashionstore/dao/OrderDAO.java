
package com.fashionstore.dao;

import com.fashionstore.model.Order;
import java.sql.SQLException;
import java.util.List;

public interface OrderDAO {

    // Place a new order
    boolean placeOrder(Order order) throws SQLException;

    // Retrieve an order by ID
    Order getOrderById(int orderId) throws SQLException;

    // Retrieve all orders belonging to a customer
    List<Order> getOrdersByUserId(int userId) throws SQLException;

    // Retrieve all orders
    List<Order> getAllOrders() throws SQLException;

    // Update order status
    boolean updateOrderStatus(int orderId, String orderStatus)
            throws SQLException;

    // Cancel an order without deleting its history
    boolean cancelOrder(int orderId) throws SQLException;
}
