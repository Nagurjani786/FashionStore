
package com.fashionstore.model;

public class ProductVariant {

    private int variantId;
    private int productId;
    private String size;
    private int stockQuantity;

    // Default constructor
    public ProductVariant() {
    }

    // Parameterized constructor
    public ProductVariant(int variantId, int productId,
                          String size, int stockQuantity) {
        this.variantId = variantId;
        this.productId = productId;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
