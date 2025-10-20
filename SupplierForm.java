package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class SupplierForm extends JFrame {
    private Connection conn = null;
    private Statement stmt = null;

    private JTable table;
    private JTextField txtSupplierId;
    private JTextField txtSupplierName;
    private JTextField txtSupplierContact;
    private JTextField txtSupplierEmail;
    private JTextField txtSupplierAddress;

    public SupplierForm() {
        setTitle("Supplier Management");
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
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Supplier ID"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtSupplierId = new JTextField(20); form.add(txtSupplierId, gbc);
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
        DefaultTableModel model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        addBtn.addActionListener(e -> model.addRow(new Object[]{
                txtSupplierId.getText(), txtSupplierName.getText(), txtSupplierContact.getText(),
                txtSupplierEmail.getText(), txtSupplierAddress.getText()
        }));
        clearBtn.addActionListener(e -> {
            txtSupplierId.setText(""); txtSupplierName.setText(""); txtSupplierContact.setText("");
            txtSupplierEmail.setText(""); txtSupplierAddress.setText("");
        });
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new SupplierForm().setVisible(true));
    }
}
