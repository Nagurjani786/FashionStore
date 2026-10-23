
package com.fashionstore.dao;

import com.fashionstore.model.CartItem;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface CartItemDAO {

    boolean addCartItem(CartItem cartItem) throws SQLException;

    boolean updateCartItemQuantity(
            int cartItemId, int quantity) throws SQLException;

    boolean updateCartItemQuantityByCartAndVariant(
            int cartId, int variantId, int quantity) throws SQLException;

    boolean removeCartItem(int cartItemId) throws SQLException;

    boolean removeCartItemByCartAndVariant(
            int cartId, int variantId) throws SQLException;

    CartItem getCartItemById(int cartItemId) throws SQLException;

    CartItem getCartItemByCartIdAndVariantId(
            int cartId, int variantId) throws SQLException;

    List<CartItem> getCartItemsByCartId(int cartId) throws SQLException;

    boolean clearCart(int cartId) throws SQLException;

    int getCartItemCount(int cartId) throws SQLException;

    BigDecimal getCartTotal(int cartId) throws SQLException;
}
