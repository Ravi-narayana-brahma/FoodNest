---
```markdown
# 🍽️ FoodNest

**FoodNest** is a full-stack food ordering web application where customers can browse restaurants, add items to their cart, place orders, and view order history. Vendors can manage their menu, view and confirm orders. The project uses HTML/CSS/JavaScript on the frontend and Java Servlets with MySQL on the backend.

---

## 🌐 Features

### Customer
- Signup / Login with session management
- Browse restaurants and food categories
- Add items to cart and checkout
- Payment confirmation
- View order history
- Update profile

### Vendor
- Signup / Login
- View and update vendor profile
- Add or delete menu items
- View and confirm orders

---

## 🛠️ Tech Stack

| Layer        | Technologies Used                          |
|--------------|---------------------------------------------|
| Frontend     | HTML5, CSS3, JavaScript, jQuery             |
| Backend      | Java (Servlets, JSP), Apache Tomcat         |
| Database     | MySQL                                       |
| Tools        | Eclipse IDE, phpMyAdmin, Git, GitHub        |

---

## 📁 Project Structure

```

FoodNest/
│
├── WebContent/
│   ├── index.html              # Customer frontend
│   ├── vendor.html             # Vendor frontend
│   ├── css/
│   │   ├── style.css
│   │   └── styles.css
│   ├── js/
│   │   ├── script.js           # Customer JS
│   │   └── scripts.js          # Vendor JS
│   ├── images/                 # Assets
│   ├── categories.json         # Food categories
│   └── WEB-INF/
│       └── web.xml             # Deployment descriptor
│
├── src/
│   └── com.foodnest.api/
│       ├── AuthServlet.java            # Customer auth (login/signup/profile)
│       ├── CartServlet.java            # Cart logic (add/delete/view)
│       ├── OrderServlet.java           # Confirm order
│       ├── OrderHistoryServlet.java    # Customer order history
│       ├── VendorAuthServlet.java      # Vendor login/signup/profile
│       ├── VendorItemServlet.java      # Vendor menu item handling
│       └── VendorOrderServlet.java     # Vendor order view/confirm
│
└── README.md

````

---

## 🧠 Database Schema

### 1. `users` Table
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    phone VARCHAR(15) UNIQUE,
    email VARCHAR(100),
    password VARCHAR(100)
);
````

### 2. `vendors` Table

```sql
CREATE TABLE vendors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    vendor_phone VARCHAR(15) UNIQUE,
    email VARCHAR(100),
    restaurant_name VARCHAR(100),
    address TEXT,
    city VARCHAR(100),
    type VARCHAR(100),
    password VARCHAR(100)
);
```

### 3. `menu_items` Table

```sql
CREATE TABLE menu_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    vendor_phone VARCHAR(15),
    name VARCHAR(100),
    price DECIMAL(10,2),
    description TEXT
);
```

### 4. `orders` Table

```sql
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_mobile VARCHAR(15),
    vendor_phone VARCHAR(15),
    total_amount DECIMAL(10,2),
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 5. `order_items` Table

```sql
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    item_name VARCHAR(100),
    quantity INT,
    price DECIMAL(10,2)
);
```

---

## ▶️ How to Run

1. Clone the project:

   ```bash
   git clone https://github.com/yourusername/foodnest.git
   ```

2. Import into **Eclipse** as a Dynamic Web Project.

3. Configure **Tomcat Server** and **MySQL Database**.

4. Set database credentials in the servlet classes.

5. Deploy and run the application.

---

## 🙋‍♂️ Author

**Ravi Narayana Brahma**
Final Year B.Tech Student | Full Stack Developer
🌐 [Portfolio](https://your-portfolio-url.com) | 📧 [ravi@example.com](mailto:ravi@example.com)

---

## 📜 License

This project is licensed under the MIT License.

```

---

