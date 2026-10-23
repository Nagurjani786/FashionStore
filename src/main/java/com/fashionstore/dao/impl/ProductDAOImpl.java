
package com.fashionstore.dao.impl;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.model.Product;
import com.fashionstore.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    private static final String INSERT_PRODUCT_SQL =
        "INSERT INTO products "
        + "(category_id, product_name, description, brand, color, price, image_url) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_PRODUCT_SQL =
        "UPDATE products SET category_id = ?, product_name = ?, "
        + "description = ?, brand = ?, color = ?, price = ?, image_url = ? "
        + "WHERE product_id = ?";

    private static final String DELETE_PRODUCT_SQL =
        "DELETE FROM products WHERE product_id = ?";

    private static final String GET_PRODUCT_BY_ID_SQL =
        "SELECT * FROM products WHERE product_id = ?";

    private static final String GET_ALL_PRODUCTS_SQL =
        "SELECT * FROM products ORDER BY product_id";

    private static final String GET_PRODUCTS_BY_CATEGORY_SQL =
        "SELECT * FROM products WHERE category_id = ? ORDER BY product_id";

    private static final String SEARCH_PRODUCTS_SQL =
        "SELECT * FROM products "
        + "WHERE product_name LIKE ? OR brand LIKE ? OR description LIKE ? "
        + "ORDER BY product_name";

    private static final String GET_PRODUCTS_BY_PRICE_SQL =
        "SELECT * FROM products WHERE price BETWEEN ? AND ? ORDER BY price";

    private static final String GET_PRODUCTS_BY_CATEGORY_PRICE_SQL =
        "SELECT * FROM products WHERE category_id = ? "
        + "AND price BETWEEN ? AND ? ORDER BY price";

    private static final String GET_LATEST_PRODUCTS_SQL =
        "SELECT * FROM products ORDER BY created_at DESC LIMIT ?";

    private static final String GET_RELATED_PRODUCTS_SQL =
        "SELECT * FROM products WHERE category_id = ? AND product_id <> ? "
        + "ORDER BY product_id DESC LIMIT ?";

    @Override
    public boolean addProduct(Product product) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     INSERT_PRODUCT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setProductParameters(ps, product);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        product.setProductId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateProduct(Product product) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(UPDATE_PRODUCT_SQL)) {

            setProductParameters(ps, product);
            ps.setInt(8, product.getProductId());

            return ps.executeUpdate() > 0;
        }
    }

    private void setProductParameters(
            PreparedStatement ps, Product product) throws SQLException {

        ps.setInt(1, product.getCategoryId());
        ps.setString(2, product.getProductName());
        ps.setString(3, product.getDescription());
        ps.setString(4, product.getBrand());
        ps.setString(5, product.getColor());
        ps.setBigDecimal(6, BigDecimal.valueOf(product.getPrice()));
        ps.setString(7, product.getImageUrl());
    }

    @Override
    public boolean deleteProduct(int productId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(DELETE_PRODUCT_SQL)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Product getProductById(int productId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_PRODUCT_BY_ID_SQL)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        return executeProductQuery(GET_ALL_PRODUCTS_SQL);
    }

    @Override
    public List<Product> getAllActiveProducts() throws SQLException {
        // This schema has no active/status column.
        return getAllProducts();
    }

    @Override
    public List<Product> getProductsByCategory(int categoryId)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_PRODUCTS_BY_CATEGORY_SQL)) {

            ps.setInt(1, categoryId);
            return extractProducts(ps);
        }
    }

    @Override
    public List<Product> searchProducts(String keyword)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(SEARCH_PRODUCTS_SQL)) {

            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            return extractProducts(ps);
        }
    }

    @Override
    public List<Product> getProductsByPriceRange(
            BigDecimal minPrice, BigDecimal maxPrice) throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_PRODUCTS_BY_PRICE_SQL)) {

            ps.setBigDecimal(1, minPrice);
            ps.setBigDecimal(2, maxPrice);

            return extractProducts(ps);
        }
    }

    @Override
    public List<Product> getProductsByCategoryAndPriceRange(
            int categoryId, BigDecimal minPrice, BigDecimal maxPrice)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_PRODUCTS_BY_CATEGORY_PRICE_SQL)) {

            ps.setInt(1, categoryId);
            ps.setBigDecimal(2, minPrice);
            ps.setBigDecimal(3, maxPrice);

            return extractProducts(ps);
        }
    }

    @Override
    public List<Product> getSortedProducts(
            String sortBy, String sortOrder) throws SQLException {

        String column;

        switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "price":
                column = "price";
                break;
            case "name":
            case "product_name":
                column = "product_name";
                break;
            case "date":
            case "created_at":
                column = "created_at";
                break;
            default:
                column = "product_id";
        }

        String direction = "DESC".equalsIgnoreCase(sortOrder)
                ? "DESC" : "ASC";

        return executeProductQuery(
                "SELECT * FROM products ORDER BY " + column + " " + direction);
    }

    @Override
    public List<Product> getProductsSortedByPriceAsc() throws SQLException {
        return executeProductQuery(
                "SELECT * FROM products ORDER BY price ASC");
    }

    @Override
    public List<Product> getProductsSortedByPriceDesc() throws SQLException {
        return executeProductQuery(
                "SELECT * FROM products ORDER BY price DESC");
    }

    @Override
    public List<Product> getProductsByPage(int pageNumber, int pageSize)
            throws SQLException {

        validatePagination(pageNumber, pageSize);

        String sql = "SELECT * FROM products ORDER BY product_id LIMIT ? OFFSET ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pageSize);
            ps.setInt(2, (pageNumber - 1) * pageSize);

            return extractProducts(ps);
        }
    }

    @Override
    public List<Product> getLatestProducts(int limit) throws SQLException {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1.");
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_LATEST_PRODUCTS_SQL)) {

            ps.setInt(1, limit);
            return extractProducts(ps);
        }
    }

    @Override
    public List<Product> getFilteredProducts(
            String keyword, Integer categoryId,
            BigDecimal minPrice, BigDecimal maxPrice,
            String brand, String color, String size,
            String sortBy, String sortOrder,
            int pageNumber, int pageSize) throws SQLException {

        validatePagination(pageNumber, pageSize);

        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT p.* FROM products p ");

        if (hasText(size)) {
            sql.append("JOIN product_variants v ON p.product_id = v.product_id ");
        }

        sql.append("WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();

        if (hasText(keyword)) {
            sql.append("AND (p.product_name LIKE ? OR p.brand LIKE ? "
                    + "OR p.description LIKE ?) ");
            String search = "%" + keyword.trim() + "%";
            parameters.add(search);
            parameters.add(search);
            parameters.add(search);
        }

        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            parameters.add(categoryId);
        }

        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            parameters.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            parameters.add(maxPrice);
        }

        if (hasText(brand)) {
            sql.append("AND p.brand = ? ");
            parameters.add(brand.trim());
        }

        if (hasText(color)) {
            sql.append("AND p.color = ? ");
            parameters.add(color.trim());
        }

        if (hasText(size)) {
            sql.append("AND v.size = ? AND v.stock_quantity > 0 ");
            parameters.add(size.trim());
        }

        String sortColumn;

        switch (sortBy == null ? "" : sortBy.toLowerCase()) {
            case "price":
                sortColumn = "p.price";
                break;
            case "name":
            case "product_name":
                sortColumn = "p.product_name";
                break;
            case "created_at":
            case "date":
                sortColumn = "p.created_at";
                break;
            default:
                sortColumn = "p.product_id";
        }

        String direction = "DESC".equalsIgnoreCase(sortOrder)
                ? "DESC" : "ASC";

        sql.append("ORDER BY ").append(sortColumn).append(" ")
           .append(direction).append(" LIMIT ? OFFSET ?");

        parameters.add(pageSize);
        parameters.add((pageNumber - 1) * pageSize);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            setParameters(ps, parameters);
            return extractProducts(ps);
        }
    }

    @Override
    public int countFilteredProducts(
            String keyword, Integer categoryId,
            BigDecimal minPrice, BigDecimal maxPrice,
            String brand, String color, String size) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT p.product_id) FROM products p ");

        if (hasText(size)) {
            sql.append("JOIN product_variants v ON p.product_id = v.product_id ");
        }

        sql.append("WHERE 1 = 1 ");
        List<Object> parameters = new ArrayList<>();

        if (hasText(keyword)) {
            sql.append("AND (p.product_name LIKE ? OR p.brand LIKE ? "
                    + "OR p.description LIKE ?) ");
            String search = "%" + keyword.trim() + "%";
            parameters.add(search);
            parameters.add(search);
            parameters.add(search);
        }

        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            parameters.add(categoryId);
        }

        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            parameters.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            parameters.add(maxPrice);
        }

        if (hasText(brand)) {
            sql.append("AND p.brand = ? ");
            parameters.add(brand.trim());
        }

        if (hasText(color)) {
            sql.append("AND p.color = ? ");
            parameters.add(color.trim());
        }

        if (hasText(size)) {
            sql.append("AND v.size = ? AND v.stock_quantity > 0 ");
            parameters.add(size.trim());
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            setParameters(ps, parameters);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    @Override
    public List<Product> getRelatedProducts(
            int categoryId, int excludeProductId, int limit)
            throws SQLException {

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1.");
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_RELATED_PRODUCTS_SQL)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, excludeProductId);
            ps.setInt(3, limit);

            return extractProducts(ps);
        }
    }

    private List<Product> executeProductQuery(String sql)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            return extractProducts(ps);
        }
    }

    private List<Product> extractProducts(PreparedStatement ps)
            throws SQLException {

        List<Product> products = new ArrayList<>();

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        }

        return products;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product product = new Product();

        product.setProductId(rs.getInt("product_id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setProductName(rs.getString("product_name"));
        product.setDescription(rs.getString("description"));
        product.setBrand(rs.getString("brand"));
        product.setColor(rs.getString("color"));
        product.setPrice(rs.getDouble("price"));
        product.setImageUrl(rs.getString("image_url"));
        product.setCreatedAt(rs.getTimestamp("created_at"));

        return product;
    }

    private void setParameters(
            PreparedStatement ps, List<Object> parameters)
            throws SQLException {

        for (int i = 0; i < parameters.size(); i++) {
            Object value = parameters.get(i);

            if (value instanceof String) {
                ps.setString(i + 1, (String) value);
            } else if (value instanceof Integer) {
                ps.setInt(i + 1, (Integer) value);
            } else if (value instanceof BigDecimal) {
                ps.setBigDecimal(i + 1, (BigDecimal) value);
            }
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void validatePagination(int pageNumber, int pageSize) {
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException(
                    "Page number and page size must be at least 1.");
        }
    }
}
