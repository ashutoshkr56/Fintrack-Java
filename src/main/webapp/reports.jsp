
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.math.BigDecimal" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    BigDecimal income = (BigDecimal) request.getAttribute("income");
    BigDecimal expenses = (BigDecimal) request.getAttribute("expenses");
    BigDecimal balance = (BigDecimal) request.getAttribute("balance");
    String selectedMonth = (String) request.getAttribute("selectedMonth");

    if (income == null) income = BigDecimal.ZERO;
    if (expenses == null) expenses = BigDecimal.ZERO;
    if (balance == null) balance = BigDecimal.ZERO;
    if (selectedMonth == null) {
        selectedMonth = java.time.YearMonth.now().toString();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinTrack - Reports</title>
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
        <h1>Financial Reports</h1>
        <p>Review your monthly income, expenses, and savings.</p>
    </header>

    <section class="panel">
        <h2>Select Month</h2>

        <form action="reports" method="get" class="transaction-form">
            <div class="form-group">
                <label for="month">Report Month</label>
                <input type="month" id="month" name="month"
                       value="<%= selectedMonth %>" required>
            </div>

            <button type="submit" class="btn primary">Generate Report</button>
        </form>
    </section>

    <section class="summary-grid">
        <div class="summary-card income-card">
            <h3>Total Income</h3>
            <p>₹<%= income.toPlainString() %></p>
        </div>

        <div class="summary-card expense-card">
            <h3>Total Expenses</h3>
            <p>₹<%= expenses.toPlainString() %></p>
        </div>

        <div class="summary-card balance-card">
            <h3>Monthly Balance</h3>
            <p>₹<%= balance.toPlainString() %></p>
        </div>
    </section>

    <section class="panel">
        <h2>Report Summary</h2>
        <p>Selected month: <strong><%= selectedMonth %></strong></p>

        <% if (balance.compareTo(BigDecimal.ZERO) > 0) { %>
            <p>Your income is greater than your expenses this month.</p>
        <% } else if (balance.compareTo(BigDecimal.ZERO) < 0) { %>
            <p>Your expenses are greater than your income this month.</p>
        <% } else { %>
            <p>Your income and expenses are equal this month.</p>
        <% } %>
    </section>
</main>

</body>
</html>
