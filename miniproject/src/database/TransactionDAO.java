package database;

import model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Get all transactions for a user
    public List<Transaction> getAllByUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT id, user_id, amount, type, category_id, description, date FROM transactions WHERE user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        rs.getInt("category_id"),
                        rs.getString("description"),
                        rs.getDate("date")
                );
                list.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Insert a new transaction
    public boolean insert(Transaction t) {
        String sql = "INSERT INTO transactions (user_id, amount, type, category_id, description, date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, t.getUserId());
            ps.setDouble(2, t.getAmount());
            ps.setString(3, t.getType());
            ps.setInt(4, t.getCategoryId());
            ps.setString(5, t.getDescription());
            java.util.Date dt = t.getDate();
            if (dt != null) {
                ps.setDate(6, new java.sql.Date(dt.getTime()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete transaction by ID
    public boolean delete(int id) {
        String sql = "DELETE FROM transactions WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
