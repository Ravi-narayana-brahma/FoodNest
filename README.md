
# 🍽️ FoodNest – Online Food Ordering Web App

**FoodNest** is a full-stack food ordering system designed for both customers and restaurant vendors. It supports user authentication, restaurant browsing, dynamic cart handling, and order tracking with a backend powered by Java Servlets and a MySQL database.

---

## 🔧 Tech Stack

- **Frontend:** HTML5, CSS3, JavaScript
- **Backend:** Java Servlets (JSP-free), JDBC
- **Database:** MySQL
- **Tools:** Eclipse IDE, Apache Tomcat, phpMyAdmin

---

## 👤 User Features

- 🔐 Signup/Login with session management
- 🍽️ Browse restaurants and view menu
- 🛒 Add to cart with quantity update
- ✅ Place order and track order history
- 👤 Profile view and edit options

---

## 🧑‍🍳 Vendor Features

- 🔐 Vendor authentication (signup/login)
- 🍱 Add, edit, delete menu items
- 📦 View and confirm customer orders
- 👤 Manage vendor profile

---

## 🗃️ Database Tables

### `users` Table
| Column        | Type     |
|---------------|----------|
| id            | INT (PK) |
| first_name    | VARCHAR  |
| last_name     | VARCHAR  |
| phone         | VARCHAR  |
| email         | VARCHAR  |
| password      | VARCHAR  |

### `vendors` Table
| Column         | Type     |
|----------------|----------|
| id             | INT (PK) |
| first_name     | VARCHAR  |
| last_name      | VARCHAR  |
| vendor_phone   | VARCHAR  |
| email          | VARCHAR  |
| restaurant_name| VARCHAR  |
| address        | TEXT     |
| city           | VARCHAR  |
| type           | VARCHAR  |
| password       | VARCHAR  |

### `items` Table
| Column        | Type     |
|---------------|----------|
| id            | INT (PK) |
| name          | VARCHAR  |
| description   | TEXT     |
| price         | DECIMAL  |
| image_url     | TEXT     |
| vendor_phone  | VARCHAR  |

### `orders` Table
| Column        | Type     |
|---------------|----------|
| id            | INT (PK) |
| user_mobile   | VARCHAR  |
| total_price   | DECIMAL  |
| order_time    | TIMESTAMP|

### `order_items` Table
| Column     | Type     |
|------------|----------|
| id         | INT (PK) |
| order_id   | INT (FK) |
| item_id    | INT (FK) |
| quantity   | INT      |

---

## 🚀 How to Run

1. Clone the repo
2. Import project into Eclipse as Dynamic Web Project
3. Configure Tomcat Server
4. Import the SQL file into MySQL (`foodnest` DB)
5. Update DB credentials in your Java files (if needed)
6. Run on server and access via browser

---

## 📂 Folder Structure



FoodNest/
├── src/
│   └── com.foodnest.api/
│       ├── AuthServlet.java
│       ├── CartServlet.java
│       ├── OrderServlet.java
│       └── VendorAuthServlet.java
├── WebContent/
│   ├── index.html
│   ├── vendor.html
│   ├── styles.css
│   ├── scripts.js
│   ├── categories.json
│   └── WEB-INF/
│       └── web.xml



---

## ✨ Highlights

- Clean UI for both users and vendors
- Modular Servlet architecture
- Uses only Servlets (no JSP)
- Session-based auth and DB integration
- Mobile-responsive frontend

---

## 🙋‍♂️ Author

**Ravi Narayana Brahma**  

---

