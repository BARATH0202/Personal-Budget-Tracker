package client.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

import client.BudgetClient;
import database.DBConnection;

public class BudgetTrackerUI extends JFrame {

    private final BudgetClient client;
    private JTable table;
    private JTextField amountField, descriptionField, dateField;
    private JComboBox<String> typeBox, categoryBox;

    public BudgetTrackerUI(BudgetClient client) {
        this.client = client;
        setTitle("Personal Budget Tracker");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadTransactions();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 10, 10));

        // Input fields
        amountField = new JTextField();
        descriptionField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());

        typeBox = new JComboBox<>(new String[]{"Expense", "Income"});
        categoryBox = new JComboBox<>();

        JButton addBtn = new JButton("Add Transaction");

        // Load category names from DB
        loadCategories();

        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeBox);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryBox);

        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addBtn);

        // Table setup
        table = new JTable(new DefaultTableModel(
                new String[]{"ID", "Amount", "Type", "Category", "Description", "Date"}, 0));
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);

        // Button Action
        addBtn.addActionListener(e -> addTransaction());
    }

    private void loadCategories() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM categories")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categoryBox.addItem(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    private void addTransaction() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO transactions (user_id, amount, type, category_id, description, date) " +
                    "VALUES (?, ?, ?, (SELECT id FROM categories WHERE name=?), ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, 1); // default user id
            ps.setDouble(2, Double.parseDouble(amountField.getText()));
            ps.setString(3, (String) typeBox.getSelectedItem());
            ps.setString(4, (String) categoryBox.getSelectedItem());
            ps.setString(5, descriptionField.getText());
            ps.setDate(6, java.sql.Date.valueOf(dateField.getText()));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Transaction added successfully!");
            loadTransactions();

            amountField.setText("");
            descriptionField.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding transaction: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT t.id, t.amount, t.type, c.name AS category, t.description, t.date " +
                     "FROM transactions t LEFT JOIN categories c ON t.category_id = c.id")) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getDouble("amount"));
                row.add(rs.getString("type"));
                row.add(rs.getString("category"));
                row.add(rs.getString("description"));
                row.add(rs.getDate("date"));
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
        }
    }
}
