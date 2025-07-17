package api;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.JSONObject;

public class OrderServlet extends HttpServlet {

    private Connection conn;

    @Override
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodnest", "root", "119191");
            System.out.println("OrderServlet → DB connected: " + (conn != null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");          // ← return JSON every time

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("phone") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write(new JSONObject()
                    .put("success", false)
                    .put("message", "Login required").toString());
            return;
        }

        String phone  = (String) session.getAttribute("phone");
        String address       = req.getParameter("address");
        String paymentMethod = req.getParameter("paymentMethod");

        if (address == null || address.trim().isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(new JSONObject()
                    .put("success", false)
                    .put("message", "Address is required").toString());
            return;
        }

        try {
            conn.setAutoCommit(false);

            // 1️⃣ insert into orders
            PreparedStatement psOrder = conn.prepareStatement(
            	    "INSERT INTO orders (phone, address, payment_method, status) VALUES (?,?,?,?)",
            	    Statement.RETURN_GENERATED_KEYS);
            	psOrder.setString(1, phone);
            	psOrder.setString(2, address);
            	psOrder.setString(3, paymentMethod);
            	psOrder.setString(4, "Pending");  // 🔁 default status
            psOrder.executeUpdate();

            ResultSet keys = psOrder.getGeneratedKeys();
            keys.next();
            int orderId = keys.getInt(1);

            // 2️⃣ copy cart items into order_items
            PreparedStatement psCart = conn.prepareStatement(
            	    "SELECT name, price, image, quantity, vendor_phone FROM cart_items WHERE phone = ?"
            	);
            psCart.setString(1, phone);
            ResultSet cart = psCart.executeQuery();

            PreparedStatement psItem = conn.prepareStatement(
            	    "INSERT INTO order_items (order_id, name, price, image, quantity, vendor_phone) VALUES (?,?,?,?,?,?)");

            	while (cart.next()) {
            	    psItem.setInt(1, orderId);
            	    psItem.setString(2, cart.getString("name"));
            	    psItem.setDouble(3, cart.getDouble("price"));
            	    psItem.setString(4, cart.getString("image"));
            	    psItem.setInt(5, cart.getInt("quantity"));
            	    psItem.setString(6, cart.getString("vendor_phone"));  // ✅ fetch from cart DB
            	    psItem.addBatch();
            	}
            psItem.executeBatch();

            // 3️⃣ clear cart
            try (PreparedStatement clear = conn.prepareStatement(
                    "DELETE FROM cart_items WHERE phone = ?")) {
                clear.setString(1, phone);
                clear.executeUpdate();
            }

            conn.commit();

            res.getWriter().write(new JSONObject()
                    .put("success", true)
                    .put("message", "Order placed successfully").toString());

        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (SQLException ignored) {}
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write(new JSONObject()
                    .put("success", false)
                    .put("message", "Database error").toString());
        }
    }

    @Override
    public void destroy() {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
