package com.moneymanager.ui;

import com.moneymanager.dao.DatabaseConnection;
import com.moneymanager.dao.UserDAO;
import com.moneymanager.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;


public class AnalyticsDashboard extends JFrame {
    

    private static final Color PRIMARY_COLOR = new Color(64, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color INFO_COLOR = new Color(23, 162, 184);
    private static final Color LIGHT_COLOR = new Color(248, 249, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_MUTED = new Color(108, 117, 125);
    
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    
    private User currentUser;
    private UserDAO userDAO;
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
 
    private double totalIncome = 0;
    private double totalExpenses = 0;
    private double currentBalance = 0;
    private Map<String, Double> categoryExpenses = new HashMap<>();
    private Map<String, Double> monthlyIncomeData = new HashMap<>();
    private Map<String, Double> monthlyExpenseData = new HashMap<>();
    
    private JPanel mainScrollPanel;
    private JPanel summaryPanel;
    private JPanel categoryPanel;
    private JPanel chartsPanel;

    public AnalyticsDashboard(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        
        initializeData();
        initializeUI();
        loadAnalyticsData();
        refreshAllPanels();
    }

    private void initializeUI() {
        setTitle("Analytics Dashboard - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Main container
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_COLOR);
        
        // Header panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content with scroll
        mainScrollPanel = new JPanel();
        mainScrollPanel.setLayout(new BoxLayout(mainScrollPanel, BoxLayout.Y_AXIS));
        mainScrollPanel.setBackground(LIGHT_COLOR);
        mainScrollPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(mainScrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Initialize panels
        createAllPanels();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel("Financial Analytics Dashboard");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Comprehensive overview of your financial data • " + currentUser.getUsername());
        subtitleLabel.setFont(BODY_FONT);
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Refresh button and back button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);
        
        JButton backButton = new JButton("Back to Dashboard");
        styleModernButton(backButton, TEXT_DARK);
        backButton.addActionListener(e -> dispose());
        
        JButton refreshButton = new JButton("Refresh Data");
        styleModernButton(refreshButton, SUCCESS_COLOR);
        refreshButton.addActionListener(e -> refreshAnalytics());
        
        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void createAllPanels() {
        mainScrollPanel.removeAll();
        
        // Summary section
        summaryPanel = createFinancialSummaryPanel();
        mainScrollPanel.add(summaryPanel);
        mainScrollPanel.add(Box.createVerticalStrut(20));
        
        // Two-column layout for charts (removed trends panel)
        JPanel chartsRowPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsRowPanel.setBackground(LIGHT_COLOR);
        
        categoryPanel = createCategoryBreakdownPanel();
        chartsPanel = createVisualChartsPanel();
        
        chartsRowPanel.add(categoryPanel);
        chartsRowPanel.add(chartsPanel);
        
        mainScrollPanel.add(chartsRowPanel);
        mainScrollPanel.add(Box.createVerticalStrut(20));
        
        mainScrollPanel.revalidate();
        mainScrollPanel.repaint();
    }

    private JPanel createFinancialSummaryPanel() {
        JPanel panel = createModernCard("Financial Summary");
        panel.setLayout(new GridLayout(2, 3, 15, 15));
        
        // Calculate additional metrics
        double netSavings = totalIncome - totalExpenses;
        double avgMonthlyExpense = monthlyExpenseData.isEmpty() ? 0 : 
            totalExpenses / monthlyExpenseData.size();
        double expenseRatio = totalIncome > 0 ? (totalExpenses / totalIncome) * 100 : 0;
        
        panel.add(createSummaryCard("Total Income", totalIncome, SUCCESS_COLOR, ""));
        panel.add(createSummaryCard("Total Expenses", totalExpenses, DANGER_COLOR, ""));
        panel.add(createSummaryCard("Current Balance", currentBalance, 
            currentBalance >= 0 ? SUCCESS_COLOR : DANGER_COLOR, ""));
        panel.add(createSummaryCard("Net Savings", netSavings,
            netSavings >= 0 ? SUCCESS_COLOR : DANGER_COLOR, ""));
        panel.add(createSummaryCard("Avg Monthly Expense", avgMonthlyExpense, WARNING_COLOR, ""));
        panel.add(createSummaryCard("Expense Ratio", expenseRatio, INFO_COLOR, "%"));
        
        return panel;
    }

    private JPanel createCategoryBreakdownPanel() {
        JPanel panel = createModernCard("Expense Breakdown by Category");
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        if (categoryExpenses.isEmpty()) {
            JLabel noDataLabel = new JLabel("No expense data available");
            noDataLabel.setFont(BODY_FONT);
            noDataLabel.setForeground(TEXT_MUTED);
            noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalStrut(50));
            panel.add(noDataLabel);
            return panel;
        }
        
        // Sort categories by expense amount
        List<Map.Entry<String, Double>> sortedCategories = new ArrayList<>(categoryExpenses.entrySet());
        sortedCategories.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        panel.add(Box.createVerticalStrut(10));
        
        for (Map.Entry<String, Double> entry : sortedCategories) {
            if (entry.getValue() > 0) {
                panel.add(createCategoryProgressBar(entry.getKey(), entry.getValue()));
                panel.add(Box.createVerticalStrut(12));
            }
        }
        
        return panel;
    }

    private JPanel createVisualChartsPanel() {
        JPanel panel = createModernCard("Visual Distribution");
        panel.setLayout(new BorderLayout());
        
        if (categoryExpenses.isEmpty()) {
            JLabel noDataLabel = new JLabel("No data to visualize");
            noDataLabel.setFont(BODY_FONT);
            noDataLabel.setForeground(TEXT_MUTED);
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noDataLabel, BorderLayout.CENTER);
        } else {
            PieChartPanel pieChart = new PieChartPanel(categoryExpenses);
            panel.add(pieChart, BorderLayout.CENTER);
        }
        
        return panel;
    }

    private JPanel createModernCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            new EmptyBorder(20, 25, 25, 25)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        card.add(titleLabel, BorderLayout.NORTH);
        
        return card;
    }

