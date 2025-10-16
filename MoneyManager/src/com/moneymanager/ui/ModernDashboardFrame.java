package com.moneymanager.ui;

import com.moneymanager.model.User;
import com.moneymanager.dao.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Modern dashboard frame for money management
 */
public class ModernDashboardFrame extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel balanceLabel;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> categoryComboBox;
    
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(64, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color INFO_COLOR = new Color(23, 162, 184);
    private static final Color LIGHT_COLOR = new Color(248, 249, 250);
    private static final Color DARK_COLOR = new Color(52, 58, 64);
    private static final Color CARD_COLOR = Color.WHITE;
    
    public ModernDashboardFrame(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        applyModernStyling();
        loadTransactions();
        updateBalance();
    }
    
    private void initializeComponents() {
        setTitle("Money Manager - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        balanceLabel = new JLabel("$0.00");
        
        // Transaction table
        String[] columnNames = {"ID", "Date", "Type", "Category", "Description", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        
        // Hide the ID column but keep it for reference
        transactionTable.getColumnModel().getColumn(0).setMinWidth(0);
        transactionTable.getColumnModel().getColumn(0).setMaxWidth(0);
        transactionTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Input fields
        amountField = new JTextField(15);
        descriptionField = new JTextField(20);
        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        categoryComboBox = new JComboBox<>(new String[]{
            "Food", "Transportation", "Entertainment", "Utilities", 
            "Healthcare", "Shopping", "Salary", "Investment", "Other"
        });
    }
    
    private void applyModernStyling() {
        // Set background
        getContentPane().setBackground(LIGHT_COLOR);
        
        // Style welcome label
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(DARK_COLOR);
        
        // Style balance label
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        balanceLabel.setForeground(SUCCESS_COLOR);
        
        // Style input fields
        styleTextField(amountField);
        styleTextField(descriptionField);
        styleComboBox(typeComboBox);
        styleComboBox(categoryComboBox);
        
        // Style table
        styleTable(transactionTable);
        
        // Set modern font
        Font modernFont = new Font("Segoe UI", Font.PLAIN, 14);
        setFontRecursively(this, modernFont);
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(9, 11, 9, 11)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        });
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        comboBox.setBackground(Color.WHITE);
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(230, 247, 255));
        table.setSelectionForeground(DARK_COLOR);
        
        // Header styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(LIGHT_COLOR);
        table.getTableHeader().setForeground(DARK_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Custom cell renderer for alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(250, 250, 250));
                    }
                }
                
                // Style amount column based on transaction type
                if (column == 1 && value != null) { // Type column
                    if ("Income".equals(value.toString())) {
                        setForeground(SUCCESS_COLOR);
                    } else if ("Expense".equals(value.toString())) {
                        setForeground(DANGER_COLOR);
                    }
                }
                
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return c;
            }
        });
    }
    
    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }
    
    private void styleDangerButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(DANGER_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(DANGER_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(DANGER_COLOR);
            }
        });
    }
    
    private void styleWarningButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(255, 193, 7)); // Warning color
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 193, 7).darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 193, 7));
            }
        });
    }
    
    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(DARK_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(11, 19, 11, 19)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(LIGHT_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
    }
    
    private void styleInfoButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(INFO_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(INFO_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(INFO_COLOR);
            }
        });
    }
    
    private void setFontRecursively(Container container, Font font) {
        for (Component component : container.getComponents()) {
            if (!(component instanceof JTable)) {
                component.setFont(font);
            }
            if (component instanceof Container) {
                setFontRecursively((Container) component, font);
            }
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel with welcome and balance
        JPanel topPanel = createCard();
        topPanel.setLayout(new BorderLayout(20, 0));
        topPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setBackground(CARD_COLOR);
        welcomePanel.add(welcomeLabel);
        
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        balancePanel.setBackground(CARD_COLOR);
        JLabel balanceText = new JLabel("Current Balance: ");
        balanceText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        balanceText.setForeground(DARK_COLOR);
        balancePanel.add(balanceText);
        balancePanel.add(balanceLabel);
        
        topPanel.add(welcomePanel, BorderLayout.WEST);
        topPanel.add(balancePanel, BorderLayout.EAST);
        
        // Center panel with transactions table
        JPanel centerPanel = createCard();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25), "Recent Transactions",
            0, 0, new Font("Segoe UI", Font.BOLD, 16), DARK_COLOR
        ));
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with input form
        JPanel bottomPanel = createCard();
        bottomPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(20, 25, 10, 25), "Add New Transaction",
            0, 0, new Font("Segoe UI", Font.BOLD, 16), DARK_COLOR
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Form fields
        addFormField(formPanel, gbc, "Amount:", amountField, 0, 0);
        addFormField(formPanel, gbc, "Type:", typeComboBox, 2, 0);
        addFormField(formPanel, gbc, "Description:", descriptionField, 0, 1);
        addFormField(formPanel, gbc, "Category:", categoryComboBox, 2, 1);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(CARD_COLOR);
        
        JButton addButton = new JButton("Add Transaction");
        JButton editButton = new JButton("Edit Transaction");
        JButton deleteButton = new JButton("Delete Selected");
        JButton analyticsButton = new JButton("Analytics");
        JButton logoutButton = new JButton("Logout");
        
        stylePrimaryButton(addButton);
        styleWarningButton(editButton);
        styleDangerButton(deleteButton);
        styleInfoButton(analyticsButton);
        styleSecondaryButton(logoutButton);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(analyticsButton);
        buttonPanel.add(logoutButton);
        
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Main container with margins
        JPanel mainContainer = new JPanel(new BorderLayout(0, 15));
        mainContainer.setBackground(LIGHT_COLOR);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        mainContainer.add(topPanel, BorderLayout.NORTH);
        mainContainer.add(centerPanel, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
        
        // Event handlers
        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        analyticsButton.addActionListener(e -> openAnalyticsDashboard());
        logoutButton.addActionListener(e -> logout());
    }
    
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return card;
    }
    
    private void addFormField(JPanel parent, GridBagConstraints gbc, String labelText, JComponent field, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(DARK_COLOR);
        
        gbc.gridx = x; gbc.gridy = y; gbc.anchor = GridBagConstraints.WEST;
        parent.add(label, gbc);
        
        gbc.gridx = x + 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        parent.add(field, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }
    
    private void setupEventHandlers() {
        // Additional event handlers can be added here
    }
    
    // Rest of the methods remain the same as the original DashboardFrame
    // (addTransaction, deleteTransaction, loadTransactions, updateBalance, etc.)
    
    private void addTransaction() {
        String amountStr = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String category = (String) categoryComboBox.getSelectedItem();
        
        if (amountStr.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
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
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get transaction data from the selected row
        int transactionId = (Integer) tableModel.getValueAt(selectedRow, 0); // Hidden ID column
        String date = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 2);
        String category = (String) tableModel.getValueAt(selectedRow, 3);
        String description = (String) tableModel.getValueAt(selectedRow, 4);
        String amountStr = (String) tableModel.getValueAt(selectedRow, 5);
        
        // Remove $ and parse amount
        double amount = Double.parseDouble(amountStr.replace("$", "").replace(",", ""));
        
        // Create and show edit dialog
        EditTransactionDialog dialog = new EditTransactionDialog(this, transactionId, date, type, category, description, amount);
        dialog.setVisible(true);
        
        // Refresh the table if transaction was updated
        if (dialog.isTransactionUpdated()) {
            loadTransactions();
            updateBalance();
        }
    }
    
    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?", 
                                                   "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Get transaction ID from the selected row
            int transactionId = (Integer) tableModel.getValueAt(selectedRow, 0); // Hidden ID column
            
            // Delete from database
            String query = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, transactionId);
                stmt.setInt(2, currentUser.getId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Transaction deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTransactions(); // Refresh the table
                    updateBalance(); // Update balance
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete transaction.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadTransactions() {
        tableModel.setRowCount(0);
        
        String query = "SELECT id, transaction_date, type, category, description, amount FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"), // Transaction ID (hidden column)
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
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
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
                balanceLabel.setText(String.format("$%.2f", balance));
                
                if (balance >= 0) {
                    balanceLabel.setForeground(SUCCESS_COLOR);
                } else {
                    balanceLabel.setForeground(DANGER_COLOR);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error calculating balance: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearInputFields() {
        amountField.setText("");
        descriptionField.setText("");
        typeComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedIndex(0);
    }
    
    private void openAnalyticsDashboard() {
        try {
            AnalyticsDashboard analyticsDashboard = new AnalyticsDashboard(currentUser);
            analyticsDashboard.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error opening analytics dashboard: " + e.getMessage(), 
                "Analytics Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                                                   "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new ModernLoginFrame().setVisible(true);
        }
    }
}