package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class CategoryForm extends JFrame {
    private Connection conn = null;
    private Statement stmt = null;

    private JTable table;
    private JTextField txtCategoryId;
    private JTextField txtCategoryName;
    private JTextField txtCategoryDesc;

    public CategoryForm() {
        setTitle("Category Management");
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
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Category ID"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtCategoryId = new JTextField(20); form.add(txtCategoryId, gbc);
        y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Category Name"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtCategoryName = new JTextField(20); form.add(txtCategoryName, gbc);
        y++;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Description"), gbc);
        gbc.gridx = 1; gbc.gridy = y; txtCategoryDesc = new JTextField(20); form.add(txtCategoryDesc, gbc);
        y++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(clearBtn);
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; form.add(btns, gbc);

        String[] cols = new String[]{"Category ID", "Category Name", "Description"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        panel.add(form, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        add(panel);

        addBtn.addActionListener(e -> model.addRow(new Object[]{
                txtCategoryId.getText(), txtCategoryName.getText(), txtCategoryDesc.getText()
        }));
        clearBtn.addActionListener(e -> {
            txtCategoryId.setText(""); txtCategoryName.setText(""); txtCategoryDesc.setText("");
        });
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new CategoryForm().setVisible(true));
    }
}
