# Architecture

FashionStore is a **Jakarta Servlet MVC** WAR. HTTP hits annotated servlets (Controller). Servlets load POJOs via JDBC DAOs (Model) and forward to JSP (View). There is no Spring, no REST API, and no service/business layer between controllers and DAOs.

## Package layout

```
com.fashionstore
├── controller   HttpServlet classes (@WebServlet)
├── model        Table-aligned POJOs
├── dao          DAO interfaces
├── dao.impl     JDBC implementations
└── util         DBConnection, AllDAOTest
```

Views live in `src/main/webapp/WEB-INF/views/` (not URL-addressable). Static CSS/images are under `src/main/webapp/assets/`. Welcome file `index.html` is listed in `web.xml`; app URLs are servlet mappings.

## MVC

```mermaid
flowchart TB
    Browser[Browser]
    Servlet[Controller: HttpServlet]
    JSP[View: JSP]
    DAO[DAO interface]
    Impl[dao.impl JDBC]
    DB[(MySQL fashion_store)]
    Session[HttpSession]

    Browser -->|GET/POST| Servlet
    Servlet -->|setAttribute / forward| JSP
    JSP -->|HTML| Browser
    Servlet -->|read/write cart, user, lastOrder| Session
    Servlet --> DAO
    DAO --> Impl
    Impl -->|DBConnection.getConnection| DB
```

Request path:

1. Browser calls a mapped URL (`/home`, `/products`, `/cart`, …).
2. Servlet parses parameters and session.
3. Servlet calls one or more DAO methods (or only session, for cart display).
4. Servlet sets request attributes and **forwards** to a JSP, or **redirects** after a mutating POST.

Session holds: `user`, `userId`, `cart` (`Map<String, CartItem>` keyed by `productId-variantId`), `lastOrder`, `lastOrderItems`.

## Layer class diagram

```mermaid
classDiagram
    class HttpServlet
    HomeServlet --|> HttpServlet
    ProductsServlet --|> HttpServlet
    ProductDetailsServlet --|> HttpServlet
    LoginServlet --|> HttpServlet
    RegisterServlet --|> HttpServlet
    CartServlet --|> HttpServlet
    CheckoutServlet --|> HttpServlet

    HomeServlet --> CategoryDAO
    HomeServlet --> ProductDAO
    ProductsServlet --> CategoryDAO
    ProductsServlet --> ProductDAO
    ProductDetailsServlet --> ProductDAO
    ProductDetailsServlet --> ProductVariantDAO
    LoginServlet --> UserDAO
    RegisterServlet --> UserDAO
    CartServlet --> ProductDAO
    CartServlet --> ProductVariantDAO
    CheckoutServlet --> OrderDAO
    CheckoutServlet --> ProductVariantDAO

    UserDAO <|.. UserDAOImpl
    CategoryDAO <|.. CategoryDAOImpl
    ProductDAO <|.. ProductDAOImpl
    ProductVariantDAO <|.. ProductVariantDAOImpl
    CartDAO <|.. CartDAOImpl
    CartItemDAO <|.. CartItemDAOImpl
    OrderDAO <|.. OrderDAOImpl
    OrderItemDAO <|.. OrderItemDAOImpl

    UserDAOImpl ..> User
    ProductDAOImpl ..> Product
    CategoryDAOImpl ..> Category
    ProductVariantDAOImpl ..> ProductVariant
    CartDAOImpl ..> Cart
    CartItemDAOImpl ..> CartItem
    OrderDAOImpl ..> Order
    OrderItemDAOImpl ..> OrderItem

    UserDAOImpl ..> DBConnection
    ProductDAOImpl ..> DBConnection
    OrderDAOImpl ..> DBConnection
```

`CartDAO` / `CartItemDAO` / `OrderItemDAO` exist and are tested in `AllDAOTest`, but **no servlet currently injects them**. Live cart is session-only. Checkout writes the `orders` row only; line items stay in session as `lastOrderItems`.

## Domain class diagram

