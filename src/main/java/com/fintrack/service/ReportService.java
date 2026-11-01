
package com.fintrack.service;

import com.fintrack.dao.TransactionDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;

public class ReportService {

    private final TransactionDAO transactionDAO;

    public ReportService() {
        this.transactionDAO = new TransactionDAO();
    }

    public BigDecimal getMonthlyIncome(int userId, YearMonth month)
            throws SQLException {
        return transactionDAO.getMonthlyTotalByType(userId, month, "INCOME");
    }

    public BigDecimal getMonthlyExpenses(int userId, YearMonth month)
            throws SQLException {
        return transactionDAO.getMonthlyTotalByType(userId, month, "EXPENSE");
    }

    public BigDecimal getMonthlyBalance(BigDecimal income, BigDecimal expenses) {
        if (income == null) income = BigDecimal.ZERO;
        if (expenses == null) expenses = BigDecimal.ZERO;
        return income.subtract(expenses);
    }

    public BigDecimal getMonthlyBalance(int userId, YearMonth month)
            throws SQLException {
        return getMonthlyBalance(
                getMonthlyIncome(userId, month),
                getMonthlyExpenses(userId, month)
        );
    }
}
