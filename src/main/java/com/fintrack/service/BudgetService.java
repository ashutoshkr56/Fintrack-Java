
package com.fintrack.service;

import com.fintrack.dao.BudgetDAO;
import com.fintrack.model.Budget;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BudgetService {

    private final BudgetDAO budgetDAO;

    public BudgetService() {
        budgetDAO = new BudgetDAO();
    }

    public boolean saveBudget(Budget budget) throws SQLException {

        if (budget.getAmount() == null
                || budget.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Budget amount must be greater than zero");
        }

        if (budget.getBudgetMonth() == null) {
            throw new IllegalArgumentException(
                    "Budget month is required");
        }

        LocalDate month = budget.getBudgetMonth()
                .withDayOfMonth(1);

        budget.setBudgetMonth(month);

        return budgetDAO.addBudget(budget);
    }

    public List<Budget> getBudgets(int userId) throws SQLException {
        return budgetDAO.getBudgetsByUserId(userId);
    }

    public boolean deleteBudget(int budgetId, int userId)
            throws SQLException {
        return budgetDAO.deleteBudget(budgetId, userId);
    }
}
