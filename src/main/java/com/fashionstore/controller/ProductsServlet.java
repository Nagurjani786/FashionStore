
package com.fashionstore.controller;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.CategoryDAO;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.dao.impl.CategoryDAOImpl;
import com.fashionstore.model.Product;
import com.fashionstore.model.Category;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/products")
public class ProductsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAOImpl();
        categoryDAO = new CategoryDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String keyword = request.getParameter("keyword");
        String categoryParam = request.getParameter("categoryId");
        String minParam = request.getParameter("minPrice");
        String maxParam = request.getParameter("maxPrice");
        String sortBy = request.getParameter("sortBy");

        if (keyword != null) {
            keyword = keyword.trim();
        }

        if (keyword != null && keyword.isEmpty()) {
            keyword = null;
        }

        Integer categoryId = parseInteger(categoryParam);
        BigDecimal minPrice = parsePrice(minParam);
        BigDecimal maxPrice = parsePrice(maxParam);

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "default";
        }

        // Prevent an invalid price range from returning misleading results.
        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            request.setAttribute(
                    "filterError",
                    "Minimum price cannot be greater than maximum price."
            );
            minPrice = null;
            maxPrice = null;
        }

        try {
            List<Category> categories = categoryDAO.getAllCategories();

            List<Product> products = productDAO.getFilteredProducts(
                    keyword,
                    categoryId,
                    minPrice,
                    maxPrice,
                    null,       // Brand filter
                    null,       // Color filter
                    null,       // Size filter
                    sortBy,
                    "ASC",
                    1,
                    100
            );

            int productCount = productDAO.countFilteredProducts(
                    keyword,
                    categoryId,
                    minPrice,
                    maxPrice,
                    null,
                    null,
                    null
            );

            request.setAttribute("products", products);
            request.setAttribute("productCount", productCount);
            request.setAttribute("categories", categories);
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedCategory", categoryId);
            request.setAttribute("minPrice", minParam);
            request.setAttribute("maxPrice", maxParam);
            request.setAttribute("sortBy", sortBy);

            request.getRequestDispatcher(
                    "/WEB-INF/views/products.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to load products and categories.", e);
        }
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isBlank()) {
                return null;
            }

            int number = Integer.parseInt(value);
            return number > 0 ? number : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parsePrice(String value) {
        try {
            if (value == null || value.isBlank()) {
                return null;
            }

            BigDecimal price = new BigDecimal(value);

            return price.signum() >= 0 ? price : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
