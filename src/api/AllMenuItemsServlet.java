package api;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/allMenuItems")
public class AllMenuItemsServlet extends HttpServlet {
    private Connection conn;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodnest", "root", "119191");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT item_name, item_price, item_type, vendor_phone FROM vendor_menu_items");

            JSONArray items = new JSONArray();
            while (rs.next()) {
                JSONObject item = new JSONObject();
                item.put("name", rs.getString("item_name"));
                item.put("price", rs.getDouble("item_price"));
                item.put("type", rs.getString("item_type"));
                item.put("vendor_phone", rs.getString("vendor_phone"));  // ✅ include vendor_phone
                items.put(item);
            }

            out.print(items.toString());
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"Failed to fetch menu items\"}");
        }
    }
}
