
package com.fashionstore.dao;

import com.fashionstore.model.ProductVariant;
import java.sql.SQLException;
import java.util.List;

public interface ProductVariantDAO {

    boolean addProductVariant(ProductVariant productVariant)
            throws SQLException;

    boolean updateProductVariant(ProductVariant productVariant)
            throws SQLException;

    boolean deleteProductVariant(int variantId)
            throws SQLException;

    ProductVariant getVariantById(int variantId)
            throws SQLException;

    ProductVariant getVariantByProductIdAndSize(
            int productId, String size) throws SQLException;

    List<ProductVariant> getVariantsByProductId(int productId)
            throws SQLException;

    List<ProductVariant> getAllVariants() throws SQLException;

    boolean updateStock(int variantId, int stockQuantity)
            throws SQLException;

    boolean isStockAvailable(int variantId, int requiredQuantity)
            throws SQLException;
}
