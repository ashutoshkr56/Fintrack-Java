
package com.fintrack.service;

import com.fintrack.dao.CategoryDAO;
import com.fintrack.model.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        categoryDAO = new CategoryDAO();
    }

    public boolean addCategory(Category category) throws SQLException {

        if (category.getName() == null
                || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Category name cannot be empty");
        }

        if (!"INCOME".equals(category.getType())
                && !"EXPENSE".equals(category.getType())) {
            throw new IllegalArgumentException(
                    "Category type must be INCOME or EXPENSE");
        }

        category.setName(category.getName().trim());

        return categoryDAO.addCategory(category);
    }

    public List<Category> getCategories(int userId)
            throws SQLException {

        return categoryDAO.getCategoriesByUserId(userId);
    }

    public boolean deleteCategory(int categoryId, int userId)
            throws SQLException {

        return categoryDAO.deleteCategory(categoryId, userId);
    }
}
