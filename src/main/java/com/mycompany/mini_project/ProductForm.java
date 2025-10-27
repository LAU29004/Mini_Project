package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductForm extends JFrame {
    private Connection conn = null;

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtProductId;
    private JTextField txtName;
    private JTextField txtCategory;
    private JTextField txtQuantity;
    private JTextField txtPrice;

    public ProductForm() {
        setTitle("Product Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        conn = DBConnection.getConnection(); 
        initComponents();
        setSize(900,600);
        setLocationRelativeTo(null);
        
        if (conn != null) {
            loadProducts();
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
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Product ID"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtProductId = new JTextField(20); 
        txtProductId.setEditable(false);
        form.add(txtProductId, gbc);
        y++;
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Name"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtName = new JTextField(20); form.add(txtName, gbc);
        y++;
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Category"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtCategory = new JTextField(20); form.add(txtCategory, gbc);
        y++;
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Quantity"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtQuantity = new JTextField(20); form.add(txtQuantity, gbc);
        y++;
        
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Price"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtPrice = new JTextField(20); form.add(txtPrice, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Product ID", "Name", "Category", "Quantity", "Price"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        // Action Listeners
        addBtn.addActionListener(e -> addProduct());
        editBtn.addActionListener(e -> editProduct());
        delBtn.addActionListener(e -> deleteProduct());
        clearBtn.addActionListener(e -> clearFields());

        // Table Click Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtProductId.setText(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtCategory.setText(model.getValueAt(row, 2).toString());
                txtQuantity.setText(model.getValueAt(row, 3).toString());
                txtPrice.setText(model.getValueAt(row, 4).toString());
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
    
    private void loadProducts() {
        model.setRowCount(0); 
        String sql = "SELECT product_id, name, category, quantity, price FROM product"; 
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void addProduct() {
        String name = txtName.getText().trim();
        String cat = txtCategory.getText().trim();
        String qty = txtQuantity.getText().trim();
        String price = txtPrice.getText().trim();

        if (name.isEmpty() || qty.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Quantity, and Price cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO product (name, category, quantity, price) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, cat);
            pstmt.setInt(3, Integer.parseInt(qty));
            pstmt.setDouble(4, Double.parseDouble(price));

            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                loadProducts();
                clearFields();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Product Name '" + name + "' already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void editProduct() {
        String id = txtProductId.getText().trim();
        String name = txtName.getText().trim();
        String cat = txtCategory.getText().trim();
        String qty = txtQuantity.getText().trim();
        String price = txtPrice.getText().trim();

        if (id.isEmpty() || name.isEmpty() || qty.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a product and fill all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE product SET name = ?, category = ?, quantity = ?, price = ? WHERE product_id = ?";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, cat);
            pstmt.setInt(3, Integer.parseInt(qty));
            pstmt.setDouble(4, Double.parseDouble(price));
            pstmt.setString(5, id);

            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
                loadProducts();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Product ID not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Product Name '" + name + "' already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error updating product: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void deleteProduct() {
        String id = txtProductId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete product ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM product WHERE product_id = ?";
            PreparedStatement pstmt = null;

            try {
                if (conn == null) return;
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id);

                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                    loadProducts();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed. Product ID not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private void clearFields() {
        txtProductId.setText(""); txtName.setText(""); txtCategory.setText("");
        txtQuantity.setText(""); txtPrice.setText("");
        table.clearSelection();
    }
}