```mermaid
classDiagram
    class User {
        int userId
        String fullName
        String email
        String password
        String phone
        String address
        String city
        String state
        String postalCode
        Timestamp createdAt
    }
    class Category {
        int categoryId
        String categoryName
        String description
    }
    class Product {
        int productId
        int categoryId
        String productName
        String brand
        String color
        double price
        String imageUrl
    }
    class ProductVariant {
        int variantId
        int productId
        String size
        int stockQuantity
    }
    class Cart {
        int cartId
        int userId
    }
    class CartItem {
        int cartItemId
        int cartId
        int productId
        int variantId
        String productName
        BigDecimal price
        int quantity
        BigDecimal getSubtotal()
    }
    class Order {
        int orderId
        int userId
        BigDecimal totalAmount
        String orderStatus
        String paymentMethod
        String deliveryName
        String phone
    }
    class OrderItem {
        int orderItemId
        int orderId
        int variantId
        String productName
        BigDecimal unitPrice
        int quantity
        BigDecimal subtotal
    }

    Category "1" --> "*" Product : categoryId
    Product "1" --> "*" ProductVariant : productId
    User "1" --> "0..1" Cart : userId
    Cart "1" --> "*" CartItem : cartId
    ProductVariant "1" --> "*" CartItem : variantId
    User "1" --> "*" Order : userId
    Order "1" --> "*" OrderItem : orderId
    ProductVariant "1" --> "*" OrderItem : variantId
```

`CartItem` used by the UI also carries display fields (`productName`, `brand`, `imageUrl`, `size`, `price`) that are **not** columns on `cart_items`. Those come from `Product` + `ProductVariant` when adding to the session cart.

---

## Sequence diagrams (live flows)

### Home — `GET /home`

```mermaid
sequenceDiagram
    actor User
    participant HomeServlet
    participant CategoryDAOImpl
    participant ProductDAOImpl
    participant home_jsp as home.jsp
    participant DB as MySQL

    User->>HomeServlet: GET /home
    HomeServlet->>CategoryDAOImpl: getAllCategories()
    CategoryDAOImpl->>DB: SELECT * FROM categories
    HomeServlet->>ProductDAOImpl: getLatestProducts(8)
    ProductDAOImpl->>DB: SELECT * FROM products ORDER BY created_at DESC LIMIT 8
    HomeServlet->>home_jsp: forward categories, latestProducts
    home_jsp-->>User: homepage HTML
```

### Catalog — `GET /products`

Query params: `keyword`, `categoryId`, `minPrice`, `maxPrice`, `sortBy`. Brand/color/size filters exist on the DAO but the servlet always passes `null` for those. Pagination is hard-coded to page 1, size 100.

```mermaid
sequenceDiagram
    actor User
    participant ProductsServlet
    participant CategoryDAOImpl
    participant ProductDAOImpl
    participant products_jsp as products.jsp
    participant DB as MySQL

    User->>ProductsServlet: GET /products?...
    ProductsServlet->>ProductsServlet: parse filters, reject min > max
    ProductsServlet->>CategoryDAOImpl: getAllCategories()
    CategoryDAOImpl->>DB: SELECT * FROM categories
    ProductsServlet->>ProductDAOImpl: getFilteredProducts(...)
    ProductDAOImpl->>DB: SELECT DISTINCT p.* FROM products p WHERE ...
    ProductsServlet->>ProductDAOImpl: countFilteredProducts(...)
    ProductDAOImpl->>DB: COUNT(DISTINCT p.product_id)
    ProductsServlet->>products_jsp: forward products, categories, filter state
    products_jsp-->>User: catalog HTML
```

Navbar search is the same servlet: `GET /products?keyword=...`.

### Product details — `GET /product?id=`

```mermaid
sequenceDiagram
    actor User
    participant ProductDetailsServlet
    participant ProductDAOImpl
    participant ProductVariantDAOImpl
    participant details as product-details.jsp
    participant DB as MySQL

    User->>ProductDetailsServlet: GET /product?id={id}
    alt missing id
        ProductDetailsServlet-->>User: redirect /products
    else invalid id
        ProductDetailsServlet-->>User: 400
    else product missing
        ProductDetailsServlet->>ProductDAOImpl: getProductById
        ProductDAOImpl->>DB: SELECT * FROM products WHERE product_id=?
        ProductDetailsServlet-->>User: 404
    else found
        ProductDetailsServlet->>ProductDAOImpl: getProductById
        ProductDAOImpl->>DB: SELECT * FROM products WHERE product_id=?
        ProductDetailsServlet->>ProductVariantDAOImpl: getVariantsByProductId
        ProductVariantDAOImpl->>DB: SELECT * FROM product_variants WHERE product_id=?
        ProductDetailsServlet->>details: forward product, variants
        details-->>User: details + size picker
    end
```

