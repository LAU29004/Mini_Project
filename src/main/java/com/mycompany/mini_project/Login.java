package com.mycompany.mini_project;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {
    public Login() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField(15);
        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
        JButton btnSignUp = new JButton("Sign Up");

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblUsername, gbc);
        gbc.gridx = 1; panel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblPassword, gbc);
        gbc.gridx = 1; panel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(btnLogin, gbc);
        gbc.gridx = 1; panel.add(btnSignUp, gbc);

        add(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            
            Connection conn = DBConnection.getConnection();
            
            if (conn != null) {
                // IMPORTANT: In a production app, never store or compare plain passwords. Use hashing (e.g., bcrypt).
                String sql = "SELECT * FROM user WHERE username = ? AND password = ?"; 
                PreparedStatement pstmt = null;
                ResultSet rs = null;
                
                try {
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password); 
                    
                    rs = pstmt.executeQuery();
                    
                    if(rs.next()) {
                        new MainDashboard().setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Username or Password");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Login Database Error: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    try { if (rs != null) rs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                    DBConnection.closeConnection(conn);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Could not connect to database. Check DBConnection settings.");
            }
        });

        btnSignUp.addActionListener(e -> {
            new SignUp().setVisible(true);
            this.dispose();
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Login().setVisible(true));
    }
}