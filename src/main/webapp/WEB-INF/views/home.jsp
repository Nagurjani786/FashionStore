
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fashionstore.model.Category" %>
<%@ page import="com.fashionstore.model.Product" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description"
          content="Discover fashionable clothing and accessories at FashionStore.">

    <title>FashionStore | Discover Your Style</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/home.css?v=4">
</head>
<body>

<%@ include file="/WEB-INF/views/partials/navbar.jsp" %>

<main class="home-content">

    <!-- HERO: TEXT LEFT, SINGLE ANIMATED BANNER RIGHT -->
    <section class="hero-split" aria-label="Featured fashion collection">

        <div class="hero-copy">
            <span class="hero-label" id="hero-label">
                NEW SEASON EDIT
            </span>

            <h1 id="hero-title">
                Discover Your Style
            </h1>

            <p id="hero-description">
                Explore fresh fashion, timeless essentials, and outfits
                that make every day feel special.
            </p>

            <a class="hero-shop-button"
               id="hero-link"
               href="${pageContext.request.contextPath}/products">
                <span id="hero-button-text">Shop Collection</span>
                <span aria-hidden="true">→</span>
            </a>

            <div class="hero-features">
                <div class="hero-feature">
                    <span class="feature-symbol" aria-hidden="true">✓</span>
                    <span>
                        <strong>Curated Styles</strong>
                        <small>Find your perfect look</small>
                    </span>
                </div>

                <div class="hero-feature">
                    <span class="feature-symbol" aria-hidden="true">♡</span>
                    <span>
                        <strong>Easy Shopping</strong>
                        <small>A simple shopping experience</small>
                    </span>
                </div>

                <div class="hero-feature">
                    <span class="feature-symbol" aria-hidden="true">↗</span>
                    <span>
                        <strong>Latest Trends</strong>
                        <small>Discover new arrivals</small>
                    </span>
                </div>
            </div>
        </div>

        <!-- Banner area: all images occupy the same space -->
        <div class="hero-banner">

            <div class="hero-banner-track">

                <div class="hero-banner-slide active">
                    <img
                        class="hero-banner-image"
                        src="${pageContext.request.contextPath}/assets/images/Banner1.jpg"
                        alt="FashionStore new season collection"
                        fetchpriority="high">
                </div>

                <div class="hero-banner-slide">
                    <img
                        class="hero-banner-image"
                        src="${pageContext.request.contextPath}/assets/images/banner2.jpg"
                        alt="FashionStore special offer"
                        loading="lazy">
                </div>

                <div class="hero-banner-slide">
                    <img
                        class="hero-banner-image"
                        src="${pageContext.request.contextPath}/assets/images/banner3.jpg"
                        alt="FashionStore fashion sale"
                        loading="lazy">
                </div>

            </div>

            <div class="banner-caption">
                <span class="banner-caption-icon" aria-hidden="true">✦</span>
                <span>
                    <strong id="banner-caption-title">New Arrivals</strong>
                    <small id="banner-caption-description">
                        Explore the latest styles
                    </small>
                </span>
            </div>

            <button class="hero-arrow hero-prev"
                    type="button"
                    aria-label="Previous banner">
                &#10094;
            </button>

            <button class="hero-arrow hero-next"
                    type="button"
                    aria-label="Next banner">
                &#10095;
            </button>

            <div class="hero-dots" aria-label="Choose a banner">
                <button class="active"
                        type="button"
                        aria-label="Show banner 1"
                        aria-current="true"></button>

                <button type="button"
                        aria-label="Show banner 2"></button>

                <button type="button"
                        aria-label="Show banner 3"></button>
            </div>

            <span class="hero-slide-count" aria-live="polite">
                <span id="hero-current-slide">01</span> / 03
            </span>

        </div>

        <div class="hero-decoration hero-decoration-one"></div>
        <div class="hero-decoration hero-decoration-two"></div>

    </section>

    <!-- SHOPPING BENEFITS -->
    <section class="benefits-strip" aria-label="Shopping benefits">

        <div class="benefit-item">
            <span class="benefit-icon" aria-hidden="true">✦</span>
            <div>
                <h3>Curated Styles</h3>
                <p>Find your perfect look</p>
            </div>
        </div>

        <div class="benefit-item">
            <span class="benefit-icon" aria-hidden="true">♡</span>
            <div>
                <h3>Easy Shopping</h3>
                <p>A simple browsing experience</p>
            </div>
        </div>

        <div class="benefit-item">
            <span class="benefit-icon" aria-hidden="true">✓</span>
            <div>
                <h3>Made for You</h3>
                <p>Discover your personal style</p>
            </div>
        </div>

    </section>

    <!-- SHOP BY CATEGORY -->
    <section class="home-section category-section">

        <div class="section-heading">
            <div>
                <span class="section-eyebrow">FIND YOUR STYLE</span>
                <h2>Shop by Category</h2>
                <p>Start with the styles you love.</p>
            </div>

            <a class="view-all"
               href="${pageContext.request.contextPath}/products">
                Explore All <span aria-hidden="true">→</span>
            </a>
        </div>

        <div class="category-grid">

            <%
                List<Category> categories =
                    (List<Category>) request.getAttribute("categories");

                if (categories != null && !categories.isEmpty()) {
                    for (Category category : categories) {
            %>

                <a class="category-card"
                   href="<%= request.getContextPath() %>/products?categoryId=<%= category.getCategoryId() %>">

                    <span class="category-card-name">
                        <%= category.getCategoryName() %>
                    </span>

                    <span class="category-card-arrow" aria-hidden="true">
                        →
                    </span>
                </a>

            <%
                    }
                } else {
            %>

                <p class="empty-message">
                    No categories are available right now.
                </p>

            <%
                }
            %>

        </div>
    </section>

    <!-- LATEST PRODUCTS -->
    <section class="home-section featured-products">

        <div class="section-heading">
            <div>
                <span class="section-eyebrow">JUST ARRIVED</span>
                <h2>Latest Arrivals</h2>
                <p>Discover the latest additions to our collection.</p>
            </div>

            <a class="view-all"
               href="${pageContext.request.contextPath}/products">
                View All Products <span aria-hidden="true">→</span>
            </a>
        </div>

        <div class="product-grid">

            <%
                List<Product> latestProducts =
                    (List<Product>) request.getAttribute("latestProducts");

                if (latestProducts != null && !latestProducts.isEmpty()) {
                    for (Product product : latestProducts) {

                        String imageUrl = product.getImageUrl();
                        String finalImageUrl;

                        if (imageUrl == null
                                || imageUrl.trim().isEmpty()
                                || imageUrl.startsWith("file:")) {

                            finalImageUrl = request.getContextPath()
                                + "/assets/images/MENSHoodie.jpg";

                        } else if (imageUrl.startsWith("http://")
                                || imageUrl.startsWith("https://")) {

                            finalImageUrl = imageUrl;

                        } else if (imageUrl.startsWith("/")) {

                            finalImageUrl = request.getContextPath() + imageUrl;

                        } else {

                            finalImageUrl = request.getContextPath()
                                + "/assets/images/" + imageUrl;
                        }
            %>

                <article class="product-card">

                    <a class="product-image"
                       href="<%= request.getContextPath() %>/product?id=<%= product.getProductId() %>">

                        <img
                            src="<%= finalImageUrl %>"
                            alt="<%= product.getProductName() %>"
                            loading="lazy"
                            onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/assets/images/MENSHoodie.jpg';">

                        
                    </a>

                    <div class="product-info">

                        <span class="product-brand">
                            <%= product.getBrand() == null
                                    || product.getBrand().isBlank()
                                    ? "FashionStore"
                                    : product.getBrand() %>
                        </span>

                        <h3 class="product-name">
                            <a href="<%= request.getContextPath() %>/product?id=<%= product.getProductId() %>">
                                <%= product.getProductName() %>
                            </a>
                        </h3>

                        <p class="product-price">
                            ₹<%= product.getPrice() %>
                        </p>

                        <a class="product-shop-button"
                           href="<%= request.getContextPath() %>/product?id=<%= product.getProductId() %>">
                            View Details <span aria-hidden="true">→</span>
                        </a>

                    </div>
                </article>

            <%
                    }
                } else {
            %>

                <p class="empty-message">
                    No products are available right now.
                    Please check back soon.
                </p>

            <%
                }
            %>

        </div>
    </section>

