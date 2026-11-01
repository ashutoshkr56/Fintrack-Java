
package com.fintrack.servlet;

import com.fintrack.service.FinanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final FinanceService financeService = new FinanceService();

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
            BigDecimal income = financeService.getTotalIncome(userId);
            BigDecimal expenses = financeService.getTotalExpenses(userId);
            BigDecimal balance = financeService.getBalance(income, expenses);
            BigDecimal savingsRate = financeService.getSavingsRate(income, balance);

            request.setAttribute("income", income);
            request.setAttribute("expenses", expenses);
            request.setAttribute("balance", balance);
            request.setAttribute("savingsRate", savingsRate);

            request.getRequestDispatcher("/dashboard.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to load dashboard.", e);
        }
    }
}
