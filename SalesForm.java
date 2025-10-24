package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class SalesForm extends JFrame {
    private Connection conn = null;
    private Statement stmt = null;

    private JTable table;
    private JTextField txtSaleId;
    private JTextField txtSaleProduct;
    private JTextField txtSaleQty;
    private JTextField txtSaleTotal;
    private JTextField txtSaleDate;

    public SalesForm() {
        setTitle("Sales / Transactions");
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

        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Sale ID"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSaleId = new JTextField(20); form.add(txtSaleId, gbc);
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
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Date"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSaleDate = new JTextField(20); form.add(txtSaleDate, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Sale ID", "Product", "Quantity", "Total Price", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        addBtn.addActionListener(e -> model.addRow(new Object[]{
                txtSaleId.getText(), txtSaleProduct.getText(), txtSaleQty.getText(),
                txtSaleTotal.getText(), txtSaleDate.getText()
        }));
        clearBtn.addActionListener(e -> {
            txtSaleId.setText(""); txtSaleProduct.setText(""); txtSaleQty.setText("");
            txtSaleTotal.setText(""); txtSaleDate.setText("");
        });
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new SalesForm().setVisible(true));
    }
}
