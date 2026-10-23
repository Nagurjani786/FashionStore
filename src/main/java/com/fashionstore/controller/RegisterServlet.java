
package com.fashionstore.controller;

import com.fashionstore.dao.UserDAO;
import com.fashionstore.dao.impl.UserDAOImpl;
import com.fashionstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/register.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword =
                request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String postalCode = request.getParameter("postalCode");

        // Validate required fields
        if (isBlank(fullName) || isBlank(email)
                || isBlank(password)
                || isBlank(confirmPassword)) {

            showError(request, response,
                    "Please fill in all required fields.");
            return;
        }

        fullName = fullName.trim();
        email = email.trim().toLowerCase();

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            showError(request, response,
                    "Please enter a valid email address.");
            return;
        }

        if (password.length() < 8) {
            showError(request, response,
                    "Password must contain at least 8 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(request, response,
                    "Passwords do not match.");
            return;
        }

        try {
            // Check whether email already exists
            if (userDAO.emailExists(email)) {
                showError(request, response,
                        "This email is already registered.");
                return;
            }

            // Check phone only when provided
            if (!isBlank(phone)
                    && userDAO.phoneExists(phone.trim())) {

                showError(request, response,
                        "This phone number is already registered.");
                return;
            }

            User user = new User();

            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhone(clean(phone));
            user.setAddress(clean(address));
            user.setCity(clean(city));
            user.setState(clean(state));
            user.setPostalCode(clean(postalCode));

            boolean registered = userDAO.registerUser(user);

            if (registered) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/login?registered=true");
            } else {
                showError(request, response,
                        "Registration failed. Please try again.");
            }

        } catch (SQLException e) {
            throw new ServletException(
                    "Database error during registration.", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String clean(String value) {
        return value == null || value.trim().isEmpty()
                ? null : value.trim();
    }

    private void showError(HttpServletRequest request,
                           HttpServletResponse response,
                           String message)
            throws ServletException, IOException {

        request.setAttribute("error", message);

        request.getRequestDispatcher(
                "/WEB-INF/views/register.jsp")
                .forward(request, response);
    }
}
