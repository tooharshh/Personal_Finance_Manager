package com.moneymanager.ui;

import com.moneymanager.dao.UserDAO;
import com.moneymanager.model.User;
import com.moneymanager.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login frame for user authentication
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserDAO userDAO;
    
    public LoginFrame() {
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("Money Manager - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Money Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 102, 153));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        
        // Enter key for password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            showMessage("Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = userDAO.authenticateUser(username, password);
        if (user != null) {
            showMessage("Login successful! Welcome, " + user.getFullName(), "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Open dashboard
            SwingUtilities.invokeLater(() -> {
                new DashboardFrame(user).setVisible(true);
                dispose();
            });
        } else {
            showMessage("Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRegister() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setVisible(true);
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    /**
     * Registration dialog
     */
    private class RegisterDialog extends JDialog {
        private JTextField regUsernameField;
        private JPasswordField regPasswordField;
        private JPasswordField confirmPasswordField;
        private JTextField emailField;
        private JTextField fullNameField;
        
        public RegisterDialog(JFrame parent) {
            super(parent, "Register New User", true);
            initializeRegisterComponents();
            setupRegisterLayout();
            setupRegisterEventHandlers();
        }
        
        private void initializeRegisterComponents() {
            setSize(400, 350);
            setLocationRelativeTo(getParent());
            setResizable(false);
            
            regUsernameField = new JTextField(20);
            regPasswordField = new JPasswordField(20);
            confirmPasswordField = new JPasswordField(20);
            emailField = new JTextField(20);
            fullNameField = new JTextField(20);
        }
        
        private void setupRegisterLayout() {
            setLayout(new BorderLayout());
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            
            // Username
            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(regUsernameField, gbc);
            
            // Password
            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(regPasswordField, gbc);
            
            // Confirm Password
            gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Confirm Password:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(confirmPasswordField, gbc);
            
            // Email
            gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(emailField, gbc);
            
            // Full Name
            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
            formPanel.add(new JLabel("Full Name:"), gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(fullNameField, gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton registerBtn = new JButton("Register");
            JButton cancelBtn = new JButton("Cancel");
            buttonPanel.add(registerBtn);
            buttonPanel.add(cancelBtn);
            
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            
            // Event handlers for buttons
            registerBtn.addActionListener(e -> handleRegistration());
            cancelBtn.addActionListener(e -> dispose());
        }
        
        private void setupRegisterEventHandlers() {
            // Enter key handling can be added here if needed
        }
        
        private void handleRegistration() {
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String email = emailField.getText().trim();
            String fullName = fullNameField.getText().trim();
            
            // Validation
            if (!ValidationUtil.isValidUsername(username)) {
                showMessage("Username must be 3-20 characters, alphanumeric and underscore only.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!ValidationUtil.isValidPassword(password)) {
                showMessage("Password must be at least 6 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                showMessage("Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!ValidationUtil.isValidEmail(email)) {
                showMessage("Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!ValidationUtil.isValidFullName(fullName)) {
                showMessage("Full name must be 2-50 characters, letters and spaces only.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if username exists
            if (userDAO.usernameExists(username)) {
                showMessage("Username already exists. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Register user
            User newUser = new User(username, password, email, fullName);
            if (userDAO.registerUser(newUser)) {
                showMessage("Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                showMessage("Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}