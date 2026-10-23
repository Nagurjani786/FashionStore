
package com.fashionstore.controller;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.ProductVariantDAO;
import com.fashionstore.dao.impl.ProductDAOImpl;
import com.fashionstore.dao.impl.ProductVariantDAOImpl;
import com.fashionstore.model.Product;
import com.fashionstore.model.ProductVariant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/product")
public class ProductDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private ProductVariantDAO productVariantDAO;

    @Override
    public void init() {
        productDAO = new ProductDAOImpl();
        productVariantDAO = new ProductVariantDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");

        if (id == null || id.trim().isEmpty()) {
            response.sendRedirect(
                request.getContextPath() + "/products"
            );
            return;
        }

        try {
            int productId = Integer.parseInt(id);

            Product product = productDAO.getProductById(productId);

            if (product == null) {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Product not found"
                );
                return;
            }

            List<ProductVariant> variants =
                productVariantDAO.getVariantsByProductId(productId);

            request.setAttribute("product", product);
            request.setAttribute("variants", variants);

            request.getRequestDispatcher(
                "/WEB-INF/views/product-details.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid product ID"
            );
        } catch (Exception e) {
            throw new ServletException(
                "Error loading product details", e
            );
        }
    }
}
