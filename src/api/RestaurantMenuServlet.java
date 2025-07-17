package api;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/restaurantMenu") // ✅ Servlet URL to match the fetch call
public class RestaurantMenuServlet extends HttpServlet {
    private Connection conn;

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ✅ Use modern driver class
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodnest", "root", "119191");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String vendorPhone = request.getParameter("vendor_phone");

        if (vendorPhone == null || vendorPhone.trim().isEmpty()) {
            out.print("{\"error\":\"Missing vendor_phone parameter\"}");
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT item_name, item_price FROM vendor_menu_items WHERE vendor_phone = ?"
            );
            stmt.setString(1, vendorPhone);
            ResultSet rs = stmt.executeQuery();

            JSONArray menu = new JSONArray();
            while (rs.next()) {
                JSONObject item = new JSONObject();
                item.put("name", rs.getString("item_name"));
                item.put("price", rs.getDouble("item_price"));
                menu.put(item);
            }

            out.print(menu.toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"Failed to fetch menu items\"}");
        }
    }
}
