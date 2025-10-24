package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class StockReportForm extends JFrame {
    private Connection conn = null;
    private Statement stmt = null;

    private JTable table;
    private JTextField txtStockProduct;
    private JTextField txtStockCurrent;
    private JTextField txtStockRestock;

    public StockReportForm() {
        setTitle("Stock Report");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        setSize(900,600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y=0;

        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Product"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtStockProduct = new JTextField(20); form.add(txtStockProduct, gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Current Quantity"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtStockCurrent = new JTextField(20); form.add(txtStockCurrent, gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Restock Level"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtStockRestock = new JTextField(20); form.add(txtStockRestock, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Product", "Current Quantity", "Restock Level"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        addBtn.addActionListener(e -> model.addRow(new Object[]{
                txtStockProduct.getText(), txtStockCurrent.getText(), txtStockRestock.getText()
        }));
        clearBtn.addActionListener(e -> {
            txtStockProduct.setText(""); txtStockCurrent.setText(""); txtStockRestock.setText("");
        });
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new StockReportForm().setVisible(true));
    }
}
