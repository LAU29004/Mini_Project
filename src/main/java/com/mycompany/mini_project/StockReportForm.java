package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockReportForm extends JFrame {
    private Connection conn = null;

    private JTable table;
    private DefaultTableModel model;
    // Keeping fields for manual update/restock purposes
    private JTextField txtStockProduct;
    private JTextField txtStockCurrent;
    private JTextField txtStockRestock;

    // Define a constant for the restock level for filtering/reporting
    private static final int LOW_STOCK_THRESHOLD = 10; 

    public StockReportForm() {
        setTitle("Stock Report");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        conn = DBConnection.getConnection(); 
        initComponents();
        setSize(900,600);
        setLocationRelativeTo(null);
        
        if (conn != null) {
            loadStockReport();
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Stock Management"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y=0;

        // Note: The UI for Stock Report should typically be simple viewing, 
        // but we keep the original fields for potential quick manual adjustments or reference.

        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Product Name"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtStockProduct = new JTextField(20); form.add(txtStockProduct, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Current Quantity"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtStockCurrent = new JTextField(20); form.add(txtStockCurrent, gbc);
        y++;
        
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Restock Level"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtStockRestock = new JTextField(20); form.add(txtStockRestock, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh Report");
        JButton viewLowStockBtn = new JButton("View Low Stock");
        JButton clearBtn = new JButton("Clear Fields");
        btns.add(refreshBtn); btns.add(viewLowStockBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Product Name", "Current Quantity", "Restock Level", "Status"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        // Action Listeners
        refreshBtn.addActionListener(e -> loadStockReport());
        viewLowStockBtn.addActionListener(e -> loadLowStockReport());
        clearBtn.addActionListener(e -> clearFields());

        // Close connection on window close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                DBConnection.closeConnection(conn);
            }
        });
    }

    // --- READ Methods ---
    
    private void loadStockReport() {
        model.setRowCount(0); 
        // Assumes we use a default restock level for reporting purposes
        String sql = "SELECT name, quantity FROM product"; 
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                String status = (quantity <= LOW_STOCK_THRESHOLD) ? "LOW STOCK" : "In Stock";
                
                model.addRow(new Object[]{
                    rs.getString("name"),
                    quantity,
                    LOW_STOCK_THRESHOLD,
                    status
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading stock report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void loadLowStockReport() {
        model.setRowCount(0); 
        // Selects only products where the quantity is below the threshold
        String sql = "SELECT name, quantity FROM product WHERE quantity <= ?"; 
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (conn == null) return;
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, LOW_STOCK_THRESHOLD);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    LOW_STOCK_THRESHOLD,
                    "LOW STOCK"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading low stock report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    private void clearFields() {
        txtStockProduct.setText(""); txtStockCurrent.setText(""); txtStockRestock.setText("");
        table.clearSelection();
    }
}