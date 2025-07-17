package api;

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private Connection conn;

    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodnest",
                "root",
                "119191"
            );
            System.out.println("DB connected?  →  " + (conn != null));   // <-- add
        } catch (Exception e) {
            e.printStackTrace();                 // <-- will show exact reason
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	String action = req.getParameter("action");
    	System.out.println("🟡 Received action: " + action);  // Add this line
        res.setContentType("text/plain");

        /* ---------- SIGNUP ---------- */
        if ("signup".equals(action)) {
            String firstName = req.getParameter("first-name");
            String lastName = req.getParameter("last-name");
            String phone = req.getParameter("phone");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirm-password");

            if (!password.equals(confirmPassword)) {
                res.getWriter().write("❌ Passwords do not match");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (first_name, last_name, phone, password) VALUES (?, ?, ?, ?)")) {
                
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, phone);
                ps.setString(4, password);
                ps.executeUpdate();
                
                res.getWriter().write("✅ Signup successful! You can login now.");

            } catch (SQLIntegrityConstraintViolationException e) {
                res.getWriter().write("⚠️ Phone number already registered.");
            } catch (Exception e) {
                e.printStackTrace();  // 👈 This prints the actual error to server logs
                res.getWriter().write("❌ Signup failed (server error).");
                System.out.println("👉 Received action: " + req.getParameter("action"));
            }
        }
        /* ---------- LOGIN ---------- */
        else if ("login".equals(action)) {
            String phone = req.getParameter("username");
            String password = req.getParameter("password");

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE phone = ? AND password = ?")) {
                ps.setString(1, phone);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    HttpSession session = req.getSession();
                    String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                    session.setAttribute("user", fullName);
                    session.setAttribute("phone", phone);
                    res.getWriter().write("✅ Login success");
                } else {
                    res.getWriter().write("❌ Invalid phone or password");
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.getWriter().write("❌ Login failed (server error).");
            }
        }
        /* ---------- PROFILE (read) ---------- */
        else if ("profile".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("phone") == null) {
                res.getWriter().write("{\"error\":\"Not logged in\"}");
                return;
            }
            String phone = (String) session.getAttribute("phone");

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT first_name, last_name, phone, address FROM users WHERE phone = ?")) {
                ps.setString(1, phone);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                    String address  = rs.getString("address");

                    res.getWriter().write("{"
                        + "\"name\":\"" + fullName + "\","
                        + "\"mobile\":\"" + phone + "\","
                        + "\"address\":\"" + (address != null ? address : "") + "\""
                        + "}");
                } else {
                    res.getWriter().write("{\"error\":\"User not found\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.getWriter().write("{\"error\":\"Server error\"}");
            }
        }

        /* ---------- PROFILE (update – optional) ---------- */
        else if ("profileUpdate".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("phone") == null) {
                res.getWriter().write("❌ Not logged in");
                return;
            }
            String phone   = (String) session.getAttribute("phone");
            String address = req.getParameter("address");   // you could accept name etc. too

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET address = ? WHERE phone = ?")) {
                ps.setString(1, address);
                ps.setString(2, phone);
                int rows = ps.executeUpdate();
                res.getWriter().write(rows > 0 ? "✅ Profile updated" : "❌ Update failed");
            } catch (Exception e) {
                e.printStackTrace();
                res.getWriter().write("❌ Server error");
            }
        }

        /* ---------- LOGOUT ---------- */
        else if ("logout".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // ✅ Kill cookie (set same path)
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setMaxAge(0);
            cookie.setPath(req.getContextPath()); // typically "/FoodNest"
            res.addCookie(cookie);

            res.getWriter().write("✅ Logged out successfully");
        }



        /* ---------- Unknown Action ---------- */
        else {
        	res.getWriter().write("❌ Unknown action: " + action);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);  // Don't create new session
        res.setContentType("application/json");
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        if (session == null || session.getAttribute("phone") == null) {
            res.getWriter().write("{\"error\": \"Not logged in\"}");
            return;
        }

        String phone = (String) session.getAttribute("phone");

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT first_name, last_name, address FROM users WHERE phone = ?")) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                String address = rs.getString("address");

                res.getWriter().write("{"
                    + "\"name\": \"" + fullName + "\","
                    + "\"mobile\": \"" + phone + "\","
                    + "\"address\": \"" + (address != null ? address : "") + "\""
                    + "}");
            } else {
                res.getWriter().write("{\"error\": \"User not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().write("{\"error\": \"Server error\"}");
        }
    }

    public void destroy() {
        try {
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
