
package com.fashionstore.dao.impl;

import com.fashionstore.dao.CategoryDAO;
import com.fashionstore.model.Category;
import com.fashionstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {

    private static final String INSERT_CATEGORY_SQL =
            "INSERT INTO categories (category_name, description) VALUES (?, ?)";

    private static final String UPDATE_CATEGORY_SQL =
            "UPDATE categories SET category_name = ?, description = ? "
            + "WHERE category_id = ?";

    private static final String DELETE_CATEGORY_SQL =
            "DELETE FROM categories WHERE category_id = ?";

    private static final String GET_CATEGORY_BY_ID_SQL =
            "SELECT * FROM categories WHERE category_id = ?";

    private static final String GET_CATEGORY_BY_NAME_SQL =
            "SELECT * FROM categories WHERE category_name = ?";

    private static final String GET_ALL_CATEGORIES_SQL =
            "SELECT * FROM categories ORDER BY category_id";

    @Override
    public boolean addCategory(Category category) throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     INSERT_CATEGORY_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        category.setCategoryId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateCategory(Category category) throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(UPDATE_CATEGORY_SQL)) {

            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteCategory(int categoryId) throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(DELETE_CATEGORY_SQL)) {

            ps.setInt(1, categoryId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Category getCategoryById(int categoryId) throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_CATEGORY_BY_ID_SQL)) {

            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public Category getCategoryByName(String categoryName)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_CATEGORY_BY_NAME_SQL)) {

            ps.setString(1, categoryName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Category> getAllCategories() throws SQLException {

        List<Category> categories = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(GET_ALL_CATEGORIES_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        }

        return categories;
    }

    private Category mapCategory(ResultSet rs) throws SQLException {

        Category category = new Category();

        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));

        return category;
    }
}
