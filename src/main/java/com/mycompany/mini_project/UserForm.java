package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserForm extends JFrame {
    private Connection conn = null;

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtUserId;
    private JTextField txtUsername;
    private JTextField txtUserRole;
    private JTextField txtUserStatus;

    public UserForm() {
        setTitle("User Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        conn = DBConnection.getConnection();

        initComponents();
        setSize(900, 600);
        setLocationRelativeTo(null);

        if (conn != null) {
            loadUsers();
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("User ID"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtUserId = new JTextField(20); 
        txtUserId.setEditable(false); 
        form.add(txtUserId, gbc);
        y++;
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtUsername = new JTextField(20); form.add(txtUsername, gbc);
        y++;
  
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Role"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtUserRole = new JTextField(20); form.add(txtUserRole, gbc);
        y++;
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Status"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtUserStatus = new JTextField(20); form.add(txtUserStatus, gbc);
        y++;


        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; form.add(btns, gbc);

        String[] cols = new String[]{"User ID", "Username", "Role", "Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);
        
        // --- Action Listeners for CRUD Operations ---

        // 1. Add (INSERT)
        addBtn.addActionListener(e -> addUser());
        
        // 2. Edit (UPDATE)
        editBtn.addActionListener(e -> updateUser());
        
        // 3. Delete (DELETE)
        delBtn.addActionListener(e -> deleteUser());

        // 4. Clear Fields
        clearBtn.addActionListener(e -> clearFields());
        
        // 5. Table Click (LOAD selected row data into form fields)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtUserId.setText(model.getValueAt(row, 0).toString());
                txtUsername.setText(model.getValueAt(row, 1).toString());
                txtUserRole.setText(model.getValueAt(row, 2).toString());
                txtUserStatus.setText(model.getValueAt(row, 3).toString());
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                DBConnection.closeConnection(conn);
            }
        });
    }

    private void loadUsers() {
        model.setRowCount(0); 
        String sql = "SELECT user_id, username, role, status FROM user"; 
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (conn == null) return;
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void addUser() {
        String username = txtUsername.getText();
        String role = txtUserRole.getText();
        String status = txtUserStatus.getText();

        String tempPassword = "123"; 

        if (username.isEmpty() || role.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Username, Role, and Status.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO user (username, password, role, status) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, tempPassword);
            pstmt.setString(3, role);
            pstmt.setString(4, status);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
                loadUsers();
                clearFields();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    // Updates an existing user in the database
    private void updateUser() {
        String id = txtUserId.getText();
        String username = txtUsername.getText();
        String role = txtUserRole.getText();
        String status = txtUserStatus.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE user SET username = ?, role = ?, status = ? WHERE user_id = ?";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, role);
            pstmt.setString(3, status);
            pstmt.setString(4, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                loadUsers(); // Refresh the table
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. User ID not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    // Deletes a user from the database
    private void deleteUser() {
        String id = txtUserId.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM user WHERE user_id = ?";
            PreparedStatement pstmt = null;

            try {
                if (conn == null) return;

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    loadUsers(); // Refresh the table
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed. User ID not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // Clears the text fields
    private void clearFields() {
        txtUserId.setText("");
        txtUsername.setText("");
        txtUserRole.setText("");
        txtUserStatus.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new UserForm().setVisible(true));
    }
}