
package com.fintrack.dao;

import com.fintrack.model.Budget;
import com.fintrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public boolean addBudget(Budget budget) throws SQLException {

        String sql = "INSERT INTO budgets "
                + "(user_id, category_id, amount, budget_month) "
                + "VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, budget.getUserId());
            ps.setInt(2, budget.getCategoryId());
            ps.setBigDecimal(3, budget.getAmount());
            ps.setDate(4, java.sql.Date.valueOf(
                    budget.getBudgetMonth()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                String lookup = "SELECT id FROM budgets "
                        + "WHERE user_id = ? AND category_id = ? "
                        + "AND budget_month = ?";

                try (PreparedStatement find = con.prepareStatement(lookup)) {
                    find.setInt(1, budget.getUserId());
                    find.setInt(2, budget.getCategoryId());
                    find.setDate(3, java.sql.Date.valueOf(
                            budget.getBudgetMonth()));

                    try (ResultSet rs = find.executeQuery()) {
                        if (rs.next()) {
                            budget.setId(rs.getInt("id"));
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    public List<Budget> getBudgetsByUserId(int userId)
            throws SQLException {

        List<Budget> budgets = new ArrayList<>();

        String sql = "SELECT id, user_id, category_id, amount, budget_month "
                + "FROM budgets WHERE user_id = ? "
                + "ORDER BY budget_month DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Budget budget = new Budget();

                    budget.setId(rs.getInt("id"));
                    budget.setUserId(rs.getInt("user_id"));
                    budget.setCategoryId(rs.getInt("category_id"));
                    budget.setAmount(rs.getBigDecimal("amount"));
                    budget.setBudgetMonth(
                            rs.getDate("budget_month").toLocalDate());

                    budgets.add(budget);
                }
            }
        }

        return budgets;
    }

    public boolean deleteBudget(int budgetId, int userId)
            throws SQLException {

        String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, budgetId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }
}
