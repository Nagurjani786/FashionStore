
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         isELIgnored="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Create Account | FashionStore</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/register.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="register-page">

        <section class="register-card">

            <div class="register-heading">
                <h1>Create Your Account</h1>
                <p>Join FashionStore and discover your style.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="message error-message" role="alert">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/register"
                  method="post"
                  class="register-form">

                <div class="form-group">
                    <label for="fullName">Full Name *</label>
                    <input type="text"
                           id="fullName"
                           name="fullName"
                           placeholder="Enter your full name"
                           value="<%= request.getParameter("fullName") != null
                               ? request.getParameter("fullName") : "" %>"
                           maxlength="100"
                           autocomplete="name"
                           required>
                </div>

                <div class="form-group">
                    <label for="email">Email Address *</label>
                    <input type="email"
                           id="email"
                           name="email"
                           placeholder="you@example.com"
                           value="<%= request.getParameter("email") != null
                               ? request.getParameter("email") : "" %>"
                           maxlength="150"
                           autocomplete="email"
                           required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="password">Password *</label>
                        <input type="password"
                               id="password"
                               name="password"
                               placeholder="At least 8 characters"
                               minlength="8"
                               autocomplete="new-password"
                               required>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password *</label>
                        <input type="password"
                               id="confirmPassword"
                               name="confirmPassword"
                               placeholder="Re-enter password"
                               minlength="8"
                               autocomplete="new-password"
                               required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="tel"
                           id="phone"
                           name="phone"
                           placeholder="Enter your phone number"
                           value="<%= request.getParameter("phone") != null
                               ? request.getParameter("phone") : "" %>"
                           maxlength="20"
                           autocomplete="tel">
                </div>

                <div class="form-group">
                    <label for="address">Address</label>
                    <textarea id="address"
                              name="address"
                              rows="2"
                              maxlength="500"
                              placeholder="House number, street, area"><%= request.getParameter("address") != null
                                  ? request.getParameter("address") : "" %></textarea>
                </div>

                <div class="form-row form-row-three">
                    <div class="form-group">
                        <label for="city">City</label>
                        <input type="text"
                               id="city"
                               name="city"
                               maxlength="100"
                               value="<%= request.getParameter("city") != null
                                   ? request.getParameter("city") : "" %>"
                               placeholder="City">
                    </div>

                    <div class="form-group">
                        <label for="state">State</label>
                        <input type="text"
                               id="state"
                               name="state"
                               maxlength="100"
                               value="<%= request.getParameter("state") != null
                                   ? request.getParameter("state") : "" %>"
                               placeholder="State">
                    </div>

                    <div class="form-group">
                        <label for="postalCode">Postal Code</label>
                        <input type="text"
                               id="postalCode"
                               name="postalCode"
                               maxlength="20"
                               value="<%= request.getParameter("postalCode") != null
                                   ? request.getParameter("postalCode") : "" %>"
                               placeholder="Postal code">
                    </div>
                </div>

                <button type="submit" class="register-button">
                    Create Account
                </button>

            </form>

            <p class="login-prompt">
                Already have an account?
                <a href="${pageContext.request.contextPath}/login">
                    Login here
                </a>
            </p>

        </section>

    </main>

    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