</main>

<%@ include file="/WEB-INF/views/partials/footer.jsp" %>

<script>
(() => {
    const hero = document.querySelector(".hero-split");

    if (!hero || hero.dataset.initialized === "true") {
        return;
    }

    hero.dataset.initialized = "true";

    const slides = Array.from(
        hero.querySelectorAll(".hero-banner-slide")
    );

    const dots = Array.from(
        hero.querySelectorAll(".hero-dots button")
    );

    const previousButton = hero.querySelector(".hero-prev");
    const nextButton = hero.querySelector(".hero-next");

    const label = hero.querySelector("#hero-label");
    const title = hero.querySelector("#hero-title");
    const description = hero.querySelector("#hero-description");
    const buttonText = hero.querySelector("#hero-button-text");

    const captionTitle = hero.querySelector("#banner-caption-title");
    const captionDescription =
        hero.querySelector("#banner-caption-description");

    const currentSlideText =
        hero.querySelector("#hero-current-slide");

    const slideContent = [
        {
            label: "NEW SEASON EDIT",
            title: "Discover Your Style",
            description:
                "Explore fresh fashion, timeless essentials, and outfits that make every day feel special.",
            button: "Shop Collection",
            captionTitle: "New Arrivals",
            captionDescription: "Explore the latest styles"
        },
        {
            label: "SPECIAL OFFER",
            title: "Find Your Perfect Look",
            description:
                "Refresh your wardrobe with versatile styles for every moment.",
            button: "Explore Offers",
            captionTitle: "Special Offers",
            captionDescription: "Discover your favourites"
        },
        {
            label: "FIND YOUR NEXT FAVOURITE",
            title: "Make Every Outfit Count",
            description:
                "Discover standout looks and find fashion that fits your personality.",
            button: "Find Your Style",
            captionTitle: "Trending Styles",
            captionDescription: "Find your next favourite"
        }
    ];

    let currentIndex = 0;
    let timer = null;

    function showSlide(index) {
        if (slides.length === 0) {
            return;
        }

        currentIndex = (index + slides.length) % slides.length;

        slides.forEach((slide, i) => {
            slide.classList.toggle("active", i === currentIndex);
        });

        dots.forEach((dot, i) => {
            const active = i === currentIndex;

            dot.classList.toggle("active", active);

            if (active) {
                dot.setAttribute("aria-current", "true");
            } else {
                dot.removeAttribute("aria-current");
            }
        });

        const content = slideContent[currentIndex];

        label.textContent = content.label;
        title.textContent = content.title;
        description.textContent = content.description;
        buttonText.textContent = content.button;
        captionTitle.textContent = content.captionTitle;
        captionDescription.textContent = content.captionDescription;

        currentSlideText.textContent =
            String(currentIndex + 1).padStart(2, "0");

        title.classList.remove("hero-title-animate");
        void title.offsetWidth;
        title.classList.add("hero-title-animate");
    }

    function stopAutoPlay() {
        if (timer !== null) {
            window.clearInterval(timer);
            timer = null;
        }
    }

    function startAutoPlay() {
        stopAutoPlay();

        if (
            window.matchMedia("(prefers-reduced-motion: reduce)").matches ||
            slides.length < 2
        ) {
            return;
        }

        timer = window.setInterval(() => {
            if (!document.hidden && !hero.matches(":hover")) {
                showSlide(currentIndex + 1);
            }
        }, 5000);
    }

    previousButton.addEventListener("click", () => {
        showSlide(currentIndex - 1);
        startAutoPlay();
    });

    nextButton.addEventListener("click", () => {
        showSlide(currentIndex + 1);
        startAutoPlay();
    });

    dots.forEach((dot, index) => {
        dot.addEventListener("click", () => {
            showSlide(index);
            startAutoPlay();
        });
    });

    hero.addEventListener("mouseenter", stopAutoPlay);
    hero.addEventListener("mouseleave", startAutoPlay);

    document.addEventListener("visibilitychange", () => {
        if (document.hidden) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    });

    showSlide(0);
    startAutoPlay();
})();
</script>

</body>
</html>
