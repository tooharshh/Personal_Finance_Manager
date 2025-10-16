package com.moneymanager.ui;

import com.moneymanager.dao.UserDAO;
import com.moneymanager.model.User;
import com.moneymanager.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern registration dialog
 */
public class ModernRegisterDialog extends JDialog {
    private JTextField regUsernameField;
    private JPasswordField regPasswordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private UserDAO userDAO;
    
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(64, 123, 255);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    
    public ModernRegisterDialog(JFrame parent) {
        super(parent, "Create Account", true);
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        applyModernStyling();
    }
    
    private void initializeComponents() {
        setSize(480, 650);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        regUsernameField = new JTextField(20);
        regPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        emailField = new JTextField(20);
        fullNameField = new JTextField(20);
    }
    
    private void applyModernStyling() {
        // Set background
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Style text fields
        styleTextField(regUsernameField);
        styleTextField(regPasswordField);
        styleTextField(confirmPasswordField);
        styleTextField(emailField);
        styleTextField(fullNameField);
        
        // Set modern font
        Font modernFont = new Font("Segoe UI", Font.PLAIN, 14);
        setFontRecursively(this, modernFont);
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        
        // Add focus border effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(11, 15, 11, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        });
    }
    
    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        // Hover effect
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
        
        // Hover effect
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
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_COLOR);
        
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Join Money Manager today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(25));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        
        // Add form fields
        addFormField(formPanel, "Full Name", fullNameField);
        addFormField(formPanel, "Username", regUsernameField);
        addFormField(formPanel, "Email", emailField);
        addFormField(formPanel, "Password", regPasswordField);
        addFormField(formPanel, "Confirm Password", confirmPasswordField);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(CARD_COLOR);
        
        JButton registerBtn = new JButton("Create Account");
        JButton cancelBtn = new JButton("Cancel");
        
        stylePrimaryButton(registerBtn);
        styleSecondaryButton(cancelBtn);
        
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, registerBtn.getPreferredSize().height));
        
        cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, cancelBtn.getPreferredSize().height));
        
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(registerBtn);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(cancelBtn);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        
        // Event handlers for buttons
        registerBtn.addActionListener(e -> handleRegistration());
        cancelBtn.addActionListener(e -> dispose());
    }
    
    private void addFormField(JPanel parent, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(33, 37, 41));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        
        parent.add(label);
        parent.add(Box.createVerticalStrut(5));
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));
    }
    
    private void setupEventHandlers() {
        // Additional event handlers can be added here
    }
    
    private void handleRegistration() {
        String username = regUsernameField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        
        // Validation
        if (!ValidationUtil.isValidFullName(fullName)) {
            showMessage("Full name must be 2-50 characters, letters and spaces only.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtil.isValidUsername(username)) {
            showMessage("Username must be 3-20 characters, alphanumeric and underscore only.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            showMessage("Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtil.isValidPassword(password)) {
            showMessage("Password must be at least 6 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if username exists
        if (userDAO.usernameExists(username)) {
            showMessage("Username already exists. Please choose a different username.", "Username Taken", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Register user
        User newUser = new User(username, password, email, fullName);
        if (userDAO.registerUser(newUser)) {
            showMessage("Account created successfully! You can now sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showMessage("Registration failed. Please try again.", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}