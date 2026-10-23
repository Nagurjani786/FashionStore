
package com.fashionstore.dao.impl;

import com.fashionstore.dao.ProductVariantDAO;
import com.fashionstore.model.ProductVariant;
import com.fashionstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductVariantDAOImpl implements ProductVariantDAO {

    private static final String INSERT_VARIANT_SQL =
        "INSERT INTO product_variants (product_id, size, stock_quantity) "
        + "VALUES (?, ?, ?)";

    private static final String UPDATE_VARIANT_SQL =
        "UPDATE product_variants SET product_id = ?, size = ?, "
        + "stock_quantity = ? WHERE variant_id = ?";

    private static final String DELETE_VARIANT_SQL =
        "DELETE FROM product_variants WHERE variant_id = ?";

    private static final String GET_VARIANT_BY_ID_SQL =
        "SELECT * FROM product_variants WHERE variant_id = ?";

    private static final String GET_VARIANT_BY_PRODUCT_AND_SIZE_SQL =
        "SELECT * FROM product_variants WHERE product_id = ? AND size = ?";

    private static final String GET_VARIANTS_BY_PRODUCT_SQL =
        "SELECT * FROM product_variants WHERE product_id = ? ORDER BY size";

    private static final String GET_ALL_VARIANTS_SQL =
        "SELECT * FROM product_variants ORDER BY variant_id";

    private static final String UPDATE_STOCK_SQL =
        "UPDATE product_variants SET stock_quantity = ? WHERE variant_id = ?";

    private static final String CHECK_STOCK_SQL =
        "SELECT stock_quantity FROM product_variants WHERE variant_id = ?";

    @Override
    public boolean addProductVariant(ProductVariant productVariant)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     INSERT_VARIANT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, productVariant.getProductId());
            ps.setString(2, productVariant.getSize());
            ps.setInt(3, productVariant.getStockQuantity());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        productVariant.setVariantId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateProductVariant(ProductVariant productVariant)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(UPDATE_VARIANT_SQL)) {

            ps.setInt(1, productVariant.getProductId());
            ps.setString(2, productVariant.getSize());
            ps.setInt(3, productVariant.getStockQuantity());
            ps.setInt(4, productVariant.getVariantId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteProductVariant(int variantId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(DELETE_VARIANT_SQL)) {

            ps.setInt(1, variantId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ProductVariant getVariantById(int variantId)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_VARIANT_BY_ID_SQL)) {

            ps.setInt(1, variantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVariant(rs);
                }
            }
        }

        return null;
    }

    @Override
    public ProductVariant getVariantByProductIdAndSize(
            int productId, String size) throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(
                             GET_VARIANT_BY_PRODUCT_AND_SIZE_SQL)) {

            ps.setInt(1, productId);
            ps.setString(2, size);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVariant(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<ProductVariant> getVariantsByProductId(int productId)
            throws SQLException {

        List<ProductVariant> variants = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_VARIANTS_BY_PRODUCT_SQL)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    variants.add(mapVariant(rs));
                }
            }
        }

        return variants;
    }

    @Override
    public List<ProductVariant> getAllVariants() throws SQLException {
        List<ProductVariant> variants = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_ALL_VARIANTS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                variants.add(mapVariant(rs));
            }
        }

        return variants;
    }

    @Override
    public boolean updateStock(int variantId, int stockQuantity)
            throws SQLException {

        if (stockQuantity < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity cannot be negative.");
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(UPDATE_STOCK_SQL)) {

            ps.setInt(1, stockQuantity);
            ps.setInt(2, variantId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean isStockAvailable(int variantId, int requiredQuantity)
            throws SQLException {

        if (requiredQuantity <= 0) {
            return false;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(CHECK_STOCK_SQL)) {

            ps.setInt(1, variantId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        && rs.getInt("stock_quantity") >= requiredQuantity;
            }
        }
    }

    private ProductVariant mapVariant(ResultSet rs) throws SQLException {
        ProductVariant variant = new ProductVariant();

        variant.setVariantId(rs.getInt("variant_id"));
        variant.setProductId(rs.getInt("product_id"));
        variant.setSize(rs.getString("size"));
        variant.setStockQuantity(rs.getInt("stock_quantity"));

        return variant;
    }
}
