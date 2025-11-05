package client.ui;

import client.BudgetClient;
import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Registration window for new users.
 */
public class RegisterUI extends JFrame {

    private final BudgetClient client;
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton, backToLoginButton;

    public RegisterUI(BudgetClient client) {
        this.client = client;
        setTitle("Register - Personal Budget Tracker");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        // --- UI Components ---
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        registerButton = new JButton("Register");
        backToLoginButton = new JButton("Back to Login");

        add(registerButton);
        add(backToLoginButton);

        // --- Action Listeners ---
        registerButton.addActionListener(this::registerUser);
        backToLoginButton.addActionListener(e -> {
            dispose();
            new LoginUI(client).setVisible(true);
        });
    }

    /** Handles user registration */
    private void registerUser(ActionEvent e) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
                dispose();
                new LoginUI(client).setVisible(true);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
