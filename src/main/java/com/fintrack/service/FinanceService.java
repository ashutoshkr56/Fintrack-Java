
package com.fintrack.service;

import com.fintrack.dao.TransactionDAO;

import java.math.BigDecimal;
import java.sql.SQLException;

public class FinanceService {

    private final TransactionDAO transactionDAO;

    public FinanceService() {
        this.transactionDAO = new TransactionDAO();
    }

    public BigDecimal getTotalIncome(int userId) throws SQLException {
        return transactionDAO.getTotalByType(userId, "INCOME");
    }

    public BigDecimal getTotalExpenses(int userId) throws SQLException {
        return transactionDAO.getTotalByType(userId, "EXPENSE");
    }

    public BigDecimal getBalance(BigDecimal income, BigDecimal expenses) {
        if (income == null) income = BigDecimal.ZERO;
        if (expenses == null) expenses = BigDecimal.ZERO;
        return income.subtract(expenses);
    }

    public BigDecimal getBalance(int userId) throws SQLException {
        return getBalance(getTotalIncome(userId), getTotalExpenses(userId));
    }

    public BigDecimal getSavingsRate(BigDecimal income, BigDecimal balance) {
        if (income == null || income.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        return balance.multiply(BigDecimal.valueOf(100))
                .divide(income, 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getSavingsRate(int userId) throws SQLException {
        BigDecimal income = getTotalIncome(userId);
        BigDecimal expenses = getTotalExpenses(userId);
        BigDecimal balance = getBalance(income, expenses);
        return getSavingsRate(income, balance);
    }
}
