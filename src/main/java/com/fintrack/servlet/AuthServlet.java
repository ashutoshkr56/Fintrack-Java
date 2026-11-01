
package com.fintrack.servlet;
import java.sql.SQLException;
import com.fintrack.dao.UserDAO;
import com.fintrack.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                        HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("login".equals(action)) {
                login(request, response);
            } else if ("register".equals(action)) {
                register(request, response);
            } else {
                response.sendRedirect("index.jsp");
            }
        } catch (SQLException e) {
            throw new ServletException("Database operation failed.", e);
        }
    }

    private void login(HttpServletRequest request,
                   HttpServletResponse response)
        throws IOException, ServletException, SQLException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = userDAO.getUserByEmail(email);

        if (user != null && password != null
                && checkPassword(password, user.getPasswordHash())) {

            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());

            response.sendRedirect("dashboard");
        } else {
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    private void register(HttpServletRequest request,
                       HttpServletResponse response)
        throws IOException, ServletException, SQLException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.length() < 6) {

            request.setAttribute(
                    "error",
                    "Enter your name and email, and use a password of at least 6 characters."
            );
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        if (userDAO.emailExists(email.trim())) {
            request.setAttribute("error", "This email is already registered.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPasswordHash(hashPassword(password));

        userDAO.addUser(user);

        response.sendRedirect("index.jsp?registered=true");
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(
                    password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password.", e);
        }
    }

    private boolean checkPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        return storedHash.equals(hashPassword(plainPassword))
                || storedHash.equals(plainPassword);
    }
}
