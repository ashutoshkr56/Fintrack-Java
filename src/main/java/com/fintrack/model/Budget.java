
package com.fintrack.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Budget {

    private int id;
    private int userId;
    private int categoryId;
    private String categoryName;
    private BigDecimal amount;
    private LocalDate budgetMonth;

    public Budget() {
    }

    public Budget(int userId, int categoryId, BigDecimal amount,
                  LocalDate budgetMonth) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.budgetMonth = budgetMonth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(LocalDate budgetMonth) {
        this.budgetMonth = budgetMonth;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
