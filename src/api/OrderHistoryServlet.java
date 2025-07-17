package api;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/orders")                     // ⇦ URL the JS fetches
public class OrderHistoryServlet extends HttpServlet {

    private Connection conn;               // kept for the life of the servlet

    @Override
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodnest",
                "root",
                "119191"
            );
            System.out.println("OrderHistoryServlet → DB connected: " + (conn != null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        res.setContentType("application/json; charset=UTF-8");
        HttpSession session = req.getSession(false);

        // ✅ use SAME key you set at login
        if (session == null || session.getAttribute("phone") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"error\":\"Login required\"}");
            return;
        }
        String phone = (String) session.getAttribute("phone");

        String orderSql =
            "SELECT id, address, payment_method, order_time, status " +
            "FROM orders WHERE phone = ? ORDER BY order_time DESC";

        String itemsSql =
            "SELECT name, price, quantity FROM order_items WHERE order_id = ?";

        try (PreparedStatement psOrder = conn.prepareStatement(orderSql);
             PreparedStatement psItems = conn.prepareStatement(itemsSql)) {

            psOrder.setString(1, phone);
            ResultSet rsOrder = psOrder.executeQuery();

            JSONArray orderList = new JSONArray();

            while (rsOrder.next()) {
                int orderId = rsOrder.getInt("id");

                JSONObject orderObj = new JSONObject()
                    .put("time", rsOrder.getString("order_time"))
                    .put("address", rsOrder.getString("address"))
                    .put("paymentMethod", rsOrder.getString("payment_method"))
                	.put("status", rsOrder.getString("status"));
                psItems.setInt(1, orderId);
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

    @Override
    public void destroy() {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
