package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesForm extends JFrame {
    private Connection conn = null;

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSaleId;
    private JTextField txtSaleProduct;
    private JTextField txtSaleQty;
    private JTextField txtSaleTotal;
    private JTextField txtSaleDate;

    public SalesForm() {
        setTitle("Sales / Transactions");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        conn = DBConnection.getConnection(); 
        initComponents();
        setSize(900,600);
        setLocationRelativeTo(null);
        
        if (conn != null) {
            loadSales();
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

        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Sale ID"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSaleId = new JTextField(20); 
        txtSaleId.setEditable(false);
        form.add(txtSaleId, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Product"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSaleProduct = new JTextField(20); form.add(txtSaleProduct, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Quantity"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSaleQty = new JTextField(20); form.add(txtSaleQty, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Total Price"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSaleTotal = new JTextField(20); form.add(txtSaleTotal, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Date (YYYY-MM-DD)"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSaleDate = new JTextField(20); 
        txtSaleDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); // Set current date as default
        form.add(txtSaleDate, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Sale ID", "Product", "Quantity", "Total Price", "Date"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        // Action Listeners
        addBtn.addActionListener(e -> addSale());
        editBtn.addActionListener(e -> editSale());
        delBtn.addActionListener(e -> deleteSale());
        clearBtn.addActionListener(e -> clearFields());

        // Table Click Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtSaleId.setText(model.getValueAt(row, 0).toString());
                txtSaleProduct.setText(model.getValueAt(row, 1).toString());
                txtSaleQty.setText(model.getValueAt(row, 2).toString());
                txtSaleTotal.setText(model.getValueAt(row, 3).toString());
                txtSaleDate.setText(model.getValueAt(row, 4).toString());
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
    
    private void loadSales() {
        model.setRowCount(0); 
        String sql = "SELECT sale_id, product, quantity, total_price, sale_date FROM sale"; 
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("sale_id"),
                    rs.getString("product"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_price"),
                    rs.getDate("sale_date").toString()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading sales: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void addSale() {
        String product = txtSaleProduct.getText().trim();
        String qty = txtSaleQty.getText().trim();
        String total = txtSaleTotal.getText().trim();
        String date = txtSaleDate.getText().trim();

        if (product.isEmpty() || qty.isEmpty() || total.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO sale (product, quantity, total_price, sale_date) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, product);
            pstmt.setInt(2, Integer.parseInt(qty));
            pstmt.setDouble(3, Double.parseDouble(total));
            pstmt.setString(4, date); // MySQL can parse YYYY-MM-DD string as DATE

            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Sale added successfully!");
                loadSales();
                clearFields();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity and Total Price must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding sale: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void editSale() {
        String id = txtSaleId.getText().trim();
        String product = txtSaleProduct.getText().trim();
        String qty = txtSaleQty.getText().trim();
        String total = txtSaleTotal.getText().trim();
        String date = txtSaleDate.getText().trim();

        if (id.isEmpty() || product.isEmpty() || qty.isEmpty() || total.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a sale and fill all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE sale SET product = ?, quantity = ?, total_price = ?, sale_date = ? WHERE sale_id = ?";
        PreparedStatement pstmt = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, product);
            pstmt.setInt(2, Integer.parseInt(qty));
            pstmt.setDouble(3, Double.parseDouble(total));
            pstmt.setString(4, date);
            pstmt.setString(5, id);

            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Sale updated successfully!");
                loadSales();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Sale ID not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity and Total Price must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating sale: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void deleteSale() {
        String id = txtSaleId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a sale to delete.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete sale ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM sale WHERE sale_id = ?";
            PreparedStatement pstmt = null;

            try {
                if (conn == null) return;
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id);

                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Sale deleted successfully!");
                    loadSales();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed. Sale ID not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting sale: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private void clearFields() {
        txtSaleId.setText(""); txtSaleProduct.setText(""); txtSaleQty.setText("");
        txtSaleTotal.setText(""); txtSaleDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        table.clearSelection();
    }
}