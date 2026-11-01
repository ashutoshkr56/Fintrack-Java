
package com.fintrack.servlet;

import com.fintrack.dao.TransactionDAO;
import com.fintrack.model.Category;
import com.fintrack.model.Transaction;
import com.fintrack.service.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        try {
            loadTransactions(request, userId);

            request.getRequestDispatcher("/transactions.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to load transactions.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                addTransaction(request, userId);
            } else if ("update".equals(action)) {
                updateTransaction(request, userId);
            } else if ("delete".equals(action)) {
                deleteTransaction(request, userId);
            } else {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid transaction action."
                );
                return;
            }

            response.sendRedirect("transactions");

        } catch (IllegalArgumentException | java.time.format.DateTimeParseException e) {
            String errorMsg = e instanceof java.time.format.DateTimeParseException
                    ? "Invalid date format. Please select a valid date."
                    : (e instanceof NumberFormatException
                        ? "Invalid number entered for amount or category."
                        : e.getMessage());
            request.setAttribute("error", errorMsg);

            try {
                loadTransactions(request, userId);

                request.getRequestDispatcher("/transactions.jsp")
                        .forward(request, response);

            } catch (Exception ex) {
                throw new ServletException(
                        "Unable to reload transactions.", ex
                );
            }

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to process transaction.", e
            );
        }
    }

    private void addTransaction(HttpServletRequest request, int userId)
            throws Exception {

        int categoryId = Integer.parseInt(
                request.getParameter("categoryId")
        );

        String type = request.getParameter("type");
        BigDecimal amount = new BigDecimal(
                request.getParameter("amount")
        );

        String description = request.getParameter("description");
        LocalDate date = LocalDate.parse(
                request.getParameter("transactionDate")
        );

        validateTransaction(
                type, amount, description, date, categoryId, userId
        );

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setCategoryId(categoryId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDescription(
                description == null ? "" : description.trim()
        );
        transaction.setTransactionDate(date);

        transactionDAO.addTransaction(transaction);
    }

    private void updateTransaction(HttpServletRequest request, int userId)
            throws Exception {

        int id = Integer.parseInt(request.getParameter("id"));

        int categoryId = Integer.parseInt(
                request.getParameter("categoryId")
        );

        String type = request.getParameter("type");
        BigDecimal amount = new BigDecimal(
                request.getParameter("amount")
        );

        String description = request.getParameter("description");
        LocalDate date = LocalDate.parse(
                request.getParameter("transactionDate")
        );

        validateTransaction(
                type, amount, description, date, categoryId, userId
        );

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUserId(userId);
        transaction.setCategoryId(categoryId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDescription(
                description == null ? "" : description.trim()
        );
        transaction.setTransactionDate(date);

        transactionDAO.updateTransaction(transaction);
    }

    private void deleteTransaction(HttpServletRequest request, int userId)
            throws Exception {

        int id = Integer.parseInt(request.getParameter("id"));
        transactionDAO.deleteTransaction(id, userId);
    }

    private void validateTransaction(String type, BigDecimal amount,
                                     String description, LocalDate date,
                                     int categoryId, int userId)
            throws Exception {

        if (!"INCOME".equals(type) && !"EXPENSE".equals(type)) {
            throw new IllegalArgumentException(
                    "Select a valid transaction type."
            );
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        if (date == null || categoryId <= 0) {
            throw new IllegalArgumentException(
                    "Select a valid date and category."
            );
        }

        if (description != null && description.length() > 255) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 255 characters."
            );
        }

        List<Category> categories =
                categoryService.getCategories(userId);

        boolean validCategory = false;

        for (Category category : categories) {
            if (category.getId() == categoryId
                    && type.equals(category.getType())) {
                validCategory = true;
                break;
            }
        }

        if (!validCategory) {
            throw new IllegalArgumentException(
                    "Select a category that matches the transaction type."
            );
        }
    }

    private void loadTransactions(HttpServletRequest request, int userId)
            throws Exception {

        List<Transaction> transactions =
                transactionDAO.getTransactionsByUserId(userId);

        List<Category> categories =
                categoryService.getCategories(userId);

        request.setAttribute("transactions", transactions);
        request.setAttribute("categories", categories);
    }
}
