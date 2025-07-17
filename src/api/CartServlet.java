package api;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class CartServlet extends HttpServlet {

    private Connection conn;

    @Override
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodnest",
                "root",
                "119191"
            );
            System.out.println("DB connected?  →  " + (conn != null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("phone") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"error\":\"Not logged in\"}");
            return;
        }

        String phone = (String) session.getAttribute("phone");

        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT name, price, image, quantity FROM cart_items WHERE phone = ?"
            );
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            JSONArray cart = new JSONArray();
            while (rs.next()) {
                JSONObject item = new JSONObject();
                item.put("name", rs.getString("name"));
                item.put("price", rs.getDouble("price"));
                item.put("image", rs.getString("image"));
                item.put("quantity", rs.getInt("quantity"));
                cart.put(item);
            }

            res.getWriter().write(cart.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"Database error\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("phone") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Login required");
            return;
        }

        String phone = (String) session.getAttribute("phone");
        String name = req.getParameter("name");
        String image = req.getParameter("image");
        String vendorPhone = req.getParameter("vendor_phone");  // ✅ NEW
        double price = Double.parseDouble(req.getParameter("price"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        try {
            PreparedStatement check = conn.prepareStatement(
                "SELECT quantity FROM cart_items WHERE phone = ? AND name = ?"
            );
            check.setString(1, phone);
            check.setString(2, name);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                PreparedStatement update = conn.prepareStatement(
                    "UPDATE cart_items SET quantity = quantity + ? WHERE phone = ? AND name = ?"
                );
                update.setInt(1, quantity);
                update.setString(2, phone);
                update.setString(3, name);
                update.executeUpdate();
            } else {
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO cart_items (phone, name, price, image, quantity, vendor_phone) VALUES (?, ?, ?, ?, ?, ?)"
                );
                insert.setString(1, phone);
                insert.setString(2, name);
                insert.setDouble(3, price);
                insert.setString(4, image);
                insert.setInt(5, quantity);
                insert.setString(6, vendorPhone);  // ✅ NEW
                insert.executeUpdate();
            }

            res.setContentType("text/plain");
            res.getWriter().write("✅ Added to cart");

        } catch (SQLException e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("Database error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("phone") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Login required");
            return;
        }

        String phone = (String) session.getAttribute("phone");

        try {
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM cart_items WHERE phone = ?"
            );
            ps.setString(1, phone);
            ps.executeUpdate();

            res.setContentType("text/plain");
            res.getWriter().write("🧹 Cart cleared");

        } catch (SQLException e) {
            e.printStackTrace();
            res.setStatus(500);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    @Override
    public void destroy() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
