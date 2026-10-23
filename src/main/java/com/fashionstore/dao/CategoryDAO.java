
package com.fashionstore.dao;

import com.fashionstore.model.Category;
import java.sql.SQLException;
import java.util.List;

public interface CategoryDAO {

    boolean addCategory(Category category) throws SQLException;

    boolean updateCategory(Category category) throws SQLException;

    boolean deleteCategory(int categoryId) throws SQLException;

    Category getCategoryById(int categoryId) throws SQLException;

    Category getCategoryByName(String categoryName)
            throws SQLException;

    List<Category> getAllCategories() throws SQLException;
}
