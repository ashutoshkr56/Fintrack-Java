
<%@ page import="java.util.List" %>
<%@ page import="com.fintrack.model.Category" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    if (request.getAttribute("categories") == null) {
        response.sendRedirect("categories");
        return;
    }

    List<Category> categories =
            (List<Category>) request.getAttribute("categories");

    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinTrack - Categories</title>
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
        <h1>Categories</h1>
        <p>Organize your income and expenses.</p>
    </header>

    <% if (error != null) { %>
        <div class="alert error"><%= error %></div>
    <% } %>

    <section class="panel">
        <h2>Add Category</h2>

        <form action="categories" method="post" class="transaction-form">
            <input type="hidden" name="action" value="add">

            <div class="form-group">
                <label for="name">Category Name</label>
                <input type="text" id="name" name="name"
                       maxlength="100" required
                       placeholder="e.g. Food, Salary, Transport">
            </div>

            <div class="form-group">
                <label for="type">Category Type</label>
                <select id="type" name="type" required>
                    <option value="EXPENSE">Expense</option>
                    <option value="INCOME">Income</option>
                </select>
            </div>

            <button type="submit" class="btn primary">Add Category</button>
        </form>
    </section>

    <section class="panel">
        <h2>Your Categories</h2>

        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <% if (categories != null && !categories.isEmpty()) {
                    for (Category category : categories) { %>
                    <tr>
                        <td><%= category.getId() %></td>
                        <td><%= category.getName() %></td>
                        <td>
                            <span class="<%= "INCOME".equals(category.getType())
                                    ? "badge income-badge" : "badge expense-badge" %>">
                                <%= category.getType() %>
                            </span>
                        </td>
                        <td>
                            <% if (category.getUserId() != null
                                    && category.getUserId().equals(
                                            (Integer) session.getAttribute("userId"))) { %>
                                <form action="categories" method="post"
                                      onsubmit="return confirm('Delete this category?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id"
                                           value="<%= category.getId() %>">
                                    <button type="submit" class="btn danger">
                                        Delete
                                    </button>
                                </form>
                            <% } else { %>
                                <span>Default category</span>
                            <% } %>
                        </td>
                    </tr>
                <%  }
                   } else { %>
                    <tr>
                        <td colspan="4" class="empty-state">
                            No categories available yet. Add your first category above.
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </section>
</main>

</body>
</html>