`getRelatedProducts` is on `ProductDAO` but is **not** called here.

### Register — `GET/POST /register`

```mermaid
sequenceDiagram
    actor User
    participant RegisterServlet
    participant UserDAOImpl
    participant register_jsp as register.jsp
    participant DB as MySQL

    User->>RegisterServlet: GET /register
    RegisterServlet->>register_jsp: forward
    register_jsp-->>User: form

    User->>RegisterServlet: POST fullName, email, password, ...
    RegisterServlet->>RegisterServlet: validate required, email, min 8 chars, confirm
    RegisterServlet->>UserDAOImpl: emailExists(email)
    UserDAOImpl->>DB: SELECT 1 FROM users WHERE email=?
    opt phone provided
        RegisterServlet->>UserDAOImpl: phoneExists(phone)
        UserDAOImpl->>DB: SELECT 1 FROM users WHERE phone=?
    end
    RegisterServlet->>UserDAOImpl: registerUser(user)
    UserDAOImpl->>UserDAOImpl: PBKDF2 hash password
    UserDAOImpl->>DB: INSERT INTO users (...)
    alt success
        RegisterServlet-->>User: redirect /login?registered=true
    else fail or duplicate
        RegisterServlet->>register_jsp: forward error
    end
```

### Login — `GET/POST /login`

```mermaid
sequenceDiagram
    actor User
    participant LoginServlet
    participant UserDAOImpl
    participant Session as HttpSession
    participant login_jsp as login.jsp
    participant DB as MySQL

    User->>LoginServlet: GET /login
    LoginServlet->>login_jsp: forward
    login_jsp-->>User: form

    User->>LoginServlet: POST email, password
    LoginServlet->>UserDAOImpl: loginUser(email, password)
    UserDAOImpl->>DB: SELECT * FROM users WHERE email=?
    UserDAOImpl->>UserDAOImpl: verify PBKDF2 hash
    alt invalid
        LoginServlet->>login_jsp: forward error
    else valid
        LoginServlet->>Session: invalidate old session
        LoginServlet->>Session: set user, userId (30 min)
        LoginServlet-->>User: redirect /home
    end
```

There is **no logout servlet**. Navbar always links to `/login`.

### View cart — `GET /cart`

No DAO. Cart is a `LinkedHashMap` in session.

```mermaid
sequenceDiagram
    actor User
    participant CartServlet
    participant Session as HttpSession
    participant cart_jsp as cart.jsp

    User->>CartServlet: GET /cart
    CartServlet->>Session: get/create cart map
    CartServlet->>CartServlet: sum CartItem.getSubtotal()
    CartServlet->>cart_jsp: forward cartItems, cartTotal
    cart_jsp-->>User: cart HTML
```

### Add to cart — `POST /cart` `action=add`

```mermaid
sequenceDiagram
    actor User
    participant CartServlet
    participant ProductDAOImpl
    participant ProductVariantDAOImpl
    participant Session as HttpSession
    participant DB as MySQL

    User->>CartServlet: POST productId, variantId, quantity
    CartServlet->>ProductDAOImpl: getProductById
    ProductDAOImpl->>DB: SELECT * FROM products WHERE product_id=?
    CartServlet->>ProductVariantDAOImpl: getVariantById
    ProductVariantDAOImpl->>DB: SELECT * FROM product_variants WHERE variant_id=?
    alt product/variant mismatch
        CartServlet-->>User: redirect /product?id=...&error=invalid
    else
        CartServlet->>ProductVariantDAOImpl: isStockAvailable(variantId, newQty)
        ProductVariantDAOImpl->>DB: SELECT stock_quantity
        alt insufficient stock
            CartServlet-->>User: redirect ...&error=stock
        else
            CartServlet->>Session: put/update Map cart[productId-variantId]
            CartServlet-->>User: redirect /cart?success=added
        end
    end
```

