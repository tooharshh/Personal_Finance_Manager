package com.moneymanager.ui;

import com.moneymanager.dao.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dialog for editing existing transactions
 */
public class EditTransactionDialog extends JDialog {
    private int transactionId;
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextField dateField;
    private boolean transactionUpdated = false;
    
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(64, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color DARK_COLOR = new Color(52, 58, 64);
    
    public EditTransactionDialog(JFrame parent, int transactionId, String date, String type, 
                                String category, String description, double amount) {
        super(parent, "Edit Transaction", true);
        this.transactionId = transactionId;
        
        initializeComponents();
        populateFields(date, type, category, description, amount);
        setupLayout();
        setupEventHandlers();
        applyModernStyling();
    }
    
    private void initializeComponents() {
        setSize(500, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        amountField = new JTextField(15);
        descriptionField = new JTextField(20);
        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        categoryComboBox = new JComboBox<>(new String[]{
            "Food", "Transportation", "Entertainment", "Utilities", 
            "Healthcare", "Shopping", "Salary", "Investment", "Other"
        });
        dateField = new JTextField(15);
        dateField.setToolTipText("Format: MMM dd, yyyy (e.g., Oct 09, 2025)");
    }
    
    private void populateFields(String date, String type, String category, String description, double amount) {
        dateField.setText(date);
        typeComboBox.setSelectedItem(type);
        categoryComboBox.setSelectedItem(category);
        descriptionField.setText(description);
        amountField.setText(String.valueOf(amount));
    }
    
    private void applyModernStyling() {
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        styleTextField(amountField);
        styleTextField(descriptionField);
        styleTextField(dateField);
        styleComboBox(typeComboBox);
        styleComboBox(categoryComboBox);
        
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
    
    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SUCCESS_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SUCCESS_COLOR);
            }
        });
    }
    
    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(SECONDARY_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(11, 23, 11, 23)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(248, 249, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
    }
    
    private void setFontRecursively(Container container, Font font) {
        for (Component component : container.getComponents()) {
            component.setFont(font);
            if (component instanceof Container) {
                setFontRecursively((Container) component, font);
            }
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel with card-like appearance
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_COLOR);
        
        JLabel titleLabel = new JLabel("Edit Transaction");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(DARK_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Modify transaction details");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Add form fields
        addFormField(formPanel, gbc, "Date:", dateField, 0, 0);
        addFormField(formPanel, gbc, "Amount:", amountField, 0, 1);
        addFormField(formPanel, gbc, "Type:", typeComboBox, 0, 2);
        addFormField(formPanel, gbc, "Category:", categoryComboBox, 0, 3);
        addFormField(formPanel, gbc, "Description:", descriptionField, 0, 4);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(CARD_COLOR);
        
        JButton updateButton = new JButton("Update Transaction");
        JButton cancelButton = new JButton("Cancel");
        
        stylePrimaryButton(updateButton);
        styleSecondaryButton(cancelButton);
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        
        // Event handlers
        updateButton.addActionListener(e -> updateTransaction());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void addFormField(JPanel parent, GridBagConstraints gbc, String labelText, JComponent field, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(DARK_COLOR);
        
        gbc.gridx = x; gbc.gridy = y; gbc.anchor = GridBagConstraints.WEST;
        parent.add(label, gbc);
        
        gbc.gridx = x + 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        parent.add(field, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
    }
    
    private void setupEventHandlers() {
        // Additional event handlers can be added here
    }
    
    private void updateTransaction() {
        String amountStr = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String category = (String) categoryComboBox.getSelectedItem();
        String dateStr = dateField.getText().trim();
        
        // Validation
        if (amountStr.isEmpty() || description.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            Date parsedDate;
            try {
                parsedDate = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use format: MMM dd, yyyy (e.g., Oct 09, 2025)", 
                                            "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update database
            String query = "UPDATE transactions SET type = ?, category = ?, description = ?, amount = ?, transaction_date = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, type);
                stmt.setString(2, category);
                stmt.setString(3, description);
                stmt.setDouble(4, amount);
                stmt.setTimestamp(5, new Timestamp(parsedDate.getTime()));
                stmt.setInt(6, transactionId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Transaction updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    transactionUpdated = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update transaction.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isTransactionUpdated() {
        return transactionUpdated;
    }
}