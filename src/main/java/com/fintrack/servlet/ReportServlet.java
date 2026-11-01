
package com.fintrack.servlet;

import com.fintrack.service.ReportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

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

        String monthParameter = request.getParameter("month");
        YearMonth selectedMonth;

        try {
            selectedMonth = (monthParameter == null
                    || monthParameter.isBlank())
                    ? YearMonth.now()
                    : YearMonth.parse(monthParameter);
        } catch (Exception e) {
            selectedMonth = YearMonth.now();
        }

        try {
            BigDecimal income =
                    reportService.getMonthlyIncome(userId, selectedMonth);

            BigDecimal expenses =
                    reportService.getMonthlyExpenses(userId, selectedMonth);

            BigDecimal balance =
                    reportService.getMonthlyBalance(income, expenses);

            request.setAttribute("income", income);
            request.setAttribute("expenses", expenses);
            request.setAttribute("balance", balance);
            request.setAttribute("selectedMonth", selectedMonth.toString());

            request.getRequestDispatcher("/reports.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to generate report.", e);
        }
    }
}
