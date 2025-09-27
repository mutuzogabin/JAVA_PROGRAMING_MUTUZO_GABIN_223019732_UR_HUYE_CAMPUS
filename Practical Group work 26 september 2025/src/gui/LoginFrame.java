package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import db.DBUtil;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("NGO Project Tracking Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(3,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> login());
        panel.add(loginBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn);

        add(panel, BorderLayout.CENTER);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBUtil.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM User WHERE Username=? AND PasswordHash=?")) {

            ps.setString(1, username);
            ps.setString(2, password); // plaintext password for now
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                SwingUtilities.invokeLater(() -> new DashboardFrame(username).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }

        } catch(SQLException e) {
            JOptionPane.showMessageDialog(this,"DB Error: "+e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
