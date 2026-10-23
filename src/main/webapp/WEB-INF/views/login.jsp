
<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Login | FashionStore</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="auth-page">
        <section class="auth-card">

            <div class="auth-heading">
                <h1>Welcome Back!</h1>
                <p>Sign in to your FashionStore account.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="auth-message error" role="alert">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <% if ("true".equals(request.getParameter("registered"))) { %>
                <div class="auth-message success" role="status">
                    Registration successful! Please log in.
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/login"
                  method="post"
                  class="auth-form">

                <div class="auth-form-group">
                    <label for="email">Email Address</label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        placeholder="Enter your email"
                        autocomplete="email"
                        maxlength="254"
                        required>
                </div>

                <div class="auth-form-group">
                    <label for="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Enter your password"
                        autocomplete="current-password"
                        required>
                </div>

                <button type="submit" class="auth-button">
                    Login
                </button>

            </form>

            <p class="auth-switch">
                Don't have an account?
                <a href="${pageContext.request.contextPath}/register">
                    Create Account
                </a>
            </p>

        </section>
    </main>

    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
