package api;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.JSONObject;

@WebServlet("/vendorStats")
public class VendorStatsServlet extends HttpServlet {

    private Connection conn;

    @Override
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodnest", "root", "119191"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        res.setContentType("application/json");
        JSONObject json = new JSONObject();

        String vendorPhone = req.getParameter("vendor_phone");
        if (vendorPhone == null || vendorPhone.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("error", "Missing vendor_phone");
            res.getWriter().print(json.toString());
            return;
        }

        try {
            // Total Orders and Revenue
            String orderQuery = "SELECT COUNT(*) AS total_orders, SUM(o.total_amount) AS total_revenue " +
                                "FROM orders o JOIN order_items oi ON o.id = oi.order_id " +
                                "WHERE oi.vendor_phone = ?";
            PreparedStatement ps1 = conn.prepareStatement(orderQuery);
            ps1.setString(1, vendorPhone);
            ResultSet rs1 = ps1.executeQuery();

            int totalOrders = 0;
            int totalRevenue = 0;

            if (rs1.next()) {
                totalOrders = rs1.getInt("total_orders");
                totalRevenue = rs1.getInt("total_revenue");
            }

            // Active Items
            String itemQuery = "SELECT COUNT(*) AS active_items FROM menu_items WHERE vendor_phone = ?";
            PreparedStatement ps2 = conn.prepareStatement(itemQuery);
            ps2.setString(1, vendorPhone);
            ResultSet rs2 = ps2.executeQuery();

            int activeItems = 0;
            if (rs2.next()) {
                activeItems = rs2.getInt("active_items");
            }

            json.put("totalOrders", totalOrders);
            json.put("totalRevenue", totalRevenue);
            json.put("activeItems", activeItems);

            res.getWriter().print(json.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.put("error", "Database error");
            res.getWriter().print(json.toString());
        }
    }
}
