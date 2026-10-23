
<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.fashionstore.model.CartItem" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>

<%!
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>

<%
    String contextPath = request.getContextPath();

    Collection<CartItem> cartItems =
        (Collection<CartItem>) request.getAttribute("cartItems");

    if (cartItems == null) {
        cartItems = Collections.emptyList();
    }

    BigDecimal cartTotal =
        (BigDecimal) request.getAttribute("cartTotal");

    if (cartTotal == null) {
        cartTotal = BigDecimal.ZERO;
    }

    String error = request.getParameter("error");
    String success = request.getParameter("success");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>My Shopping Cart | FashionStore</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/style.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/cart.css">

    <style>
        .checkout-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            min-height: 60px;
            margin-top: 20px;
            padding: 16px 20px;
            background: #171717;
            color: #ffffff;
            border: 1px solid #171717;
            border-radius: 8px;
            text-decoration: none;
            text-align: center;
            font-family: inherit;
            font-size: 18px;
            font-weight: 700;
            cursor: pointer;
            transition: background 0.2s ease;
        }

        .checkout-btn:hover {
            background: #c59d5f;
            border-color: #c59d5f;
            color: #ffffff;
        }
    </style>
</head>

<body>

    <%@ include file="partials/navbar.jsp" %>

    <main class="cart-page">
        <div class="cart-container">

            <div class="cart-heading">
                <h1>My Shopping Cart</h1>
                <p>Review your items before proceeding to checkout.</p>
            </div>

            <% if ("added".equals(success)) { %>
                <div class="cart-message success-message">
                    Product added to your cart successfully!
                </div>
            <% } else if ("updated".equals(success)) { %>
                <div class="cart-message success-message">
                    Cart quantity updated successfully!
                </div>
            <% } else if ("removed".equals(success)) { %>
                <div class="cart-message success-message">
                    Product removed from your cart.
                </div>
            <% } %>

            <% if ("stock".equals(error)) { %>
                <div class="cart-message error-message">
                    Requested quantity is unavailable. Please check stock.
                </div>
            <% } else if ("invalid".equals(error)) { %>
                <div class="cart-message error-message">
                    Invalid product or quantity. Please try again.
                </div>
            <% } else if ("notfound".equals(error)) { %>
                <div class="cart-message error-message">
                    The selected cart item could not be found.
                </div>
            <% } %>

            <% if (cartItems.isEmpty()) { %>

                <section class="empty-cart">
                    <div class="empty-cart-icon">&#128722;</div>
                    <h2>Your cart is empty</h2>
                    <p>You haven't added any products to your cart yet.</p>

                    <a href="<%= contextPath %>/home"
                       class="continue-shopping-btn">
                        Continue Shopping
                    </a>
                </section>

            <% } else { %>

                <div class="cart-layout">

                    <section class="cart-items-section">

                        <div class="cart-items-header">
                            <h2>Cart Items</h2>
                            <span>
                                <%= cartItems.size() %> item(s)
                            </span>
                        </div>

                        <% for (CartItem item : cartItems) {
                            String imageUrl = item.getImageUrl();
                            String finalImageUrl;

                            if (imageUrl == null
                                    || imageUrl.trim().isEmpty()
                                    || imageUrl.startsWith("file:")) {
                                finalImageUrl =
                                    contextPath + "/assets/images/MENSHoodie.jpg";
                            } else if (imageUrl.startsWith("http://")
                                    || imageUrl.startsWith("https://")) {
                                finalImageUrl = imageUrl;
                            } else if (imageUrl.startsWith("/")) {
                                finalImageUrl = contextPath + imageUrl;
                            } else {
                                finalImageUrl = contextPath + "/" + imageUrl;
                            }
                        %>

                            <article class="cart-item">

                                <div class="cart-product-image">
                                    <img
                                        src="<%= escapeHtml(finalImageUrl) %>"
                                        alt="<%= escapeHtml(item.getProductName()) %>"
                                        onerror="this.onerror=null;this.src='<%= contextPath %>/assets/images/MENSHoodie.jpg';">
                                </div>

                                <div class="cart-product-details">

                                    <h3>
                                        <%= escapeHtml(item.getProductName()) %>
                                    </h3>

                                    <% if (item.getBrand() != null
                                            && !item.getBrand().trim().isEmpty()) { %>
                                        <p class="cart-brand">
                                            Brand: <%= escapeHtml(item.getBrand()) %>
                                        </p>
                                    <% } %>

                                    <p class="cart-size">
                                        Size: <%= escapeHtml(item.getSize()) %>
                                    </p>

                                    <p class="cart-price">
                                        ₹<%= item.getPrice().setScale(
                                            2, RoundingMode.HALF_UP) %>
                                    </p>

                                    <div class="cart-item-actions">

                                        <form action="<%= contextPath %>/cart"
                                              method="post"
                                              class="quantity-form">

                                            <input type="hidden"
                                                   name="action"
                                                   value="update">

                                            <input type="hidden"
                                                   name="variantId"
                                                   value="<%= item.getVariantId() %>">

                                            <label for="quantity-<%= item.getVariantId() %>">
                                                Quantity:
                                            </label>

                                            <input
                                                type="number"
                                                id="quantity-<%= item.getVariantId() %>"
                                                name="quantity"
                                                min="1"
                                                max="100"
                                                value="<%= item.getQuantity() %>"
                                                required>

                                            <button type="submit"
                                                    class="update-cart-btn">
                                                Update
                                            </button>
                                        </form>

                                        <form action="<%= contextPath %>/cart"
                                              method="post"
                                              class="remove-form"
                                              onsubmit="return confirm('Remove this product from your cart?');">

                                            <input type="hidden"
                                                   name="action"
                                                   value="remove">

                                            <input type="hidden"
                                                   name="variantId"
                                                   value="<%= item.getVariantId() %>">

                                            <button type="submit"
                                                    class="remove-cart-btn">
                                                Remove
                                            </button>
                                        </form>

                                    </div>
                                </div>

                                <div class="cart-item-subtotal">
                                    <span>Subtotal</span>
                                    <strong>
                                        ₹<%= item.getSubtotal().setScale(
                                            2, RoundingMode.HALF_UP) %>
                                    </strong>
                                </div>

                            </article>

                        <% } %>

                        <a href="<%= contextPath %>/home"
                           class="back-to-shopping">
                            &larr; Continue Shopping
                        </a>

                    </section>

                    <aside class="cart-summary">

                        <h2>Order Summary</h2>

                        <div class="summary-row">
                            <span>Items</span>
                            <span><%= cartItems.size() %></span>
                        </div>

                        <div class="summary-row total-row">
                            <strong>Subtotal</strong>
                            <strong>
                                ₹<%= cartTotal.setScale(
                                    2, RoundingMode.HALF_UP) %>
                            </strong>
                        </div>

                        <p class="shipping-note">
                            Shipping and final charges will be calculated
                            during checkout.
                        </p>

                        <!-- FIXED: enabled checkout link -->
                        <a href="<%= contextPath %>/checkout"
                           class="checkout-btn">
                            Proceed to Checkout
                        </a>

                    </aside>

                </div>

            <% } %>

        </div>
    </main>

    <%@ include file="partials/footer.jsp" %>

</body>
</html>
