package com.moneymanager;

import com.moneymanager.ui.ModernLoginFrame;
import javax.swing.SwingUtilities;

/**
 * Main class for the Money Manager application
 */
public class Main {
    public static void main(String[] args) {
        // Launch the modern application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new ModernLoginFrame().setVisible(true);
        });
    }
}