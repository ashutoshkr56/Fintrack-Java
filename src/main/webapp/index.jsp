
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("userId") != null) {
        response.sendRedirect("dashboard");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinTrack - Login & Register</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
        }

        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #f1f5f9;
            color: #1e293b;
            padding: 20px;
        }

        .container {
            width: 100%;
            max-width: 420px;
            padding: 32px;
            background: white;
            border-radius: 14px;
            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
        }

        h1 {
            text-align: center;
            color: #2563eb;
            margin-bottom: 8px;
        }

        .subtitle {
            text-align: center;
            color: #64748b;
            margin-bottom: 24px;
        }

        .tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 22px;
        }

        .tabs button {
            flex: 1;
            padding: 11px;
            border: none;
            border-radius: 7px;
            background: #e2e8f0;
            cursor: pointer;
            font-size: 15px;
        }

        .tabs button.active {
            background: #2563eb;
            color: white;
        }

        .form {
            display: none;
        }

        .form.active {
            display: block;
        }

        label {
            display: block;
            margin: 14px 0 6px;
            font-size: 14px;
            font-weight: bold;
        }

        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #cbd5e1;
            border-radius: 7px;
            font-size: 15px;
        }

        input:focus {
            outline: 2px solid #bfdbfe;
            border-color: #2563eb;
        }

        .submit-btn {
            width: 100%;
            margin-top: 22px;
            padding: 12px;
            border: none;
            border-radius: 7px;
            background: #2563eb;
            color: white;
            font-size: 16px;
            cursor: pointer;
        }

        .submit-btn:hover {
            background: #1d4ed8;
        }

        .message {
            margin-bottom: 14px;
            padding: 10px;
            border-radius: 7px;
            background: #fee2e2;
            color: #991b1b;
            font-size: 14px;
        }

        .success {
            background: #dcfce7;
            color: #166534;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>FinTrack</h1>
    <p class="subtitle">Manage your money. Track your goals.</p>

    <% if (request.getAttribute("error") != null) { %>
        <div class="message">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <% if ("true".equals(request.getParameter("registered"))) { %>
        <div class="message success">
            Registration successful. Please log in.
        </div>
    <% } %>

    <div class="tabs">
        <button type="button" id="loginTab" class="active"
                onclick="showForm('login')">Login</button>
        <button type="button" id="registerTab"
                onclick="showForm('register')">Register</button>
    </div>

    <form id="loginForm" class="form active" action="auth" method="post">
        <input type="hidden" name="action" value="login">

        <label for="loginEmail">Email</label>
        <input type="email" id="loginEmail" name="email" required>

        <label for="loginPassword">Password</label>
        <input type="password" id="loginPassword" name="password" required>

        <button type="submit" class="submit-btn">Login</button>
    </form>

    <form id="registerForm" class="form" action="auth" method="post">
        <input type="hidden" name="action" value="register">

        <label for="name">Full name</label>
        <input type="text" id="name" name="name" required>

        <label for="registerEmail">Email</label>
        <input type="email" id="registerEmail" name="email" required>

        <label for="registerPassword">Password (minimum 6 characters)</label>
        <input type="password" id="registerPassword" name="password"
               minlength="6" required>

        <button type="submit" class="submit-btn">Create Account</button>
    </form>
</div>

<script>
    function showForm(formName) {
        document.getElementById("loginForm").classList.toggle(
            "active", formName === "login"
        );
        document.getElementById("registerForm").classList.toggle(
            "active", formName === "register"
        );
        document.getElementById("loginTab").classList.toggle(
            "active", formName === "login"
        );
        document.getElementById("registerTab").classList.toggle(
            "active", formName === "register"
        );
    }

    <% if ("register".equals(request.getParameter("form"))
            || request.getAttribute("error") != null
            && "register".equals(request.getParameter("action"))) { %>
        showForm("register");
    <% } %>
</script>

</body>
</html>