    private JPanel createSummaryCard(String title, double value, Color color, String suffix) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(TEXT_MUTED);
        
        String valueText;
        if (suffix.equals("%")) {
            valueText = String.format("%.1f%%", value);
        } else {
            valueText = currencyFormat.format(value);
        }
        
        JLabel valueLabel = new JLabel(valueText);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);
        
        return card;
    }

    private JPanel createCategoryProgressBar(String category, double amount) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CARD_COLOR);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        container.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Top row with category name and amount
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBackground(CARD_COLOR);
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(SUBTITLE_FONT);
        categoryLabel.setForeground(TEXT_DARK);
        
        JLabel amountLabel = new JLabel(currencyFormat.format(amount));
        amountLabel.setFont(SUBTITLE_FONT);
        amountLabel.setForeground(DANGER_COLOR);
        
        labelPanel.add(categoryLabel, BorderLayout.WEST);
        labelPanel.add(amountLabel, BorderLayout.EAST);
        
        // Progress bar
        double percentage = totalExpenses > 0 ? (amount / totalExpenses) * 100 : 0;
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) percentage);
        progressBar.setString(String.format("%.1f%%", percentage));
        progressBar.setStringPainted(true);
        progressBar.setBackground(new Color(248, 249, 250));
        progressBar.setForeground(DANGER_COLOR);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 20));
        progressBar.setFont(SMALL_FONT);
        
        container.add(labelPanel, BorderLayout.NORTH);
        container.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        container.add(progressBar, BorderLayout.SOUTH);
        
        return container;
    }

    private void styleModernButton(JButton button, Color backgroundColor) {
        button.setFont(SUBTITLE_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
    }

    private void initializeData() {
        categoryExpenses.clear();
        monthlyIncomeData.clear();
        monthlyExpenseData.clear();
        totalIncome = 0;
        totalExpenses = 0;
        currentBalance = 0;
    }

    private void loadAnalyticsData() {
        try {
            String query = "SELECT type, amount, category, DATE_FORMAT(transaction_date, '%Y-%m') as month " +
                          "FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, currentUser.getId());
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    String type = rs.getString("type");
                    double amount = rs.getDouble("amount");
                    String category = rs.getString("category");
                    String month = rs.getString("month");
                    
                    if ("Income".equals(type)) {
                        totalIncome += amount;
                        monthlyIncomeData.put(month, monthlyIncomeData.getOrDefault(month, 0.0) + amount);
                    } else if ("Expense".equals(type)) {
                        totalExpenses += amount;
                        categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + amount);
                        monthlyExpenseData.put(month, monthlyExpenseData.getOrDefault(month, 0.0) + amount);
                    }
                }
                
                currentBalance = totalIncome - totalExpenses;
                
            }
        } catch (SQLException e) {
            System.err.println("Error loading analytics data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error loading analytics data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllPanels() {
        SwingUtilities.invokeLater(() -> {
            createAllPanels();
            revalidate();
            repaint();
        });
    }

    private void refreshAnalytics() {
        initializeData();
        loadAnalyticsData();
        refreshAllPanels();
        
        JOptionPane.showMessageDialog(this, 
            "Analytics data refreshed successfully!", 
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Custom Pie Chart Panel
    private class PieChartPanel extends JPanel {
        private Map<String, Double> data;
        private Color[] chartColors = {
            new Color(255, 99, 132), new Color(54, 162, 235), new Color(255, 205, 86),
            new Color(75, 192, 192), new Color(153, 102, 255), new Color(255, 159, 64),
            new Color(201, 203, 207), new Color(83, 102, 255), new Color(255, 99, 255),
            new Color(99, 255, 132)
        };

        public PieChartPanel(Map<String, Double> data) {
            this.data = new HashMap<>(data);
            setBackground(CARD_COLOR);
            setPreferredSize(new Dimension(300, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data.isEmpty() || totalExpenses == 0) {
                return;
            }

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2 - 20;
            int radius = Math.min(getWidth() - 40, getHeight() - 60) / 3;

            double startAngle = 0;
            int colorIndex = 0;

            // Draw pie slices
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double percentage = entry.getValue() / totalExpenses;
                double arcAngle = 360 * percentage;

                g2d.setColor(chartColors[colorIndex % chartColors.length]);
                g2d.fillArc(centerX - radius, centerY - radius, 
                           2 * radius, 2 * radius, 
                           (int) startAngle, (int) arcAngle);

                startAngle += arcAngle;
                colorIndex++;
            }

            // Draw legend
            int legendY = centerY + radius + 30;
            int legendX = 20;
            colorIndex = 0;
            
            g2d.setFont(SMALL_FONT);
            
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double percentage = entry.getValue() / totalExpenses;
                
                // Color box
                g2d.setColor(chartColors[colorIndex % chartColors.length]);
                g2d.fillRect(legendX, legendY, 12, 12);
                
                // Text
                g2d.setColor(TEXT_DARK);
                String legendText = String.format("%s (%.1f%%)", entry.getKey(), percentage * 100);
                g2d.drawString(legendText, legendX + 18, legendY + 10);
                
                legendY += 18;
                colorIndex++;
                
                // Wrap to next column if needed
                if (legendY > getHeight() - 20) {
                    legendY = centerY + radius + 30;
                    legendX += 150;
                }
            }
        }
    }
}