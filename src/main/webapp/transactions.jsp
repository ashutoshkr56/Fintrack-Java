
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.fintrack.model.Transaction" %>
<%@ page import="com.fintrack.model.Category" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    if (request.getAttribute("transactions") == null) {
        response.sendRedirect("transactions");
        return;
    }

    List<Transaction> transactions =
            (List<Transaction>) request.getAttribute("transactions");

    List<Category> categories =
            (List<Category>) request.getAttribute("categories");

    String error = (String) request.getAttribute("error");
    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinTrack - Transactions</title>
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
        <h1>Transactions</h1>
        <p>Track your income and expenses.</p>
    </header>

    <% if (error != null) { %>
        <div class="alert error"><%= error %></div>
    <% } %>

    <section class="panel">
        <h2 id="formTitle">Add Transaction</h2>

        <form action="transactions" method="post" class="transaction-form" id="transactionForm">
            <input type="hidden" name="action" id="formAction" value="add">
            <input type="hidden" name="id" id="transactionId" value="">

            <div class="form-group">
                <label for="type">Transaction Type</label>
                <select name="type" id="type" required>
                    <option value="EXPENSE">Expense</option>
                    <option value="INCOME">Income</option>
                </select>
            </div>

            <div class="form-group">
                <label for="categoryId">Category</label>
                <select id="categoryId" name="categoryId" required>
                    <option value="">Select a category</option>
                    <% if (categories != null) {
                        for (Category category : categories) { %>
                            <option value="<%= category.getId() %>"
                                    data-type="<%= category.getType() %>">
                                <%= category.getName() %> - <%= category.getType() %>
                            </option>
                    <%  }
                       } %>
                </select>
            </div>

            <div class="form-group">
                <label for="amount">Amount (₹)</label>
                <input type="number" id="amount" name="amount"
                       min="0.01" step="0.01" required>
            </div>

            <div class="form-group">
                <label for="transactionDate">Date</label>
                <input type="date" id="transactionDate"
                       name="transactionDate" required>
            </div>

            <div class="form-group full-width">
                <label for="description">Description</label>
                <input type="text" id="description" name="description"
                       maxlength="255" placeholder="e.g. Grocery shopping">
            </div>

            <div class="full-width">
                <button type="submit" class="btn primary" id="submitBtn">
                    Add Transaction
                </button>
                <button type="button" class="btn" id="cancelEditBtn"
                        style="display: none; margin-left: 10px; background: #e2e8f0; color: #334155;"
                        onclick="cancelEdit()">
                    Cancel
                </button>
            </div>
        </form>
    </section>

    <section class="panel">
        <h2>Transaction History</h2>

        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Category</th>
                    <th>Description</th>
                    <th>Amount</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <% if (transactions != null && !transactions.isEmpty()) {
                    for (Transaction transaction : transactions) { %>
                    <tr>
                        <td>
                            <%= transaction.getTransactionDate().format(formatter) %>
                        </td>
                        <td>
                            <span class="<%= "INCOME".equals(transaction.getType())
                                    ? "badge income-badge"
                                    : "badge expense-badge" %>">
                                <%= transaction.getType() %>
                            </span>
                        </td>
                        <td><%= transaction.getCategoryName() != null ? transaction.getCategoryName() : "Category " + transaction.getCategoryId() %></td>
                        <td><%= transaction.getDescription() == null
                                ? "" : transaction.getDescription() %></td>
                        <td>₹ <%= transaction.getAmount() %></td>
                        <td style="display: flex; gap: 8px; align-items: center;">
                            <button type="button" class="btn primary"
                                    style="padding: 6px 12px; font-size: 13px;"
                                    data-id="<%= transaction.getId() %>"
                                    data-type="<%= transaction.getType() %>"
                                    data-category-id="<%= transaction.getCategoryId() %>"
                                    data-amount="<%= transaction.getAmount() %>"
                                    data-date="<%= transaction.getTransactionDate() %>"
                                    data-description="<%= transaction.getDescription() == null ? "" : transaction.getDescription().replace("\"", "&quot;") %>"
                                    onclick="editTransaction(this.dataset.id, this.dataset.type, this.dataset.categoryId, this.dataset.amount, this.dataset.date, this.dataset.description)">
                                Edit
                            </button>
                            <form action="transactions" method="post"
                                  onsubmit="return confirm('Delete this transaction?');" style="display:inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id"
                                       value="<%= transaction.getId() %>">
                                <button type="submit" class="btn danger" style="padding: 6px 12px; font-size: 13px;">
                                    Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                <%  }
                   } else { %>
                    <tr>
                        <td colspan="6" class="empty-state">
                            No transactions yet. Add your first transaction above.
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </section>
</main>

<script>
    const typeSelect = document.getElementById("type");
    const categorySelect = document.getElementById("categoryId");

    function filterCategories() {
        const selectedType = typeSelect.value;
        let selectedOptionAvailable = false;

        for (const option of categorySelect.options) {
            if (!option.value) {
                option.hidden = false;
                continue;
            }

            option.hidden = option.dataset.type !== selectedType;

            if (!option.hidden && option.selected) {
                selectedOptionAvailable = true;
            }
        }

        if (!selectedOptionAvailable) {
            categorySelect.value = "";
        }
    }

    typeSelect.addEventListener("change", filterCategories);
    filterCategories();

    function editTransaction(id, type, categoryId, amount, date, description) {
        document.getElementById("formTitle").textContent = "Edit Transaction";
        document.getElementById("formAction").value = "update";
        document.getElementById("transactionId").value = id;
        document.getElementById("type").value = type;
        filterCategories();
        document.getElementById("categoryId").value = categoryId;
        document.getElementById("amount").value = amount;
        document.getElementById("transactionDate").value = date;
        document.getElementById("description").value = description;
        document.getElementById("submitBtn").textContent = "Update Transaction";
        document.getElementById("cancelEditBtn").style.display = "inline-block";
        window.scrollTo({ top: 0, behavior: "smooth" });
    }

    function cancelEdit() {
        document.getElementById("formTitle").textContent = "Add Transaction";
        document.getElementById("formAction").value = "add";
        document.getElementById("transactionId").value = "";
        document.getElementById("transactionForm").reset();
        filterCategories();
        document.getElementById("submitBtn").textContent = "Add Transaction";
        document.getElementById("cancelEditBtn").style.display = "none";
    }
</script>

</body>
</html>
