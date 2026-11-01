
package com.fintrack.dao;

import com.fintrack.model.Transaction;
import com.fintrack.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean addTransaction(Transaction transaction)
            throws SQLException {

        String sql = "INSERT INTO transactions "
                + "(user_id, category_id, type, amount, description, transaction_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, transaction.getUserId());
            ps.setInt(2, transaction.getCategoryId());
            ps.setString(3, transaction.getType());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setString(5, transaction.getDescription());
            ps.setDate(6, java.sql.Date.valueOf(
                    transaction.getTransactionDate()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        transaction.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    public List<Transaction> getTransactionsByUserId(int userId)
            throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT t.id, t.user_id, t.category_id, c.name AS category_name, "
                + "t.type, t.amount, t.description, t.transaction_date "
                + "FROM transactions t "
                + "LEFT JOIN categories c ON t.category_id = c.id "
                + "WHERE t.user_id = ? "
                + "ORDER BY t.transaction_date DESC, t.id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();

                    transaction.setId(rs.getInt("id"));
                    transaction.setUserId(rs.getInt("user_id"));
                    transaction.setCategoryId(rs.getInt("category_id"));
                    transaction.setCategoryName(rs.getString("category_name"));
                    transaction.setType(rs.getString("type"));
                    transaction.setAmount(rs.getBigDecimal("amount"));
                    transaction.setDescription(rs.getString("description"));
                    transaction.setTransactionDate(
                            rs.getDate("transaction_date").toLocalDate());

                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }

    public boolean updateTransaction(Transaction transaction)
            throws SQLException {

        String sql = "UPDATE transactions SET category_id = ?, "
                + "type = ?, amount = ?, description = ?, "
                + "transaction_date = ? "
                + "WHERE id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, transaction.getCategoryId());
            ps.setString(2, transaction.getType());
            ps.setBigDecimal(3, transaction.getAmount());
            ps.setString(4, transaction.getDescription());
            ps.setDate(5, java.sql.Date.valueOf(
                    transaction.getTransactionDate()));
            ps.setInt(6, transaction.getId());
            ps.setInt(7, transaction.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTransaction(int transactionId, int userId)
            throws SQLException {

        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, transactionId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    public BigDecimal getTotalByType(int userId, String type) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, type);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getMonthlyTotalByType(int userId, YearMonth month, String type)
            throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions "
                + "WHERE user_id = ? AND type = ? AND transaction_date BETWEEN ? AND ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setDate(3, java.sql.Date.valueOf(month.atDay(1)));
            ps.setDate(4, java.sql.Date.valueOf(month.atEndOfMonth()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }
}
