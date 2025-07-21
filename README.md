---

```markdown
# 🍽️ FoodNest – Online Food Ordering Web App

**FoodNest** is a full-stack food ordering system designed for both customers and restaurant vendors. It supports user authentication, restaurant browsing, dynamic cart handling, and order tracking with a backend powered by Java Servlets and a MySQL database.

---

## 🎯 Objectives

- Enable customers to browse restaurants and place orders.
- Allow vendors to manage menus and view/confirm orders.
- Build a full-stack web application with session-based authentication.
- Practice real-world Java Servlet + MySQL backend integration.

---

## 🔥 Features

### 👨‍🍳 Customer Module
- ✅ Login / Signup
- 🧾 View restaurants & categories
- 🛒 Add items to cart, checkout, and place order
- 💳 Payment confirmation simulation
- 📜 Order history
- 👤 Profile view and update

### 🏪 Vendor Module
- ✅ Vendor Login / Signup
- 📝 Add, delete menu items
- 📦 View and confirm incoming orders
- 👤 Update vendor profile

---

## 💻 Tech Stack

| Layer        | Technologies Used                           |
|--------------|---------------------------------------------|
| Frontend     | HTML5, CSS3, JavaScript                     |
| Backend      | Java Servlets (Eclipse IDE, Apache Tomcat)  |
| Database     | MySQL                                       |
| Others       | Git, GitHub                                 |

---

## 📂 Directory Structure

```

FoodNest/
├── WebContent/
│   ├── index.html              # Customer interface
│   ├── vendor.html             # Vendor interface
│   ├── css/
│   │   └── styles.css
│   ├── js/
│   │   ├── script.js           # Customer scripts
│   │   └── scripts.js          # Vendor scripts
│   ├── images/                 # Logos, icons, food images
│   ├── categories.json         # Sample food categories
│   └── WEB-INF/
│       └── web.xml             # Servlet mappings
│
├── src/
│   └── com.foodnest.api/
│       ├── AuthServlet.java
│       ├── CartServlet.java
│       ├── OrderServlet.java
│       ├── OrderHistoryServlet.java
│       ├── VendorAuthServlet.java
│       ├── VendorItemServlet.java
│       └── VendorOrderServlet.java
│
└── README.md

````

---

## 🧑‍💻 Setup Instructions

### 📦 Prerequisites
- Java JDK 8 or higher
- Eclipse IDE (Dynamic Web Project)
- Apache Tomcat 9+
- MySQL (XAMPP/WAMP or standalone)
- Web browser

### 🚀 Steps
1. **Clone the repo**:
   ```bash
   git clone https://github.com/yourusername/FoodNest.git
````

2. **Import into Eclipse** as a Dynamic Web Project.

3. **Configure Tomcat Server** in Eclipse.

4. **Create the MySQL Database**:

   ```sql
   CREATE DATABASE foodnest;
   ```

5. **Create required tables** using the schema in this README.

6. **Update DB credentials** in all Servlets (e.g., `Connection conn = DriverManager.getConnection(...)`).

7. **Run the application** on Tomcat and access:

   * `http://localhost:8080/FoodNest/index.html`
   * `http://localhost:8080/FoodNest/vendor.html`

---

## 🛠️ API Endpoints Summary

| Endpoint        | Method | Description                   |
| --------------- | ------ | ----------------------------- |
| `/auth`         | POST   | Customer login/signup/profile |
| `/vendorAuth`   | POST   | Vendor login/signup/profile   |
| `/cart`         | POST   | Add/view/delete customer cart |
| `/order`        | POST   | Confirm customer order        |
| `/orders`       | GET    | Customer order history        |
| `/vendorItems`  | POST   | Add/delete vendor menu items  |
| `/vendorOrders` | GET    | Vendor view/confirm orders    |

---

## ⚠️ Challenges Faced

* Session management across multiple modules
* Data sync between frontend (localStorage) and backend (SQL)
* Responsive UI for both vendors and customers
* Handling multiple concurrent orders

---

## 🚀 Future Enhancements

* ✅ OTP verification via mobile/email
* 🔒 Password encryption using hashing
* 📲 Mobile-responsive UI
* 📦 Admin dashboard for platform overview
* 💬 Real-time order status updates with WebSockets
* 📱 Convert to Android/iOS app (using Flutter or React Native)

---

## 🙋‍♂️ About Me

**Ravi Narayana Brahma**
👨‍🎓 Final Year B.Tech Student | Full Stack Web Developer
💻 Specializing in Java, SQL, Servlets, HTML, CSS, JS


---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---

## 📜 License

This project is licensed under the MIT License.
Feel free to use and modify as needed.

```

---



