package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierForm extends JFrame {
    private Connection conn = null;

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSupplierId;
    private JTextField txtSupplierName;
    private JTextField txtSupplierContact;
    private JTextField txtSupplierEmail;
    private JTextField txtSupplierAddress;

    public SupplierForm() {
        setTitle("Supplier Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        conn = DBConnection.getConnection(); 
        initComponents();
        setSize(900,600);
        setLocationRelativeTo(null);
        
        if (conn != null) {
            loadSuppliers();
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
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Supplier ID"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSupplierId = new JTextField(20); 
        txtSupplierId.setEditable(false);
        form.add(txtSupplierId, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Name"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSupplierName = new JTextField(20); form.add(txtSupplierName, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Contact"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSupplierContact = new JTextField(20); form.add(txtSupplierContact, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Email"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSupplierEmail = new JTextField(20); form.add(txtSupplierEmail, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Address"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSupplierAddress = new JTextField(20); form.add(txtSupplierAddress, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Supplier ID", "Name", "Contact", "Email", "Address"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        // Action Listeners
        addBtn.addActionListener(e -> addSupplier());
        editBtn.addActionListener(e -> editSupplier());
        delBtn.addActionListener(e -> deleteSupplier());
        clearBtn.addActionListener(e -> clearFields());

        // Table Click Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtSupplierId.setText(model.getValueAt(row, 0).toString());
                txtSupplierName.setText(model.getValueAt(row, 1).toString());
                txtSupplierContact.setText(model.getValueAt(row, 2).toString());
                txtSupplierEmail.setText(model.getValueAt(row, 3).toString());
                txtSupplierAddress.setText(model.getValueAt(row, 4).toString());
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
    
    private void loadSuppliers() {
        model.setRowCount(0); 
        String sql = "SELECT supplier_id, name, contact, email, address FROM supplier"; 
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("email"),
                    rs.getString("address")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void addSupplier() {
        String name = txtSupplierName.getText().trim();
        String contact = txtSupplierContact.getText().trim();
        String email = txtSupplierEmail.getText().trim();
        String address = txtSupplierAddress.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO supplier (name, contact, email, address) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, email);
            pstmt.setString(4, address);

            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");
                loadSuppliers();
                clearFields();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Supplier Name '" + name + "' already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error adding supplier: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void editSupplier() {
        String id = txtSupplierId.getText().trim();
        String name = txtSupplierName.getText().trim();
        String contact = txtSupplierContact.getText().trim();
        String email = txtSupplierEmail.getText().trim();
        String address = txtSupplierAddress.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a supplier and fill in the name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE supplier SET name = ?, contact = ?, email = ?, address = ? WHERE supplier_id = ?";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setString(5, id);

            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
                loadSuppliers();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Supplier ID not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Supplier Name '" + name + "' already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error updating supplier: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void deleteSupplier() {
        String id = txtSupplierId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete supplier ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM supplier WHERE supplier_id = ?";
            PreparedStatement pstmt = null;

            try {
                if (conn == null) return;
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id);

                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Supplier deleted successfully!");
                    loadSuppliers();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed. Supplier ID not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting supplier: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private void clearFields() {
        txtSupplierId.setText(""); txtSupplierName.setText(""); txtSupplierContact.setText("");
        txtSupplierEmail.setText(""); txtSupplierAddress.setText("");
        table.clearSelection();
    }
}