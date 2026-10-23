
package com.fashionstore.dao;

import com.fashionstore.model.Cart;
import java.sql.SQLException;

public interface CartDAO {

    boolean createCart(int userId) throws SQLException;

    Cart getCartById(int cartId) throws SQLException;

    Cart getCartByUserId(int userId) throws SQLException;

    Cart getOrCreateCartByUserId(int userId) throws SQLException;

    boolean deleteCart(int cartId) throws SQLException;

    boolean cartExistsByUserId(int userId) throws SQLException;
}
