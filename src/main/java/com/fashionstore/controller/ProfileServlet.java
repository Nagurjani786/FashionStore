
package com.fashionstore.controller;

import java.io.IOException;

import com.fashionstore.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Get the existing session without creating a new one
        HttpSession session = request.getSession(false);

        // Check whether the user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        // Retrieve the logged-in user
        Object userObject = session.getAttribute("user");

        if (!(userObject instanceof User)) {
            session.invalidate();

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        User user = (User) userObject;

        // Send user information to the profile JSP
        request.setAttribute("profileUser", user);

        // Display the profile page
        request.getRequestDispatcher(
                "/WEB-INF/views/profile.jsp"
        ).forward(request, response);
    }
}