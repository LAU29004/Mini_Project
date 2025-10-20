package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class CustomerForm extends JFrame {
    private Connection conn = null;
    private Statement stmt = null;

    private JTable table;
    private JTextField txtCustomerId;
    private JTextField txtCustomerName;
    private JTextField txtCustomerContact;
    private JTextField txtCustomerEmail;
    private JTextField txtCustomerAddress;

    public CustomerForm() {
        setTitle("Customer Management");
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
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Customer ID"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtCustomerId = new JTextField(20); form.add(txtCustomerId, gbc);
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
        DefaultTableModel model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        addBtn.addActionListener(e -> model.addRow(new Object[]{
                txtCustomerId.getText(), txtCustomerName.getText(), txtCustomerContact.getText(),
                txtCustomerEmail.getText(), txtCustomerAddress.getText()
        }));
        clearBtn.addActionListener(e -> {
            txtCustomerId.setText(""); txtCustomerName.setText(""); txtCustomerContact.setText("");
            txtCustomerEmail.setText(""); txtCustomerAddress.setText("");
        });
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new CustomerForm().setVisible(true));
    }
}
