package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class ProductForm extends JFrame {
    private Connection conn = null;
    private Statement stmt = null;

    private JTable table;
    private JTextField txtProductId;
    private JTextField txtName;
    private JTextField txtCategory;
    private JTextField txtQuantity;
    private JTextField txtPrice;

    public ProductForm() {
        setTitle("Product Management");
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
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Product ID"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtProductId = new JTextField(20); form.add(txtProductId, gbc);
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
        DefaultTableModel model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        addBtn.addActionListener(e -> model.addRow(new Object[]{
                txtProductId.getText(), txtName.getText(), txtCategory.getText(),
                txtQuantity.getText(), txtPrice.getText()
        }));
        clearBtn.addActionListener(e -> {
            txtProductId.setText(""); txtName.setText(""); txtCategory.setText("");
            txtQuantity.setText(""); txtPrice.setText("");
        });
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new ProductForm().setVisible(true));
    }
}
