
package com.fintrack.servlet;
import com.fintrack.model.Category;
import com.fintrack.service.CategoryService;
import com.fintrack.model.Budget;
import com.fintrack.service.BudgetService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@WebServlet("/budgets")
public class BudgetServlet extends HttpServlet {

private final CategoryService categoryService = new CategoryService();

    private final BudgetService budgetService = new BudgetService();

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
            List<Budget> budgets = budgetService.getBudgets(userId);

            List<Category> categories = categoryService.getCategories(userId)
                    .stream()
                    .filter(category -> "EXPENSE".equals(category.getType()))
                    .toList();

            request.setAttribute("budgets", budgets);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/budgets.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to load budgets.", e);
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
                int categoryId = Integer.parseInt(
                        request.getParameter("categoryId"));

                BigDecimal amount = new BigDecimal(
                        request.getParameter("amount"));

                YearMonth month = YearMonth.parse(
                        request.getParameter("budgetMonth"));

                List<Category> categories = categoryService.getCategories(userId);

                boolean validCategory = false;

                for (Category category : categories) {
                    if (category.getId() == categoryId
                            && "EXPENSE".equals(category.getType())) {
                        validCategory = true;
                        break;
                    }
                }

                if (!validCategory) {
                    throw new IllegalArgumentException(
                            "Please select a valid expense category.");
                }

                Budget budget = new Budget();
                budget.setUserId(userId);
                budget.setCategoryId(categoryId);
                budget.setAmount(amount);
                budget.setBudgetMonth(month.atDay(1));

                budgetService.saveBudget(budget);
            } else if ("delete".equals(action)) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.isBlank()) {
                    idParam = request.getParameter("budgetId");
                }
                if (idParam == null || idParam.isBlank()) {
                    throw new IllegalArgumentException("Budget ID is required for deletion.");
                }
                int budgetId = Integer.parseInt(idParam);
                budgetService.deleteBudget(budgetId, userId);

            } else {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid budget action."
                );
                return;
            }

            response.sendRedirect("budgets");

        } catch (IllegalArgumentException | java.time.format.DateTimeParseException e) {
            request.setAttribute("error", e.getMessage());
            loadBudgets(request, response, userId);

        } catch (Exception e) {
            throw new ServletException("Unable to process budget.", e);
        }
    }

    private void loadBudgets(HttpServletRequest request,
                             HttpServletResponse response,
                             int userId)
            throws ServletException, IOException {

        try {
            request.setAttribute(
                    "budgets",
                    budgetService.getBudgets(userId)
            );

            List<Category> categories = categoryService.getCategories(userId)
                    .stream()
                    .filter(category -> "EXPENSE".equals(category.getType()))
                    .toList();
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/budgets.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to reload budgets.", e);
        }
    }
}
