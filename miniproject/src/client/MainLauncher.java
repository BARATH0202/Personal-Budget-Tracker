package client;

import client.ui.LoginUI;

import javax.swing.*;

public class MainLauncher {
    public static void main(String[] args) {
        try {
            // Initialize client and connect to the server
            BudgetClient client = new BudgetClient("127.0.0.1", 5000);
            client.connect();
            System.out.println("Connected to server successfully.");

            // Launch login window
            SwingUtilities.invokeLater(() -> {
                new LoginUI(client).setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Could not connect to server: " + e.getMessage());
        }
    }
}
