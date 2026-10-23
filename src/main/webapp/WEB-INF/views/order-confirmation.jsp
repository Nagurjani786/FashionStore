
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.fashionstore.model.Order" %>
<%@ page import="com.fashionstore.model.CartItem" %>

<%!
    private String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    private String imagePath(String image, String contextPath) {
        if (image == null || image.trim().isEmpty()
                || image.startsWith("file:")) {
            return contextPath + "/assets/images/MENSHoodie.jpg";
        }

        if (image.startsWith("http://") || image.startsWith("https://")) {
            return image;
        }

        if (image.startsWith("/")) {
            return contextPath + image;
        }

        return contextPath + "/" + image;
    }
%>

<%
    String ctx = request.getContextPath();

    Order order = (Order) request.getAttribute("order");

    if (order == null && session != null) {
        order = (Order) session.getAttribute("lastOrder");
    }

    if (order == null) {
        response.sendRedirect(ctx + "/cart");
        return;
    }

    List<CartItem> items =
        (List<CartItem>) session.getAttribute("lastOrderItems");

    if (items == null) {
        items = java.util.Collections.emptyList();
    }

    String orderDate = "Recently placed";
    if (order.getOrderedAt() != null) {
        orderDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a")
                .format(order.getOrderedAt());
    }

    String payment = order.getPaymentMethod();
    if (payment == null || payment.trim().isEmpty()) {
        payment = "Cash on Delivery";
    } else if ("COD".equalsIgnoreCase(payment)) {
        payment = "Cash on Delivery";
    }

    String status = order.getOrderStatus();
    if (status == null || status.trim().isEmpty()) {
        status = "PLACED";
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Details | FashionStore</title>

    <link rel="stylesheet"
          href="<%= ctx %>/assets/css/style.css">

    <link rel="stylesheet"
          href="<%= ctx %>/assets/css/order-confirmation.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<main class="order-page">

    <section class="success-banner">
        <div class="success-check">&#10003;</div>
        <div>
            <h1>Thank you for your order!</h1>
            <p>Your order has been placed. Here are your order details.</p>
        </div>
    </section>

    <section class="order-meta-card">
        <div class="meta-item">
            <span>ORDER NUMBER</span>
            <strong>#FS<%= order.getOrderId() %></strong>
        </div>

        <div class="meta-item">
            <span>ORDER DATE</span>
            <strong><%= esc(orderDate) %></strong>
        </div>

        <div class="meta-item">
            <span>ORDER STATUS</span>
            <strong class="status-pill"><%= esc(status) %></strong>
        </div>
    </section>

    <div class="order-page-grid">

        <section class="order-products-card">
            <h2>Ordered Items</h2>

            <% if (items.isEmpty()) { %>
                <p class="empty-items">
                    Your order was saved. The item details are not
                    available in this session.
                </p>
            <% } else { %>

                <% for (CartItem item : items) {
                    String img = imagePath(item.getImageUrl(), ctx);
                %>

                    <article class="confirmed-product">
                        <div class="confirmed-product-image">
                            <img
                                src="<%= esc(img) %>"
                                alt="<%= esc(item.getProductName()) %>"
                                onerror="this.onerror=null;this.src='<%= ctx %>/assets/images/MENSHoodie.jpg';">
                        </div>

                        <div class="confirmed-product-info">
                            <h3><%= esc(item.getProductName()) %></h3>

                            <% if (item.getBrand() != null
                                  && !item.getBrand().trim().isEmpty()) { %>
                                <p class="product-brand">
                                    <%= esc(item.getBrand()) %>
                                </p>
                            <% } %>

                            <p>Size: <strong><%= esc(item.getSize()) %></strong></p>
                            <p>Quantity: <strong><%= item.getQuantity() %></strong></p>
                            <p>Unit price: ₹<%= item.getPrice() %></p>
                        </div>

                        <div class="confirmed-product-total">
                            ₹<%= item.getSubtotal() %>
                        </div>
                    </article>

                <% } %>

            <% } %>

            <div class="delivery-note">
                <span class="delivery-icon">&#128666;</span>
                <div>
                    <strong>Delivery details confirmed</strong>
                    <p>Your order will be delivered to the address below.</p>
                </div>
            </div>
        </section>

        <aside class="order-side-column">

            <section class="delivery-card">
                <h2>Delivery Address</h2>

                <p class="delivery-name">
                    <%= esc(order.getDeliveryName()) %>
                </p>

                <p><%= esc(order.getDeliveryAddress()) %></p>
                <p>
                    <%= esc(order.getDeliveryCity()) %>,
                    <%= esc(order.getDeliveryState()) %>
                </p>
                <p>PIN: <%= esc(order.getDeliveryPostalCode()) %></p>
                <p>Phone: <%= esc(order.getDeliveryPhone()) %></p>
            </section>

            <section class="payment-card">
                <h2>Payment Information</h2>

                <div class="payment-detail">
                    <span>Payment method</span>
                    <strong><%= esc(payment) %></strong>
                </div>

                <div class="payment-detail">
                    <span>Payment status</span>
                    <strong class="payment-pending">
                        <% if ("Cash on Delivery".equals(payment)) { %>
                            Pay on delivery
                        <% } else { %>
                            <%= esc(status) %>
                        <% } %>
                    </strong>
                </div>
            </section>

            <section class="price-card">
                <h2>Price Details</h2>

                <div class="price-row">
                    <span>Items (<%= items.size() %>)</span>
                    <span>₹<%= order.getTotalAmount() %></span>
                </div>

                <div class="price-row">
                    <span>Delivery</span>
                    <span class="free-delivery">Included</span>
                </div>

                <div class="price-row final-price">
                    <strong>Order Total</strong>
                    <strong>₹<%= order.getTotalAmount() %></strong>
                </div>

                <p class="price-disclaimer">
                    The total shown is the amount saved with your order.
                </p>
            </section>

        </aside>
    </div>

    <div class="order-actions">
        <a class="primary-order-button" href="<%= ctx %>/products">
            Continue Shopping
        </a>

        <a class="secondary-order-button" href="<%= ctx %>/home">
            Back to Home
        </a>
    </div>

</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
