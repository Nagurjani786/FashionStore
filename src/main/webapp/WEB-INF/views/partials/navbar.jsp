
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         isELIgnored="false" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.fashionstore.model.CartItem" %>

<%
    String navContextPath = request.getContextPath();
    int cartCount = 0;

    Object cartObject = session.getAttribute("cart");

    if (cartObject instanceof Map<?, ?>) {
        Map<?, ?> cart = (Map<?, ?>) cartObject;

        for (Object value : cart.values()) {
            if (value instanceof CartItem) {
                CartItem item = (CartItem) value;
                cartCount += item.getQuantity();
            }
        }
    }

    Object loggedInUser = session.getAttribute("user");
%>

<header class="navbar">
    <div class="container navbar-container">

        <div class="logo">
            <a href="<%= navContextPath %>/home">
                Fashion<span>Store</span>
            </a>
        </div>

        <form class="search-bar"
              action="<%= navContextPath %>/products"
              method="get">

            <input type="text"
                   name="keyword"
                   placeholder="Search for fashion products...">

            <button type="submit">Search</button>
        </form>

        <nav class="nav-links">
            <a href="<%= navContextPath %>/home">Home</a>
            <a href="<%= navContextPath %>/products">Products</a>

            <a href="<%= navContextPath %>/cart"
               class="nav-cart"
               aria-label="Shopping cart, <%= cartCount %> items">

                <span class="cart-icon" aria-hidden="true">&#128722;</span>
                <span>Cart</span>

                <% if (cartCount > 0) { %>
                    <span class="cart-count"><%= cartCount %></span>
                <% } %>
            </a>

            <% if (loggedInUser != null) { %>
                <a href="<%= navContextPath %>/profile">Profile</a>
            <% } else { %>
                <a href="<%= navContextPath %>/login">Login</a>
            <% } %>

        </nav>

    </div>
</header>
