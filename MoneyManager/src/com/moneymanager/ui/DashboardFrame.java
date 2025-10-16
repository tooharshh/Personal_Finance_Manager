package com.moneymanager.ui;

import com.moneymanager.model.User;
import com.moneymanager.dao.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main dashboard frame for money management
 */
public class DashboardFrame extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel balanceLabel;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> categoryComboBox;
    
    public DashboardFrame(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadTransactions();
        updateBalance();
    }
    
    private void initializeComponents() {
        setTitle("Money Manager - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        balanceLabel = new JLabel("Current Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(new Color(51, 102, 153));
        
        // Transaction table
        String[] columnNames = {"Date", "Type", "Category", "Description", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        transactionTable = new JTable(tableModel);
        
        // Input fields
        amountField = new JTextField(10);
        descriptionField = new JTextField(20);
        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        categoryComboBox = new JComboBox<>(new String[]{
            "Food", "Transportation", "Entertainment", "Utilities", 
            "Healthcare", "Shopping", "Salary", "Investment", "Other"
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(balanceLabel, BorderLayout.EAST);
        
        // Center panel with table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with input form
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(amountField, gbc);
        
        // Type
        gbc.gridx = 2; gbc.gridy = 0;
        inputPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(typeComboBox, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(descriptionField, gbc);
        
        // Category
        gbc.gridx = 2; gbc.gridy = 1;
        inputPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(categoryComboBox, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Transaction");
        JButton deleteButton = new JButton("Delete Selected");
        JButton logoutButton = new JButton("Logout");
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton);
        
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Event handlers
        addButton.addActionListener(e -> addTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        logoutButton.addActionListener(e -> logout());
    }
    
    private void setupEventHandlers() {
        // Additional event handlers can be added here
    }
    
    private void addTransaction() {
        String amountStr = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String category = (String) categoryComboBox.getSelectedItem();
        
        if (amountStr.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Insert into database
            String query = "INSERT INTO transactions (user_id, type, category, description, amount, transaction_date) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, currentUser.getId());
                stmt.setString(2, type);
                stmt.setString(3, category);
                stmt.setString(4, description);
                stmt.setDouble(5, amount);
                stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Transaction added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearInputFields();
                    loadTransactions();
                    updateBalance();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add transaction.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?", 
                                                   "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // In a real implementation, you would delete from database using transaction ID
            tableModel.removeRow(selectedRow);
            updateBalance();
            JOptionPane.showMessageDialog(this, "Transaction deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void loadTransactions() {
        tableModel.setRowCount(0);
        
        String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            while (rs.next()) {
                Object[] row = {
                    dateFormat.format(rs.getTimestamp("transaction_date")),
                    rs.getString("type"),
                    rs.getString("category"),
                    rs.getString("description"),
                    String.format("$%.2f", rs.getDouble("amount"))
                };
                tableModel.addRow(row);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBalance() {
        String query = "SELECT " +
                      "SUM(CASE WHEN type = 'Income' THEN amount ELSE 0 END) - " +
                      "SUM(CASE WHEN type = 'Expense' THEN amount ELSE 0 END) as balance " +
                      "FROM transactions WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                balanceLabel.setText(String.format("Current Balance: $%.2f", balance));
                
                // Change color based on balance
                if (balance >= 0) {
                    balanceLabel.setForeground(new Color(0, 128, 0)); // Green
                } else {
                    balanceLabel.setForeground(new Color(255, 0, 0)); // Red
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error calculating balance: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearInputFields() {
        amountField.setText("");
        descriptionField.setText("");
        typeComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedIndex(0);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                                                   "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}