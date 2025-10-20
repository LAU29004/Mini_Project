package com.mycompany.mini_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainDashboard extends JFrame {
    public MainDashboard() {
        setTitle("Inventory Management - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        sidebar.setBackground(new Color(30, 41, 59));

        JLabel logo = new JLabel("Inventory");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0,20)));

        JButton btnProducts = new JButton("Products");
        JButton btnCategories = new JButton("Categories");
        JButton btnSuppliers = new JButton("Suppliers");
        JButton btnCustomers = new JButton("Customers");
        JButton btnSales = new JButton("Sales");
        JButton btnStock = new JButton("Stock Report");
        JButton btnUsers = new JButton("Users");

        for (JButton b: new JButton[]{btnProducts, btnCategories, btnSuppliers, btnCustomers, btnSales, btnStock, btnUsers}){
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            sidebar.add(b);
            sidebar.add(Box.createRigidArea(new Dimension(0,6)));
        }

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        topbar.add(title, BorderLayout.WEST);

        // Center area with summary panels and recent table
        JPanel center = new JPanel(new BorderLayout());
        JPanel summary = new JPanel(new GridLayout(1,4,10,10));
        summary.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        summary.add(makeSummaryPanel("Total Items", "120"));
        summary.add(makeSummaryPanel("Low Stock", "8"));
        summary.add(makeSummaryPanel("Total Suppliers", "15"));
        summary.add(makeSummaryPanel("Total Sales", "230"));

        String[] cols = new String[]{"Action","Item","Qty","Date"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable recentTable = new JTable(model);
        JScrollPane sp = new JScrollPane(recentTable);

        center.add(summary, BorderLayout.NORTH);
        center.add(sp, BorderLayout.CENTER);

        root.add(sidebar, BorderLayout.WEST);
        root.add(topbar, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        add(root);

        // Action listeners
        btnProducts.addActionListener(e -> new ProductForm().setVisible(true));
        btnCategories.addActionListener(e -> new CategoryForm().setVisible(true));
        btnSuppliers.addActionListener(e -> new SupplierForm().setVisible(true));
        btnCustomers.addActionListener(e -> new CustomerForm().setVisible(true));
        btnSales.addActionListener(e -> new SalesForm().setVisible(true));
        btnStock.addActionListener(e -> new StockReportForm().setVisible(true));
        btnUsers.addActionListener(e -> new UserForm().setVisible(true));
    }

    private JPanel makeSummaryPanel(String title, String value){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(10,10,10,10)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel v = new JLabel(value, SwingConstants.RIGHT);
        v.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(t, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}
