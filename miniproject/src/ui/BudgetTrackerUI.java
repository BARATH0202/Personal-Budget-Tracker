package ui;

import client.BudgetClient;
import model.Category;
import model.Transaction;
import model.User;
import shared.Action;
import shared.Request;
import shared.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Swing UI for the budget tracker
 */
public class BudgetTrackerUI extends JFrame {
    private BudgetClient client;
    private User loggedUser;

    private CardLayout cards = new CardLayout();
    private JPanel mainPanel = new JPanel(cards);

    private JTextField txtUsername = new JTextField(15);
    private JPasswordField txtPassword = new JPasswordField(15);

    private JTextField txtAmount = new JTextField(10);
    private JComboBox<String> cmbType = new JComboBox<>(new String[]{"INCOME", "EXPENSE"});
    private JComboBox<Category> cmbCategory = new JComboBox<>();
    private JTextField txtDesc = new JTextField(15);
    private JTextField txtDate = new JTextField(10);

    private DefaultTableModel tableModel = new DefaultTableModel();
    private JTable table = new JTable(tableModel);

    @SuppressWarnings("unchecked")
    public BudgetTrackerUI(BudgetClient client) {
        super("Personal Budget Tracker");
        this.client = client;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // -------- LOGIN PANEL --------
        JPanel login = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.gridx = 0; g.gridy = 0; login.add(new JLabel("Username:"), g);
        g.gridx = 1; login.add(txtUsername, g);
        g.gridx = 0; g.gridy = 1; login.add(new JLabel("Password:"), g);
        g.gridx = 1; login.add(txtPassword, g);
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        g.gridy = 2; g.gridx = 0; login.add(btnLogin, g);
        g.gridx = 1; login.add(btnRegister, g);

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> doRegister());

        // -------- DASHBOARD PANEL --------
        JPanel dash = new JPanel(new BorderLayout());

        JPanel form = new JPanel();
        form.add(new JLabel("Amount:")); form.add(txtAmount);
        form.add(new JLabel("Type:")); form.add(cmbType);
        form.add(new JLabel("Category:")); form.add(cmbCategory);
        form.add(new JLabel("Date(YYYY-MM-DD):")); form.add(txtDate);
        form.add(new JLabel("Desc:")); form.add(txtDesc);

        // ✅ ADD BUTTONS
        JButton btnAdd = new JButton("Add Transaction");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Delete Selected");

        form.add(btnAdd);
        form.add(btnRefresh);
        form.add(btnDelete);

        // ACTIONS
        btnAdd.addActionListener(e -> addTransaction());
        btnRefresh.addActionListener(e -> loadTransactions());
        btnDelete.addActionListener(e -> deleteSelected());

        // TABLE SETUP
        tableModel.setColumnIdentifiers(new Object[]{"ID", "Date", "Type", "Amount", "Category", "Description"});
        JScrollPane sp = new JScrollPane(table);

        dash.add(form, BorderLayout.NORTH);
        dash.add(sp, BorderLayout.CENTER);

        // ADD BOTH SCREENS TO CARD LAYOUT
        mainPanel.add(login, "login");
        mainPanel.add(dash, "dash");

        add(mainPanel);
        cards.show(mainPanel, "login");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.close();
            }
        });
    }

    // ---------- LOGIN & REGISTER ----------
    private void doLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username/password");
            return;
        }
        try {
            client.sendRequest(new Request(Action.LOGIN, new User(user, pass)));
            Response r = client.readResponse();
            if (r.getStatus() == Action.RESPONSE_OK) {
                loggedUser = (User) r.getPayload();
                JOptionPane.showMessageDialog(this, "✅ Logged in as " + loggedUser.getUsername());
                loadCategories();
                loadTransactions();
                cards.show(mainPanel, "dash");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Login failed: " + r.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error communicating with server: " + e.getMessage());
        }
    }

    private void doRegister() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username/password");
            return;
        }
        try {
            client.sendRequest(new Request(Action.REGISTER, new User(user, pass)));
            Response r = client.readResponse();
            if (r.getStatus() == Action.RESPONSE_OK) {
                JOptionPane.showMessageDialog(this, "✅ Registered. Now login.");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Register failed: " + r.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ---------- LOAD DATA ----------
    private void loadCategories() {
        try {
            client.sendRequest(new Request(Action.GET_CATEGORIES, null));
            Response r = client.readResponse();
            if (r.getStatus() == Action.RESPONSE_OK) {
                java.util.List<Category> cats = (java.util.List<Category>) r.getPayload();
                cmbCategory.removeAllItems();
                for (Category c : cats) cmbCategory.addItem(c);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load categories");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTransactions() {
        try {
            client.sendRequest(new Request(Action.GET_TRANSACTIONS, null));
            Response r = client.readResponse();
            if (r.getStatus() == Action.RESPONSE_OK) {
                List<Transaction> list = (List<Transaction>) r.getPayload();
                tableModel.setRowCount(0);
                for (Transaction t : list) {
                    Object[] row = new Object[]{
                            t.getId(),
                            t.getDate().toString(),
                            t.getType(),
                            t.getAmount(),
                            (t.getCategory() == null ? "" : t.getCategory().getName()),
                            t.getDescription()
                    };
                    tableModel.addRow(row);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + r.getPayload());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ---------- ADD TRANSACTION ----------
    private void addTransaction() {
        try {
            double amt = Double.parseDouble(txtAmount.getText().trim());
            String type = (String) cmbType.getSelectedItem();
            Category cat = (Category) cmbCategory.getSelectedItem();
            String desc = txtDesc.getText().trim();
            LocalDate date = LocalDate.parse(txtDate.getText().trim());

            Transaction t = new Transaction(loggedUser.getId(), amt, type, cat, desc, date);
            client.sendRequest(new Request(Action.ADD_TRANSACTION, t));
            Response r = client.readResponse();

            if (r.getStatus() == Action.RESPONSE_OK) {
                JOptionPane.showMessageDialog(this, "✅ Transaction Added Successfully!");
                loadTransactions();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Add failed: " + r.getPayload());
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            client.sendRequest(new Request(Action.DELETE_TRANSACTION, id));
            Response r = client.readResponse();
            if (r.getStatus() == Action.RESPONSE_OK) {
                JOptionPane.showMessageDialog(this, "✅ Deleted Successfully");
                loadTransactions();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Delete failed: " + r.getPayload());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
