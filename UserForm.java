package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class UserForm extends JFrame {
    private Connection conn = null;
    private Statement stmt = null;

    private JTable table;
    private JTextField txtUserId;
    private JTextField txtUsername;
    private JTextField txtUserRole;
    private JTextField txtUserStatus;

    public UserForm() {
        setTitle("User Management");
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

        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("User ID"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtUserId = new JTextField(20); form.add(txtUserId, gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Username"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtUsername = new JTextField(20); form.add(txtUsername, gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Role"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtUserRole = new JTextField(20); form.add(txtUserRole, gbc);
        y++;
        gbc.gridx=0; gbc.gridy=y; form.add(new JLabel("Status"), gbc);
        gbc.gridx=1; gbc.gridy=y; txtUserStatus = new JTextField(20); form.add(txtUserStatus, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"User ID", "Username", "Role", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        addBtn.addActionListener(e -> model.addRow(new Object[]{
                txtUserId.getText(), txtUsername.getText(), txtUserRole.getText(), txtUserStatus.getText()
        }));
        clearBtn.addActionListener(e -> {
            txtUserId.setText(""); txtUsername.setText(""); txtUserRole.setText(""); txtUserStatus.setText("");
        });
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new UserForm().setVisible(true));
    }
}
