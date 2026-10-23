
package com.fashionstore.dao;

import com.fashionstore.model.Product;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {

    // Add, update and delete products
    boolean addProduct(Product product) throws SQLException;

    boolean updateProduct(Product product) throws SQLException;

    boolean deleteProduct(int productId) throws SQLException;

    // Retrieve products
    Product getProductById(int productId) throws SQLException;

    List<Product> getAllProducts() throws SQLException;

    List<Product> getAllActiveProducts() throws SQLException;

    List<Product> getProductsByCategory(int categoryId)
            throws SQLException;

    // Search products
    List<Product> searchProducts(String keyword) throws SQLException;

    // Filter by price
    List<Product> getProductsByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice)
            throws SQLException;

    // Filter by category and price
    List<Product> getProductsByCategoryAndPriceRange(
            int categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice) throws SQLException;

    // Sort products
    List<Product> getSortedProducts(
            String sortBy, String sortOrder) throws SQLException;

    List<Product> getProductsSortedByPriceAsc() throws SQLException;

    List<Product> getProductsSortedByPriceDesc() throws SQLException;

    // Pagination
    List<Product> getProductsByPage(int pageNumber, int pageSize)
            throws SQLException;

    // Home page
    List<Product> getLatestProducts(int limit) throws SQLException;

    // Combined search, filters, sorting and pagination
    List<Product> getFilteredProducts(
            String keyword,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String brand,
            String color,
            String size,
            String sortBy,
            String sortOrder,
            int pageNumber,
            int pageSize) throws SQLException;

    // Count results matching the selected filters
    int countFilteredProducts(
            String keyword,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String brand,
            String color,
            String size) throws SQLException;

    // Related products
    List<Product> getRelatedProducts(
            int categoryId,
            int excludeProductId,
            int limit) throws SQLException;
}
