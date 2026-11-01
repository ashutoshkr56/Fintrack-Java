
package com.fintrack.servlet;

import com.fintrack.model.Category;
import com.fintrack.service.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {

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
            List<Category> categories =
                    categoryService.getCategories(userId);

            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/categories.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to load categories.", e);
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
                String name = request.getParameter("name");
                String type = request.getParameter("type");

                Category category = new Category();
                category.setUserId(userId);
                category.setName(name);
                category.setType(type);

                categoryService.addCategory(category);

            } else if ("delete".equals(action)) {
                int categoryId = Integer.parseInt(
                        request.getParameter("id")
                );

                categoryService.deleteCategory(categoryId, userId);

            } else {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid category action."
                );
                return;
            }

            response.sendRedirect("categories");

        } catch (IllegalArgumentException e) {
            forwardWithError(request, response, userId, e.getMessage());

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            String message = "delete".equals(action)
                    ? "Cannot delete category: it is currently linked to existing transactions or budgets."
                    : "A category with this name and type already exists.";
            forwardWithError(request, response, userId, message);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to process category.", e
            );
        }
    }

    private void forwardWithError(HttpServletRequest request,
                                  HttpServletResponse response,
                                  int userId,
                                  String errorMessage)
            throws ServletException, IOException {

        request.setAttribute("error", errorMessage);

        try {
            request.setAttribute(
                    "categories",
                    categoryService.getCategories(userId)
            );

            request.getRequestDispatcher("/categories.jsp")
                    .forward(request, response);

        } catch (Exception ex) {
            throw new ServletException(
                    "Unable to reload categories.", ex
            );
        }
    }
}
