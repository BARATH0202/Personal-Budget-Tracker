package client;

import database.DBConnection;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Handles client-side communication with the server and provides DB utility methods.
 */
public class BudgetClient {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public BudgetClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /** Connect to the server */
    public void connect() throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connected to server at " + host + ":" + port);
    }

    /** Send a message to the server */
    public void send(String message) {
        out.println(message);
    }

    /** Receive a message from the server */
    public String receive() throws IOException {
        return in.readLine();
    }

    /** Close all connections */
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ LOGIN VALIDATION METHOD
    public boolean validateLogin(String username, String password) {
        boolean isValid = false;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM users WHERE username = ? AND password = ?")) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    isValid = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }
}
