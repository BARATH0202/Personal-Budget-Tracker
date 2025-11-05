package client.ui;

import client.BudgetClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Login page for the Budget Tracker
 */
public class LoginUI extends JFrame {

    private final BudgetClient client;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginUI(BudgetClient client) {
        this.client = client;
        setTitle("Login - Personal Budget Tracker");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        add(loginButton);
        add(registerButton);

        // Action listeners
        loginButton.addActionListener(this::loginAction);
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterUI(client).setVisible(true);
        });
    }

    private void loginAction(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (client.validateLogin(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            new BudgetTrackerUI(client).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
        }
    }
}
