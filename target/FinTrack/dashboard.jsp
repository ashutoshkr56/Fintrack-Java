
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    if (request.getAttribute("income") == null) {
        response.sendRedirect("dashboard");
        return;
    }

    String userName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinTrack - Dashboard</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
        }

        body {
            background: #f1f5f9;
            color: #1e293b;
        }

        .navbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 16px;
            padding: 18px 6%;
            background: white;
            border-bottom: 1px solid #e2e8f0;
        }

        .brand {
            color: #2563eb;
            font-size: 24px;
            font-weight: bold;
        }

        .navbar a {
            color: #475569;
            text-decoration: none;
            font-size: 14px;
        }

        .navbar a:hover {
            color: #2563eb;
        }

        .container {
            max-width: 1150px;
            margin: 35px auto;
            padding: 0 22px;
        }

        .welcome {
            margin-bottom: 28px;
        }

        .welcome h1 {
            font-size: 28px;
            margin-bottom: 8px;
        }

        .welcome p {
            color: #64748b;
        }

        .cards {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 18px;
        }

        .card {
            background: white;
            padding: 24px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
        }

        .card p {
            color: #64748b;
            font-size: 14px;
            margin-bottom: 15px;
        }

        .card h2 {
            font-size: 25px;
            overflow-wrap: anywhere;
        }

        .income {
            color: #16a34a;
        }

        .expenses {
            color: #dc2626;
        }

        .balance {
            color: #2563eb;
        }

        .savings {
            color: #9333ea;
        }

        .section {
            margin-top: 28px;
            padding: 24px;
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
        }

        .section h2 {
            font-size: 19px;
            margin-bottom: 10px;
        }

        .section p {
            color: #64748b;
            line-height: 1.6;
        }

        .logout {
            padding: 9px 14px;
            border-radius: 7px;
            background: #fee2e2;
            color: #b91c1c !important;
        }

        @media (max-width: 850px) {
            .cards {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 500px) {
            .navbar {
                align-items: flex-start;
                flex-direction: column;
            }

            .cards {
                grid-template-columns: 1fr;
            }

            .welcome h1 {
                font-size: 24px;
            }
        }
    </style>
</head>
<body>

<nav class="navbar">
    <div class="brand">FinTrack</div>
    <div>
        <a href="dashboard">Dashboard</a>
        &nbsp;&nbsp;
        <a href="transactions">Transactions</a>
        &nbsp;&nbsp;
        <a href="categories">Categories</a>
        &nbsp;&nbsp;
        <a href="budgets">Budgets</a>
        &nbsp;&nbsp;
        <a href="reports">Reports</a>
        &nbsp;&nbsp;
        <a class="logout" href="logout">Logout</a>
    </div>
</nav>

<main class="container">
    <div class="welcome">
        <h1>Welcome, <%= userName == null ? "User" : userName %>!</h1>
        <p>Here's an overview of your personal finances.</p>
    </div>

    <div class="cards">
        <div class="card">
            <p>Total Income</p>
            <h2 class="income">
                ₹ <%= request.getAttribute("income") %>
            </h2>
        </div>

        <div class="card">
            <p>Total Expenses</p>
            <h2 class="expenses">
                ₹ <%= request.getAttribute("expenses") %>
            </h2>
        </div>

        <div class="card">
            <p>Current Balance</p>
            <h2 class="balance">
                ₹ <%= request.getAttribute("balance") %>
            </h2>
        </div>

        <div class="card">
            <p>Savings Rate</p>
            <h2 class="savings">
                <%= request.getAttribute("savingsRate") %>%
            </h2>
        </div>
    </div>

    <div class="section">
        <h2>Your Financial Overview</h2>
        <p>
            Your income, expenses, balance, and savings rate are calculated
            from the transactions stored in your FinTrack database.
            Once you add transactions, your dashboard will reflect them.
        </p>
    </div>
</main>

</body>
</html>
