
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.fintrack.model.Budget" %>
<%@ page import="com.fintrack.model.Category" %>

<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    if (request.getAttribute("budgets") == null) {
        response.sendRedirect("budgets");
        return;
    }

    List<Budget> budgets =
            (List<Budget>) request.getAttribute("budgets");

    List<Category> categories =
            (List<Category>) request.getAttribute("categories");

    String error = (String) request.getAttribute("error");

    DateTimeFormatter monthFormatter =
            DateTimeFormatter.ofPattern("MMMM yyyy");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Budgets - FinTrack</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar">
    <a class="brand" href="dashboard">FinTrack</a>
    <div class="nav-links">
        <a href="dashboard">Dashboard</a>
        <a href="transactions">Transactions</a>
        <a href="categories">Categories</a>
        <a href="budgets">Budgets</a>
        <a href="reports">Reports</a>
        <a class="logout" href="logout">Logout</a>
    </div>
</nav>

<main class="container">
    <header class="page-header">
        <h1>Manage Budgets</h1>
        <p>Set a monthly spending limit for an expense category.</p>
    </header>

    <% if (error != null) { %>
        <div class="alert error">
            <%= error %>
        </div>
    <% } %>

    <section class="panel">
        <h2>Add Budget</h2>

        <% if (categories == null || categories.isEmpty()) { %>
            <p style="color: #64748b; margin-bottom: 12px;">No expense categories are available.</p>
            <p style="color: #64748b; margin-bottom: 16px;">Add an expense category before creating a budget.</p>
            <a href="categories" class="btn primary">Manage Categories</a>
        <% } else { %>

            <form action="budgets" method="post" class="transaction-form">
                <input type="hidden" name="action" value="add">

                <div class="form-group">
                    <label for="categoryId">Expense Category</label>
                    <select id="categoryId" name="categoryId" required>
                        <option value="">Select a category</option>

                        <% for (Category category : categories) { %>
                            <option value="<%= category.getId() %>">
                                <%= category.getName() %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="amount">Monthly Budget Amount (₹)</label>
                    <input
                        type="number"
                        id="amount"
                        name="amount"
                        min="0.01"
                        step="0.01"
                        placeholder="Enter amount"
                        required
                    >
                </div>

                <div class="form-group">
                    <label for="budgetMonth">Budget Month</label>
                    <input
                        type="month"
                        id="budgetMonth"
                        name="budgetMonth"
                        required
                    >
                </div>

                <div class="full-width">
                    <button type="submit" class="btn primary">Save Budget</button>
                </div>
            </form>

        <% } %>
    </section>

    <section class="panel">
        <h2>Your Budgets</h2>

        <% if (budgets == null || budgets.isEmpty()) { %>
            <p class="empty-state">No budgets have been created yet.</p>
        <% } else { %>

            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Category</th>
                            <th>Monthly Limit</th>
                            <th>Month</th>
                            <th>Action</th>
                        </tr>
                    </thead>

                    <tbody>
                        <% for (Budget budget : budgets) { %>
                            <tr>
                                <td>
                                    <% 
                                        String categoryName = "Category " 
                                                + budget.getCategoryId();

                                        if (categories != null) {
                                            for (Category category : categories) {
                                                if (category.getId() == budget.getCategoryId()) {
                                                    categoryName = category.getName();
                                                    break;
                                                }
                                            }
                                        }
                                    %>
                                    <%= categoryName %>
                                </td>

                                <td>
                                    ₹<%= budget.getAmount() %>
                                </td>

                                <td>
                                    <%= budget.getBudgetMonth()
                                            .format(monthFormatter) %>
                                </td>

                                <td>
                                    <form action="budgets" method="post"
                                          onsubmit="return confirm('Delete this budget?');">
                                        <input
                                            type="hidden"
                                            name="action"
                                            value="delete"
                                        >
                                        <input
                                            type="hidden"
                                            name="id"
                                            value="<%= budget.getId() %>"
                                        >
                                        <button type="submit"
                                                class="btn danger">
                                            Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

        <% } %>
    </section>
</main>

</body>
</html>
