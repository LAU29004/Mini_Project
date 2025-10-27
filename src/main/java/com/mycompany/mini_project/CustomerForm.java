package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerForm extends JFrame {
    private Connection conn = null;

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtCustomerId;
    private JTextField txtCustomerName;
    private JTextField txtCustomerContact;
    private JTextField txtCustomerEmail;
    private JTextField txtCustomerAddress;

    public CustomerForm() {
        setTitle("Customer Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        conn = DBConnection.getConnection(); 
        initComponents();
        setSize(900,600);
        setLocationRelativeTo(null);
        
        if (conn != null) {
            loadCustomers();
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
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Customer ID"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtCustomerId = new JTextField(20); 
        txtCustomerId.setEditable(false);
        form.add(txtCustomerId, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Name"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtCustomerName = new JTextField(20); form.add(txtCustomerName, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Contact"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtCustomerContact = new JTextField(20); form.add(txtCustomerContact, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Email"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtCustomerEmail = new JTextField(20); form.add(txtCustomerEmail, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Address"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtCustomerAddress = new JTextField(20); form.add(txtCustomerAddress, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Customer ID", "Name", "Contact", "Email", "Address"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        // Action Listeners
        addBtn.addActionListener(e -> addCustomer());
        editBtn.addActionListener(e -> editCustomer());
        delBtn.addActionListener(e -> deleteCustomer());
        clearBtn.addActionListener(e -> clearFields());

        // Table Click Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtCustomerId.setText(model.getValueAt(row, 0).toString());
                txtCustomerName.setText(model.getValueAt(row, 1).toString());
                txtCustomerContact.setText(model.getValueAt(row, 2).toString());
                txtCustomerEmail.setText(model.getValueAt(row, 3).toString());
                txtCustomerAddress.setText(model.getValueAt(row, 4).toString());
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
    
    private void loadCustomers() {
        model.setRowCount(0); 
        String sql = "SELECT customer_id, name, contact, email, address FROM customer"; 
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("email"),
                    rs.getString("address")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void addCustomer() {
        String name = txtCustomerName.getText().trim();
        String contact = txtCustomerContact.getText().trim();
        String email = txtCustomerEmail.getText().trim();
        String address = txtCustomerAddress.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer Name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO customer (name, contact, email, address) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, email);
            pstmt.setString(4, address);

            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
                loadCustomers();
                clearFields();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Email address already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void editCustomer() {
        String id = txtCustomerId.getText().trim();
        String name = txtCustomerName.getText().trim();
        String contact = txtCustomerContact.getText().trim();
        String email = txtCustomerEmail.getText().trim();
        String address = txtCustomerAddress.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a customer and fill in the name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE customer SET name = ?, contact = ?, email = ?, address = ? WHERE customer_id = ?";
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
                JOptionPane.showMessageDialog(this, "Customer updated successfully!");
                loadCustomers();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Customer ID not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                 JOptionPane.showMessageDialog(this, "Email address already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void deleteCustomer() {
        String id = txtCustomerId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete customer ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM customer WHERE customer_id = ?";
            PreparedStatement pstmt = null;

            try {
                if (conn == null) return;
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id);

                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
                    loadCustomers();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed. Customer ID not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private void clearFields() {
        txtCustomerId.setText(""); txtCustomerName.setText(""); txtCustomerContact.setText("");
        txtCustomerEmail.setText(""); txtCustomerAddress.setText("");
        table.clearSelection();
    }
}