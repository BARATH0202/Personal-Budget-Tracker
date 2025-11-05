package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/budget_tracker?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String pass = "Barath0011#";

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver Registered Successfully.");

            // Try to connect
            Connection conn = DriverManager.getConnection(url, user, pass);
            if (conn != null) {
                System.out.println("✅ Database Connection Successful!");
                conn.close();
            } else {
                System.out.println("❌ Failed to make connection!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
