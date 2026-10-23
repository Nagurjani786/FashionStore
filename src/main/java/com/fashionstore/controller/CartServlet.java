
package com.fashionstore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.ProductVariantDAO;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.dao.impl.ProductVariantDAOImpl;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.Product;
import com.fashionstore.model.ProductVariant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ProductDAO productDAO = new ProductDAOImpl();
    private final ProductVariantDAO variantDAO =
            new ProductVariantDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart =
                (Map<String, CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }

        request.setAttribute("cartItems", cart.values());

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.values()) {
            total = total.add(item.getSubtotal());
        }

        request.setAttribute("cartTotal", total);

        request.getRequestDispatcher(
                "/WEB-INF/views/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String contextPath = request.getContextPath();
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                addToCart(request, response);
                return;
            }

            if ("update".equals(action)) {
                updateCart(request, response);
                return;
            }

            if ("remove".equals(action)) {
                removeFromCart(request, response);
                return;
            }

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid cart action.");

        } catch (NumberFormatException e) {
            response.sendRedirect(
                    contextPath + "/cart?error=invalid");
        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to process shopping cart.", e);
        }
    }

    private void addToCart(HttpServletRequest request,
                           HttpServletResponse response)
            throws SQLException, IOException {

        String contextPath = request.getContextPath();

        int productId = Integer.parseInt(
                request.getParameter("productId"));

        int variantId = Integer.parseInt(
                request.getParameter("variantId"));

        int quantity = Integer.parseInt(
                request.getParameter("quantity"));

        if (productId <= 0 || variantId <= 0 || quantity <= 0) {
            response.sendRedirect(
                    contextPath + "/product?id=" + productId
                    + "&error=invalid");
            return;
        }

        Product product = productDAO.getProductById(productId);
        ProductVariant variant = variantDAO.getVariantById(variantId);

        if (product == null || variant == null
                || variant.getProductId() != productId) {

            response.sendRedirect(
                    contextPath + "/product?id=" + productId
                    + "&error=invalid");
            return;
        }

        HttpSession session = request.getSession();

        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart =
                (Map<String, CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
        }

        String cartKey = productId + "-" + variantId;
        CartItem existingItem = cart.get(cartKey);

        int newQuantity = quantity;

        if (existingItem != null) {
            newQuantity = existingItem.getQuantity() + quantity;
        }

        if (!variantDAO.isStockAvailable(variantId, newQuantity)) {
            response.sendRedirect(
                    contextPath + "/product?id=" + productId
                    + "&error=stock");
            return;
        }

        if (existingItem != null) {
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem item = new CartItem();

            item.setProductId(product.getProductId());
            item.setVariantId(variant.getVariantId());
            item.setProductName(product.getProductName());
            item.setBrand(product.getBrand());
            item.setImageUrl(product.getImageUrl());
            item.setSize(variant.getSize());

            // Supports numeric price types and BigDecimal.
            item.setPrice(
                    new BigDecimal(String.valueOf(product.getPrice())));

            item.setQuantity(quantity);

            cart.put(cartKey, item);
        }

        session.setAttribute("cart", cart);

        response.sendRedirect(contextPath + "/cart?success=added");
    }

    private void updateCart(HttpServletRequest request,
                            HttpServletResponse response)
            throws SQLException, IOException {

        String contextPath = request.getContextPath();

        int variantId = Integer.parseInt(
                request.getParameter("variantId"));

        int quantity = Integer.parseInt(
                request.getParameter("quantity"));

        if (variantId <= 0 || quantity <= 0) {
            response.sendRedirect(
                    contextPath + "/cart?error=invalid");
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(contextPath + "/cart");
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart =
                (Map<String, CartItem>) session.getAttribute("cart");

        if (cart == null) {
            response.sendRedirect(contextPath + "/cart");
            return;
        }

        CartItem targetItem = null;

        for (CartItem item : cart.values()) {
            if (item.getVariantId() == variantId) {
                targetItem = item;
                break;
            }
        }

        if (targetItem == null) {
            response.sendRedirect(
                    contextPath + "/cart?error=notfound");
            return;
        }

        if (!variantDAO.isStockAvailable(variantId, quantity)) {
            response.sendRedirect(
                    contextPath + "/cart?error=stock");
            return;
        }

        targetItem.setQuantity(quantity);

        session.setAttribute("cart", cart);

        response.sendRedirect(contextPath + "/cart?success=updated");
    }

    private void removeFromCart(HttpServletRequest request,
                                HttpServletResponse response)
            throws IOException {

        String contextPath = request.getContextPath();

        int variantId = Integer.parseInt(
                request.getParameter("variantId"));

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(contextPath + "/cart");
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart =
                (Map<String, CartItem>) session.getAttribute("cart");

        if (cart != null) {
            cart.entrySet().removeIf(
                    entry -> entry.getValue().getVariantId() == variantId);

            session.setAttribute("cart", cart);
        }

        response.sendRedirect(contextPath + "/cart?success=removed");
    }
}