### Update quantity — `POST /cart` `action=update`

```mermaid
sequenceDiagram
    actor User
    participant CartServlet
    participant ProductVariantDAOImpl
    participant Session as HttpSession
    participant DB as MySQL

    User->>CartServlet: POST variantId, quantity
    CartServlet->>Session: find CartItem by variantId
    CartServlet->>ProductVariantDAOImpl: isStockAvailable
    ProductVariantDAOImpl->>DB: SELECT stock_quantity
    alt ok
        CartServlet->>Session: setQuantity
        CartServlet-->>User: redirect /cart?success=updated
    else stock
        CartServlet-->>User: redirect /cart?error=stock
    end
```

### Remove line — `POST /cart` `action=remove`

```mermaid
sequenceDiagram
    actor User
    participant CartServlet
    participant Session as HttpSession

    User->>CartServlet: POST variantId
    CartServlet->>Session: remove entries matching variantId
    CartServlet-->>User: redirect /cart?success=removed
```

Stock is **checked**, never decremented, on add/update.

### Checkout form — `GET /checkout`

```mermaid
sequenceDiagram
    actor User
    participant CheckoutServlet
    participant Session as HttpSession
    participant checkout_jsp as checkout.jsp

    User->>CheckoutServlet: GET /checkout
    CheckoutServlet->>Session: user (or loggedInUser)
    alt not logged in
        CheckoutServlet-->>User: redirect /login
    else empty cart
        CheckoutServlet-->>User: redirect /cart
    else
        CheckoutServlet->>checkout_jsp: forward checkoutItems, checkoutTotal, checkoutUser
        checkout_jsp-->>User: delivery + COD form
    end
```

### Place order — `POST /checkout`

```mermaid
sequenceDiagram
    actor User
    participant CheckoutServlet
    participant ProductVariantDAOImpl
    participant OrderDAOImpl
    participant Session as HttpSession
    participant DB as MySQL

    User->>CheckoutServlet: POST delivery fields, paymentMethod=COD
    CheckoutServlet->>Session: require User + non-empty cart
    CheckoutServlet->>CheckoutServlet: validate address, 6-digit PIN, 10-digit phone, COD
    loop each CartItem
        CheckoutServlet->>ProductVariantDAOImpl: isStockAvailable
        ProductVariantDAOImpl->>DB: SELECT stock_quantity
    end
    CheckoutServlet->>OrderDAOImpl: placeOrder(order PENDING, Cash on Delivery)
    OrderDAOImpl->>DB: INSERT INTO orders (...)
    Note over CheckoutServlet,DB: OrderItemDAO is not called. Stock is not reduced.
    CheckoutServlet->>Session: lastOrder, lastOrderItems snapshot
    CheckoutServlet->>Session: remove cart
    CheckoutServlet-->>User: redirect /checkout/confirmation
```

### Confirmation — `GET /checkout/confirmation`

```mermaid
sequenceDiagram
    actor User
    participant CheckoutServlet
    participant Session as HttpSession
    participant conf as order-confirmation.jsp

    User->>CheckoutServlet: GET /checkout/confirmation
    CheckoutServlet->>Session: lastOrder
    alt missing
        CheckoutServlet-->>User: redirect /cart
    else
        CheckoutServlet->>conf: forward order
        conf->>Session: lastOrderItems
        conf-->>User: confirmation HTML
    end
```

---

## URL → view

| URL | Servlet | View |
|---|---|---|
| `GET /home` | `HomeServlet` | `home.jsp` |
| `GET /products` | `ProductsServlet` | `products.jsp` |
| `GET /product` | `ProductDetailsServlet` | `product-details.jsp` |
| `GET/POST /login` | `LoginServlet` | `login.jsp` |
| `GET/POST /register` | `RegisterServlet` | `register.jsp` |
| `GET/POST /cart` | `CartServlet` | `cart.jsp` |
| `GET/POST /checkout` | `CheckoutServlet` | `checkout.jsp` |
| `GET /checkout/confirmation` | `CheckoutServlet` | `order-confirmation.jsp` |

Shared fragments: `partials/navbar.jsp`, `partials/footer.jsp`.
