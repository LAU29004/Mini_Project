package com.mycompany.mini_project;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUp extends JFrame {
    public SignUp() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField(15);
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField(15);
        JButton btnRegister = new JButton("Register");
        JButton btnBack = new JButton("Back");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblUsername, gbc);
        gbc.gridx = 1; panel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblPassword, gbc);
        gbc.gridx = 1; panel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(btnRegister, gbc);
        gbc.gridx = 1; panel.add(btnBack, gbc);

        add(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btnRegister.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Connection conn = DBConnection.getConnection();
            
            if (conn != null) {
                // Set default values for new user
                String defaultRole = "Staff";
                String defaultStatus = "Active"; 
                
                // SQL to insert the new user
                String sql = "INSERT INTO user (username, password, role, status) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = null;
                
                try {
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.setString(3, defaultRole);
                    pstmt.setString(4, defaultStatus);
                    
                    int affectedRows = pstmt.executeUpdate();
                    
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Registration Successful! You can now log in.");
                        new Login().setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Registration Failed.");
                    }
                } catch (SQLException ex) {
                    // Catch unique constraint violation (duplicate username)
                    if (ex.getErrorCode() == 1062) { 
                        JOptionPane.showMessageDialog(this, "Username '" + username + "' is already taken.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Registration Database Error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } finally {
                    try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    DBConnection.closeConnection(conn);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Could not connect to database. Check DBConnection settings.");
            }
        });

        btnBack.addActionListener(e -> {
            new Login().setVisible(true);
            this.dispose();
        });
    }
}