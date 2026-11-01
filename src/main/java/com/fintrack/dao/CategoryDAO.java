
package com.fintrack.dao;

import com.fintrack.model.Category;
import com.fintrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public boolean addCategory(Category category) throws SQLException {

        String sql = "INSERT INTO categories (user_id, name, type) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            if (category.getUserId() == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, category.getUserId());
            }

            ps.setString(2, category.getName());
            ps.setString(3, category.getType());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        category.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    public List<Category> getCategoriesByUserId(int userId)
            throws SQLException {

        List<Category> categories = new ArrayList<>();

        String sql = "SELECT id, user_id, name, type FROM categories "
                + "WHERE user_id = ? OR user_id IS NULL "
                + "ORDER BY name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();

                    category.setId(rs.getInt("id"));

                    int categoryUserId = rs.getInt("user_id");
                    category.setUserId(
                            rs.wasNull() ? null : categoryUserId);

                    category.setName(rs.getString("name"));
                    category.setType(rs.getString("type"));

                    categories.add(category);
                }
            }
        }

        return categories;
    }

    public boolean deleteCategory(int categoryId, int userId)
            throws SQLException {

        String sql = "DELETE FROM categories "
                + "WHERE id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }
}
