package model;

import java.util.Date;

public class Transaction {
    private int id;
    private int userId;
    private double amount;
    private String type;
    private int categoryId;
    private String description;
    private Date date;

    public Transaction(int id, int userId, double amount, String type, int categoryId, String description, Date date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.description = description;
        this.date = date;
    }

    public Transaction() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}
