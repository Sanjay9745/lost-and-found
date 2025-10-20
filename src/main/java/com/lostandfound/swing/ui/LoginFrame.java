package com.lostandfound.swing.ui;

import com.lostandfound.dto.LoginRequest;
import com.lostandfound.dto.LoginResponse;
import com.lostandfound.swing.api.ApiClient;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Lost and Found - Admin Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Lost and Found System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.addActionListener(e -> performLogin());
        formPanel.add(loginButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel();
        JLabel infoLabel = new JLabel("Default: admin / admin123");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);
        infoPanel.add(infoLabel);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Enter key listener
        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter username and password",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Perform login in background thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private String errorMessage = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    LoginRequest request = new LoginRequest(username, password);
                    ApiClient.ApiResponse response = ApiClient.post("/auth/login", request);

                    if (response.isSuccess()) {
                        LoginResponse loginResponse = response.parseBody(LoginResponse.class);
                        ApiClient.setAuthToken(loginResponse.getToken());
                    } else {
                        errorMessage = "Invalid username or password";
                    }
                } catch (Exception ex) {
                    errorMessage = "Connection error: " + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Login");

                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            errorMessage,
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    // Open main window
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    LoginFrame.this.dispose();
                }
            }
        };

        worker.execute();
    }
}
