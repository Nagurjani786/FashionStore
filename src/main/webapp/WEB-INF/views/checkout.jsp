
<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         isELIgnored="false" %>

<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.fashionstore.model.CartItem" %>
<%@ page import="com.fashionstore.model.User" %>

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

    List<CartItem> checkoutItems =
        (List<CartItem>) request.getAttribute("checkoutItems");

    BigDecimal checkoutTotal =
        (BigDecimal) request.getAttribute("checkoutTotal");

    User checkoutUser =
        (User) request.getAttribute("checkoutUser");

    String checkoutError =
        (String) request.getAttribute("checkoutError");

    if (checkoutTotal == null) {
        checkoutTotal = BigDecimal.ZERO;
    }

    if (checkoutItems == null || checkoutItems.isEmpty()) {
        response.sendRedirect(contextPath + "/cart");
        return;
    }

    String name = checkoutUser != null
            ? checkoutUser.getFullName() : "";

    String phone = checkoutUser != null
            ? checkoutUser.getPhone() : "";

    String address = checkoutUser != null
            ? checkoutUser.getAddress() : "";

    String city = checkoutUser != null
            ? checkoutUser.getCity() : "";

    String state = checkoutUser != null
            ? checkoutUser.getState() : "";

    String postalCode = checkoutUser != null
            ? checkoutUser.getPostalCode() : "";
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Checkout | FashionStore</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/style.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/checkout.css">
</head>

<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="checkout-container">

        <section class="checkout-heading">
            <h1>Checkout</h1>
            <p>Confirm your delivery details and review your order.</p>
        </section>

        <% if (checkoutError != null) { %>
            <div class="error-message" role="alert">
                <%= escapeHtml(checkoutError) %>
            </div>
        <% } %>

        <form action="<%= contextPath %>/checkout" method="post">

            <div class="checkout-layout">

                <!-- DELIVERY DETAILS -->
                <div class="checkout-left">

                    <section class="checkout-card">
                        <h2>Delivery Information</h2>

                        <div class="form-group">
                            <label for="deliveryName">Full Name *</label>

                            <input type="text"
                                   id="deliveryName"
                                   name="deliveryName"
                                   value="<%= escapeHtml(name) %>"
                                   maxlength="100"
                                   autocomplete="name"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="phone">Mobile Number *</label>

                            <input type="tel"
                                   id="phone"
                                   name="phone"
                                   value="<%= escapeHtml(phone) %>"
                                   pattern="[0-9]{10}"
                                   maxlength="10"
                                   inputmode="numeric"
                                   autocomplete="tel"
                                   placeholder="10-digit mobile number"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="deliveryAddress">Full Address *</label>

                            <textarea id="deliveryAddress"
                                      name="deliveryAddress"
                                      maxlength="500"
                                      autocomplete="street-address"
                                      placeholder="House number, street, area"
                                      required><%= escapeHtml(address) %></textarea>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="deliveryCity">City *</label>

                                <input type="text"
                                       id="deliveryCity"
                                       name="deliveryCity"
                                       value="<%= escapeHtml(city) %>"
                                       maxlength="100"
                                       autocomplete="address-level2"
                                       required>
                            </div>

                            <div class="form-group">
                                <label for="deliveryState">State *</label>

                                <input type="text"
                                       id="deliveryState"
                                       name="deliveryState"
                                       value="<%= escapeHtml(state) %>"
                                       maxlength="100"
                                       autocomplete="address-level1"
                                       required>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="deliveryPostalCode">PIN Code *</label>

                            <input type="text"
                                   id="deliveryPostalCode"
                                   name="deliveryPostalCode"
                                   value="<%= escapeHtml(postalCode) %>"
                                   pattern="[0-9]{6}"
                                   maxlength="6"
                                   inputmode="numeric"
                                   autocomplete="postal-code"
                                   placeholder="6-digit PIN code"
                                   required>
                        </div>
                    </section>

                    <!-- PAYMENT METHOD -->
                    <section class="checkout-card">
                        <h2>Payment Method</h2>

                        <label class="payment-option">
                            <input type="radio"
                                   name="paymentMethod"
                                   value="COD"
                                   checked
                                   required>

                            <span>
                                <strong>Cash on Delivery</strong>
                                <small>Pay when your order arrives.</small>
                            </span>
                        </label>
                    </section>

                </div>

                <!-- ORDER SUMMARY -->
                <aside class="checkout-right">
                    <section class="checkout-card order-summary">

                        <h2>Your Order</h2>

                        <div class="order-items">

                            <% for (CartItem item : checkoutItems) {
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

                                <div class="order-item">

                                    <img class="item-image"
                                         src="<%= escapeHtml(finalImageUrl) %>"
                                         alt="<%= escapeHtml(item.getProductName()) %>"
                                         onerror="this.onerror=null;this.src='<%= contextPath %>/assets/images/MENSHoodie.jpg';">

                                    <div class="item-details">
                                        <h3>
                                            <%= escapeHtml(item.getProductName()) %>
                                        </h3>

                                        <% if (item.getSize() != null) { %>
                                            <p>Size: <%= escapeHtml(item.getSize()) %></p>
                                        <% } %>

                                        <p>Quantity: <%= item.getQuantity() %></p>
                                        <p>Price: ₹<%= item.getPrice() %></p>
                                    </div>

                                    <div class="item-price">
                                        ₹<%= item.getSubtotal() %>
                                    </div>

                                </div>

                            <% } %>

                        </div>

                        <div class="summary-row">
                            <span>Subtotal</span>
                            <strong>₹<%= checkoutTotal %></strong>
                        </div>

                        <div class="summary-row">
                            <span>Delivery</span>
                            <strong>Free</strong>
                        </div>

                        <div class="summary-row summary-total">
                            <span>Total</span>
                            <span>₹<%= checkoutTotal %></span>
                        </div>

                        <button type="submit"
                                class="place-order-button">
                            Place Order
                        </button>

                        <a class="back-to-cart"
                           href="<%= contextPath %>/cart">
                            &#8592; Return to Cart
                        </a>

                        <p class="secure-note">
                            Payment is collected upon delivery.
                        </p>

                    </section>
                </aside>

            </div>
        </form>

    </main>

    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
