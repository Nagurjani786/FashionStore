# FashionStore documentation

Jakarta Servlet MVC store: browse products, register/login, session cart, COD checkout.

| Document | Markdown | PDF |
|---|---|---|
| Architecture | [architecture.md](architecture.md) | [architecture.pdf](architecture.pdf) |
| Database | [database.md](database.md) | [database.pdf](database.pdf) |
| Controllers | [controllers.md](controllers.md) | [controllers.pdf](controllers.pdf) |
| This index | [README.md](README.md) | [README.pdf](README.pdf) |

Stack: Java 17, Jakarta Servlet 6, JSP, JDBC, MySQL, Maven WAR. There is **no service layer**. Servlets talk to DAO implementations, then forward to JSP under `WEB-INF/views`.
