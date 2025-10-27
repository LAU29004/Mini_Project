package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryForm extends JFrame {
    private Connection conn = null;

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtCategoryId;
    private JTextField txtCategoryName;
    private JTextField txtCategoryDesc;

    public CategoryForm() {
        setTitle("Category Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 1. Establish connection
        conn = DBConnection.getConnection(); 
        
        initComponents();
        setSize(900,600);
        setLocationRelativeTo(null);
        
        // 2. Load data if connection is successful
        if (conn != null) {
            loadCategories();
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y=0;
        
        // Category ID (made read-only)
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Category ID"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtCategoryId = new JTextField(20); 
        txtCategoryId.setEditable(false); 
        form.add(txtCategoryId, gbc);
        y++;
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Category Name"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtCategoryName = new JTextField(20); form.add(txtCategoryName, gbc);
        y++;
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Description"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtCategoryDesc = new JTextField(20); form.add(txtCategoryDesc, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Category ID", "Category Name", "Description"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        // --- Action Listeners for CRUD ---
        addBtn.addActionListener(e -> addCategory());
        editBtn.addActionListener(e -> editCategory());
        delBtn.addActionListener(e -> deleteCategory());
        clearBtn.addActionListener(e -> clearFields());

        // Table Click Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtCategoryId.setText(model.getValueAt(row, 0).toString());
                txtCategoryName.setText(model.getValueAt(row, 1).toString());
                txtCategoryDesc.setText(model.getValueAt(row, 2).toString());
            }
        });

        // Close connection on window close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                DBConnection.closeConnection(conn);
            }
        });
    }

    // --- CRUD Methods ---
    
    // READ (Load All)
    private void loadCategories() {
        model.setRowCount(0); 
        String sql = "SELECT category_id, category_name, description FROM category"; 
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("category_id"),
                    rs.getString("category_name"),
                    rs.getString("description")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // CREATE (Add)
    private void addCategory() {
        String name = txtCategoryName.getText().trim();
        String desc = txtCategoryDesc.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO category (category_name, description) VALUES (?, ?)";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, desc);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Category added successfully!");
                loadCategories();
                clearFields();
            }
        } catch (SQLException e) {
             // Handle unique constraint violation (Category Name already exists)
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Category Name '" + name + "' already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error adding category: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    // UPDATE (Edit)
    private void editCategory() {
        String id = txtCategoryId.getText().trim();
        String name = txtCategoryName.getText().trim();
        String desc = txtCategoryDesc.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a category and fill in the name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE category SET category_name = ?, description = ? WHERE category_id = ?";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, desc);
            pstmt.setString(3, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Category updated successfully!");
                loadCategories();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Category ID not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Category Name '" + name + "' already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error updating category: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    // DELETE
    private void deleteCategory() {
        String id = txtCategoryId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete category ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM category WHERE category_id = ?";
            PreparedStatement pstmt = null;

            try {
                if (conn == null) return;
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Category deleted successfully!");
                    loadCategories();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed. Category ID not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting category: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // Clear Fields
    private void clearFields() {
        txtCategoryId.setText("");
        txtCategoryName.setText("");
        txtCategoryDesc.setText("");
        table.clearSelection();
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new CategoryForm().setVisible(true));
    }
}