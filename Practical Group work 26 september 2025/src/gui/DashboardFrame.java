package gui;

import javax.swing.*;
import java.awt.*;

// Import all CRUD panels
import gui.ProjectPanel;
import gui.ActivityPanel;
import gui.ResourcePanel;
import gui.TransactionPanel;
import gui.CategoryPanel;
import gui.UserPanel;

public class DashboardFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private String username;

    public DashboardFrame(String username) {
        this.username = username;
        initFrame();
    }

    private void initFrame() {
        setTitle("NGO Project Tracking Dashboard");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // Top gradient panel
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(51, 153, 255),
                        0, getHeight(), new Color(102, 204, 255)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setLayout(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(100, 50));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + " - NGO Project Tracking System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutBtn.addActionListener(e -> logout());
        topPanel.add(logoutBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // TabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Projects", new ProjectPanel());
        tabbedPane.add("Activities", new ActivityPanel());
        tabbedPane.add("Resources", new ResourcePanel());
        tabbedPane.add("Transactions", new TransactionPanel());
        tabbedPane.add("Categories", new CategoryPanel());
        tabbedPane.add("Users", new UserPanel());

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(245, 245, 245));
        tabbedPane.setForeground(new Color(51, 51, 51));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
