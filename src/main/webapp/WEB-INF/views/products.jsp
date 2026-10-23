
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fashionstore.model.Product" %>
<%@ page import="com.fashionstore.model.Category" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>All Products | FashionStore</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/products.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="products-container">

        <section class="products-heading">
            <h1>Explore Our Fashion Collection</h1>
            <p>Browse products, apply filters, and discover styles that match your taste.</p>
        </section>

        <%
            List<Product> products =
                (List<Product>) request.getAttribute("products");

            List<Category> categories =
                (List<Category>) request.getAttribute("categories");

            String keyword =
                (String) request.getAttribute("keyword");

            Integer selectedCategory =
                (Integer) request.getAttribute("selectedCategory");

            String minPrice =
                (String) request.getAttribute("minPrice");

            String maxPrice =
                (String) request.getAttribute("maxPrice");

            String sortBy =
                (String) request.getAttribute("sortBy");

            Object countObject =
                request.getAttribute("productCount");

            int productCount =
                countObject == null ? 0 : (Integer) countObject;
        %>

        <section class="products-content">

            <!-- FILTER SIDEBAR -->
            <aside class="products-sidebar">
                <h2>Filters</h2>

                <form action="${pageContext.request.contextPath}/products"
                      method="get">

                    <label for="keyword">Search</label>

                    <input type="text"
                           id="keyword"
                           name="keyword"
                           placeholder="Search products..."
                           value="<%= keyword == null ? "" : keyword %>">

                    <label for="categoryId">Category</label>

                    <select id="categoryId" name="categoryId">
                        <option value="">All Categories</option>

                        <% if (categories != null) {
                            for (Category category : categories) {
                                boolean selected =
                                    selectedCategory != null
                                    && selectedCategory.intValue()
                                       == category.getCategoryId();
                        %>

                            <option value="<%= category.getCategoryId() %>"
                                <%= selected ? "selected" : "" %>>
                                <%= category.getCategoryName() %>
                            </option>

                        <%  }
                           }
                        %>
                    </select>

                    <div class="price-fields">
                        <div>
                            <label for="minPrice">Min Price</label>

                            <input type="number"
                                   id="minPrice"
                                   name="minPrice"
                                   min="0"
                                   step="0.01"
                                   placeholder="0"
                                   value="<%= minPrice == null ? "" : minPrice %>">
                        </div>

                        <div>
                            <label for="maxPrice">Max Price</label>

                            <input type="number"
                                   id="maxPrice"
                                   name="maxPrice"
                                   min="0"
                                   step="0.01"
                                   placeholder="5000"
                                   value="<%= maxPrice == null ? "" : maxPrice %>">
                        </div>
                    </div>

                    <label for="sortBy">Sort By</label>

                    <select id="sortBy" name="sortBy">
                        <option value="default"
                            <%= "default".equals(sortBy) ? "selected" : "" %>>
                            Default
                        </option>

                        <option value="price"
                            <%= "price".equals(sortBy) ? "selected" : "" %>>
                            Price: Low to High
                        </option>

                        <option value="name"
                            <%= "name".equals(sortBy) ? "selected" : "" %>>
                            Name
                        </option>

                        <option value="date"
                            <%= "date".equals(sortBy) ? "selected" : "" %>>
                            Newest
                        </option>
                    </select>

                    <div class="filter-actions">
                        <button type="submit" class="filter-button">
                            Apply Filters
                        </button>

                        <a class="clear-button"
                           href="${pageContext.request.contextPath}/products">
                            Clear
                        </a>
                    </div>

                </form>
            </aside>

            <!-- PRODUCTS SECTION -->
            <section class="products-main">

                <div class="products-title-row">
                    <div>
                        <h2>Products</h2>

                        <% if (keyword != null && !keyword.isEmpty()) { %>
                            <p class="search-results">
                                Search results for:
                                <strong><%= keyword %></strong>
                            </p>
                        <% } %>
                    </div>

                    <span class="product-count">
                        <%= productCount %> item(s) found
                    </span>
                </div>

                <% if (request.getAttribute("filterError") != null) { %>
                    <p class="filter-error">
                        <%= request.getAttribute("filterError") %>
                    </p>
                <% } %>

                <% if (products == null || products.isEmpty()) { %>

                    <div class="empty-message">
                        <h2>No products found</h2>

                        <p>
                            Try changing your search or filters.
                        </p>

                        <a href="<%= request.getContextPath() %>/products">
                            Clear Filters
                        </a>
                    </div>

                <% } else { %>

                    <div class="products-grid">

                        <% for (Product product : products) { %>

                            <article class="product-card">

                                <a class="product-image-link"
                                   href="<%= request.getContextPath() %>/product?id=<%= product.getProductId() %>">

                                    <% if (product.getImageUrl() != null
                                            && !product.getImageUrl().trim().isEmpty()) { %>

                                        <img
                                            class="product-image"
                                            src="<%= product.getImageUrl() %>"
                                            alt="<%= product.getProductName() %>"
                                            loading="lazy">

                                    <% } else { %>

                                        <div class="no-image">
                                            No Image Available
                                        </div>

                                    <% } %>

                                </a>

                                <div class="product-info">

                                    <p class="product-brand">
                                        <%= product.getBrand() == null
                                                ? "" : product.getBrand() %>
                                    </p>

                                    <h3>
                                        <a href="<%= request.getContextPath() %>/product?id=<%= product.getProductId() %>">
                                            <%= product.getProductName() %>
                                        </a>
                                    </h3>

                                    <p class="product-price">
                                        ₹<%= product.getPrice() %>
                                    </p>

                                    <a class="view-button"
                                       href="<%= request.getContextPath() %>/product?id=<%= product.getProductId() %>">
                                        View Product
                                    </a>

                                </div>
                            </article>

                        <% } %>

                    </div>

                <% } %>

            </section>
        </section>
    </main>

    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
