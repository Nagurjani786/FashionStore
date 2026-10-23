
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         isELIgnored="false" %>
<%@ page import="com.fashionstore.model.Product" %>
<%@ page import="com.fashionstore.model.ProductVariant" %>
<%@ page import="java.util.List" %>

<%
    Product product = (Product) request.getAttribute("product");
    List<ProductVariant> variants =
            (List<ProductVariant>) request.getAttribute("variants");

    String contextPath = request.getContextPath();
    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Product Details | FashionStore</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/style.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/assets/css/product-details.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="product-page">

        <% if (product == null) { %>

            <section class="not-found">
                <h1>Product Not Found</h1>
                <p>The requested product could not be found.</p>

                <a href="<%= contextPath %>/home"
                   class="primary-button">
                    Back to Home
                </a>
            </section>

        <% } else { %>

            <div class="breadcrumb">
                <a href="<%= contextPath %>/home">Home</a>
                <span> / </span>
                <a href="<%= contextPath %>/products">Products</a>
                <span> / </span>
                <span><%= product.getProductName() %></span>
            </div>

            <section class="product-details">

                <div class="product-image-container">

                    <% if (product.getImageUrl() != null
                            && !product.getImageUrl().trim().isEmpty()) { %>

                        <img
                            src="<%= product.getImageUrl() %>"
                            alt="<%= product.getProductName() %>"
                            class="product-image"
                            onerror="this.style.display='none';
                                     this.nextElementSibling.style.display='flex';">

                        <div class="image-placeholder" style="display:none;">
                            Image unavailable
                        </div>

                    <% } else { %>

                        <div class="image-placeholder">
                            No image available
                        </div>

                    <% } %>

                </div>

                <div class="product-info">

                    <p class="product-brand">
                        <%= product.getBrand() != null
                                ? product.getBrand() : "FashionStore" %>
                    </p>

                    <h1 class="product-name">
                        <%= product.getProductName() %>
                    </h1>

                    <p class="product-price">
                        ₹<%= product.getPrice() %>
                    </p>

                    <div class="product-description">
                        <h3>Product Description</h3>
                        <p>
                            <%= product.getDescription() != null
                                    && !product.getDescription().trim().isEmpty()
                                    ? product.getDescription()
                                    : "No description available." %>
                        </p>
                    </div>

                    <% if ("stock".equals(error)) { %>
                        <p class="cart-error-message">
                            The requested quantity is unavailable.
                            Please select a smaller quantity or another size.
                        </p>
                    <% } else if ("invalid".equals(error)) { %>
                        <p class="cart-error-message">
                            Invalid product, size, or quantity. Please try again.
                        </p>
                    <% } %>

                    <form action="<%= contextPath %>/cart"
                          method="post"
                          class="cart-form">

                        <input type="hidden"
                               name="action"
                               value="add">

                        <input type="hidden"
                               name="productId"
                               value="<%= product.getProductId() %>">

                        <label for="variantId">Choose Size</label>

                        <select id="variantId"
                                name="variantId"
                                required>

                            <option value="">Select a size</option>

                            <% if (variants != null) {
                                for (ProductVariant variant : variants) { %>

                                    <option
                                        value="<%= variant.getVariantId() %>"
                                        <%= variant.getStockQuantity() <= 0
                                                ? "disabled" : "" %>>

                                        <%= variant.getSize() %>

                                        <%= variant.getStockQuantity() <= 0
                                                ? " (Out of stock)" : "" %>
                                    </option>

                            <%  }
                            } %>

                        </select>

                        <label for="quantity">Quantity</label>

                        <input
                            type="number"
                            id="quantity"
                            name="quantity"
                            value="1"
                            min="1"
                            max="100"
                            required>

                        <button type="submit"
                                class="add-to-cart-button">
                            Add to Cart
                        </button>

                    </form>

                    <a href="<%= contextPath %>/products"
                       class="back-link">
                        &larr; Back to Products
                    </a>

                </div>

            </section>

        <% } %>

    </main>

    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
