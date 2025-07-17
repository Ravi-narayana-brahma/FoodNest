package api;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.*;

@WebServlet("/vendorOrders")
public class VendorOrdersServlet extends HttpServlet {

    private Connection conn;

    @Override
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodnest", "root", "119191"
            );
            System.out.println("VendorOrdersServlet → DB connected: " + (conn != null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load orders for the vendor
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");

        HttpSession session = req.getSession(false);
        String vendorPhone = (session != null) ? (String) session.getAttribute("vendor_phone") : null;

        if (vendorPhone == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"error\":\"Vendor login required\"}");
            return;
        }

        String orderSql =
            "SELECT DISTINCT o.id, o.phone AS user_mobile, o.address, o.payment_method, o.order_time, o.status " +
            "FROM orders o JOIN order_items oi ON o.id = oi.order_id " +
            "WHERE oi.vendor_phone = ? ORDER BY o.order_time DESC";

        String itemsSql =
            "SELECT name, price, quantity FROM order_items WHERE order_id = ? AND vendor_phone = ?";

        try (PreparedStatement psOrder = conn.prepareStatement(orderSql);
             PreparedStatement psItems = conn.prepareStatement(itemsSql)) {

            psOrder.setString(1, vendorPhone);
            ResultSet rsOrder = psOrder.executeQuery();

            JSONArray orderList = new JSONArray();

            while (rsOrder.next()) {
                int orderId = rsOrder.getInt("id");

                JSONObject orderObj = new JSONObject()
                    .put("id", orderId)
                    .put("user_mobile", rsOrder.getString("user_mobile"))
                    .put("address", rsOrder.getString("address"))
                    .put("paymentMethod", rsOrder.getString("payment_method"))
                    .put("time", rsOrder.getString("order_time"))
                    .put("status", rsOrder.getString("status")); // 👈 Added status field

                psItems.setInt(1, orderId);
                psItems.setString(2, vendorPhone);
                ResultSet rsItems = psItems.executeQuery();

                JSONArray itemList = new JSONArray();
                while (rsItems.next()) {
                    itemList.put(new JSONObject()
                        .put("name", rsItems.getString("name"))
                        .put("price", rsItems.getDouble("price"))
                        .put("quantity", rsItems.getInt("quantity")));
                }

                orderObj.put("items", itemList);
                orderList.put(orderObj);
            }

            res.getWriter().write(orderList.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"DB error\"}");
        }
    }

    // Update status → Confirm order
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");

        HttpSession session = req.getSession(false);
        String vendorPhone = (session != null) ? (String) session.getAttribute("vendor_phone") : null;

        if (vendorPhone == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"error\":\"Vendor login required\"}");
            return;
        }

        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            JSONObject data = new JSONObject(sb.toString());
            int orderId = data.getInt("orderId");

            // Update order status to 'confirmed'
            String updateSql =
                "UPDATE orders SET status = 'confirmed' WHERE id = ? AND id IN " +
                "(SELECT order_id FROM order_items WHERE vendor_phone = ?)";

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, orderId);
                ps.setString(2, vendorPhone);

                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    res.getWriter().write("{\"message\":\"Order confirmed\"}");
                } else {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getWriter().write("{\"error\":\"Order not found or not authorized\"}");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"Failed to confirm order\"}");
        }
    }

    @Override
    public void destroy() {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
