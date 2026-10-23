package com.fashionstore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.dao.ProductVariantDAO;
import com.fashionstore.dao.impl.OrderDAOImpl;
import com.fashionstore.dao.impl.ProductVariantDAOImpl;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.Order;
import com.fashionstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {
    "/checkout",
    "/checkout/confirmation"
})
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final OrderDAO orderDAO = new OrderDAOImpl();

    private final ProductVariantDAO variantDAO =
            new ProductVariantDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Display the order confirmation page.
        if ("/checkout/confirmation".equals(
                request.getServletPath())) {

            if (session == null) {
                response.sendRedirect(
                        request.getContextPath() + "/cart");
                return;
            }

            Order lastOrder =
                    (Order) session.getAttribute("lastOrder");

            if (lastOrder == null) {
                response.sendRedirect(
                        request.getContextPath() + "/cart");
                return;
            }

            request.setAttribute("order", lastOrder);

            request.getRequestDispatcher(
                    "/WEB-INF/views/order-confirmation.jsp")
                    .forward(request, response);

            return;
        }

        // Require a logged-in customer.
        User user = getLoggedInUser(session);

        if (user == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        Map<String, CartItem> cart = getCart(session);

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(
                    request.getContextPath() + "/cart");
            return;
        }

        prepareCheckout(request, cart, user);

        request.getRequestDispatcher(
                "/WEB-INF/views/checkout.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String contextPath = request.getContextPath();

        HttpSession session = request.getSession(false);

        User user = getLoggedInUser(session);

        if (user == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        Map<String, CartItem> cart = getCart(session);

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(contextPath + "/cart");
            return;
        }

        String deliveryName =
                trim(request.getParameter("deliveryName"));

        String deliveryAddress =
                trim(request.getParameter("deliveryAddress"));

        String deliveryCity =
                trim(request.getParameter("deliveryCity"));

        String deliveryState =
                trim(request.getParameter("deliveryState"));

        String deliveryPostalCode =
                trim(request.getParameter("deliveryPostalCode"));

        String phone =
                trim(request.getParameter("phone"));

        String paymentMethod =
                trim(request.getParameter("paymentMethod"));

        // Validate delivery details.
        if (deliveryName.isEmpty()
                || deliveryAddress.isEmpty()
                || deliveryCity.isEmpty()
                || deliveryState.isEmpty()
                || !deliveryPostalCode.matches("[0-9]{6}")
                || !phone.matches("[0-9]{10}")) {

            showError(
                    request,
                    response,
                    cart,
                    user,
                    "Please enter valid delivery details."
            );

            return;
        }

        // Only Cash on Delivery is supported currently.
        if (!"COD".equals(paymentMethod)) {

            showError(
                    request,
                    response,
                    cart,
                    user,
                    "Please select Cash on Delivery."
            );

            return;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        try {
            // Verify quantities against the current stock.
            for (CartItem item : cart.values()) {

                if (item.getQuantity() <= 0
                        || !variantDAO.isStockAvailable(
                                item.getVariantId(),
                                item.getQuantity())) {

                    showError(
                            request,
                            response,
                            cart,
                            user,
                            "A product is unavailable in the "
                                    + "requested quantity. "
                                    + "Please update your cart."
                    );

                    return;
                }

                totalAmount = totalAmount.add(
                        item.getSubtotal());
            }

            if (totalAmount.signum() <= 0) {

                showError(
                        request,
                        response,
                        cart,
                        user,
                        "The order total must be greater than zero."
                );

                return;
            }

            // Create the order.
            Order order = new Order();

            order.setUserId(user.getUserId());
            order.setTotalAmount(totalAmount);
            order.setOrderStatus("PENDING");
            order.setPaymentMethod("Cash on Delivery");

            order.setDeliveryName(deliveryName);
            order.setDeliveryAddress(deliveryAddress);
            order.setDeliveryCity(deliveryCity);
            order.setDeliveryState(deliveryState);
            order.setDeliveryPostalCode(deliveryPostalCode);
            order.setPhone(phone);

            order.setOrderedAt(
                    new Timestamp(System.currentTimeMillis()));

            // Save the order using your existing DAO.
            boolean saved = orderDAO.placeOrder(order);

            if (!saved) {

                showError(
                        request,
                        response,
                        cart,
                        user,
                        "Unable to place your order. Please try again."
                );

                return;
            }

            /*
             * Keep a snapshot of the cart for the confirmation
             * page during this session.
             */
            session.setAttribute(
                    "lastOrderItems",
                    new ArrayList<>(cart.values()));

            session.setAttribute("lastOrder", order);

            // Empty the shopping cart after successful placement.
            session.removeAttribute("cart");

            // Redirect after POST to prevent accidental resubmission.
            response.sendRedirect(
                    contextPath + "/checkout/confirmation");

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to process checkout.", e);
        }
    }

    private User getLoggedInUser(HttpSession session) {

        if (session == null) {
            return null;
        }

        // Supports either common session attribute name.
        Object loggedInUser =
                session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            loggedInUser = session.getAttribute("user");
        }

        if (loggedInUser instanceof User) {
            return (User) loggedInUser;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, CartItem> getCart(HttpSession session) {

        if (session == null) {
            return null;
        }

        return (Map<String, CartItem>)
                session.getAttribute("cart");
    }

    private String trim(String value) {

        return value == null ? "" : value.trim();
    }

    private void prepareCheckout(
            HttpServletRequest request,
            Map<String, CartItem> cart,
            User user) {

        BigDecimal total = BigDecimal.ZERO;

        List<CartItem> items =
                new ArrayList<>(cart.values());

        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }

        request.setAttribute("checkoutItems", items);
        request.setAttribute("checkoutTotal", total);
        request.setAttribute("checkoutUser", user);
    }

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            Map<String, CartItem> cart,
            User user,
            String message)
            throws ServletException, IOException {

        request.setAttribute("checkoutError", message);

        prepareCheckout(request, cart, user);

        request.getRequestDispatcher(
                "/WEB-INF/views/checkout.jsp")
                .forward(request, response);
    }
}