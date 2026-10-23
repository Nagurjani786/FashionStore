
<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.fashionstore.model.User" %>

<%!
    // Escape user-provided text before displaying it in HTML.
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    // Get the user provided by ProfileServlet.
    Object userObject = request.getAttribute("profileUser");

    if (!(userObject instanceof User)) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    User profileUser = (User) userObject;

    String fullName = profileUser.getFullName() != null
            ? profileUser.getFullName() : "";

    String email = profileUser.getEmail() != null
            ? profileUser.getEmail() : "";

    String phone = profileUser.getPhone() != null
            ? profileUser.getPhone() : "";

    String address = profileUser.getAddress() != null
            ? profileUser.getAddress() : "";

    String city = profileUser.getCity() != null
            ? profileUser.getCity() : "";

    String state = profileUser.getState() != null
            ? profileUser.getState() : "";

    String postalCode = profileUser.getPostalCode() != null
            ? profileUser.getPostalCode() : "";

    String avatarInitial = fullName.isBlank()
            ? "U"
            : fullName.substring(0, 1).toUpperCase();
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>My Profile | FashionStore</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/profile.css?v=2">
</head>

<body>

    <!-- Navbar -->
    <jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

    <main class="profile-page">

        <section class="profile-card">

            <!-- Profile Header -->
            <div class="profile-header">

                <div class="profile-avatar" aria-hidden="true">
                    <%= escapeHtml(avatarInitial) %>
                </div>

                <div class="profile-heading">

                    <span class="profile-eyebrow">
                        YOUR ACCOUNT
                    </span>

                    <h1>My Profile</h1>

                    <p>
                        Manage your FashionStore account information.
                    </p>

                </div>

            </div>

            <!-- User Information -->
            <div class="profile-details">

                <div class="profile-detail">
                    <span class="detail-label">Full Name</span>
                    <p><%= escapeHtml(fullName) %></p>
                </div>

                <div class="profile-detail">
                    <span class="detail-label">Email Address</span>
                    <p><%= escapeHtml(email) %></p>
                </div>

                <div class="profile-detail">
                    <span class="detail-label">Phone Number</span>
                    <p><%= escapeHtml(
                            phone.isBlank() ? "Not provided" : phone) %></p>
                </div>

                <div class="profile-detail">
                    <span class="detail-label">Address</span>
                    <p><%= escapeHtml(
                            address.isBlank() ? "Not provided" : address) %></p>
                </div>

                <div class="profile-detail">
                    <span class="detail-label">City</span>
                    <p><%= escapeHtml(
                            city.isBlank() ? "Not provided" : city) %></p>
                </div>

                <div class="profile-detail">
                    <span class="detail-label">State</span>
                    <p><%= escapeHtml(
                            state.isBlank() ? "Not provided" : state) %></p>
                </div>

                <div class="profile-detail">
                    <span class="detail-label">Postal Code</span>
                    <p><%= escapeHtml(
                            postalCode.isBlank() ? "Not provided" : postalCode) %></p>
                </div>

            </div>

            <!-- Profile Actions -->
            <div class="profile-actions">

                <a href="${pageContext.request.contextPath}/home"
                   class="continue-shopping">
                    Continue Shopping
                </a>

                <form action="${pageContext.request.contextPath}/logout"
                      method="post">

                    <button type="submit" class="profile-logout">
                        Logout
                    </button>

                </form>

            </div>

        </section>

    </main>

    <!-- Footer -->
    <jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
