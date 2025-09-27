package gui;

import db.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public UserPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{
                "UserID", "Username", "Role", "Email", "CreatedAt"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        buttonPanel.add(addBtn); buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn); buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData();

        addBtn.addActionListener(e -> addUser());
        editBtn.addActionListener(e -> editUser());
        deleteBtn.addActionListener(e -> deleteUser());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT UserID, Username, Role, Email, CreatedAt FROM user")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Role"),
                        rs.getString("Email"),
                        rs.getTimestamp("CreatedAt")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    private void addUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin","Manager","Staff","Volunteer"});

        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Email:", emailField,
                "Role:", roleBox
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection conn = DBUtil.connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO user (Username, PasswordHash, Role, Email) VALUES (?,?,?,?)")) {

                ps.setString(1, usernameField.getText());
                ps.setString(2, new String(passwordField.getPassword())); // store hash ideally
                ps.setString(3, (String) roleBox.getSelectedItem());
                ps.setString(4, emailField.getText());
                ps.executeUpdate();
                loadData();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
            }
        }
    }

    private void editUser() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user to edit"); return; }

        int id = (int) tableModel.getValueAt(row, 0);
        JTextField usernameField = new JTextField(tableModel.getValueAt(row, 1).toString());
        JTextField emailField = new JTextField(tableModel.getValueAt(row, 3).toString());
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin","Manager","Staff","Volunteer"});
        roleBox.setSelectedItem(tableModel.getValueAt(row, 2));

        Object[] fields = {
                "Username:", usernameField,
                "Email:", emailField,
                "Role:", roleBox
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection conn = DBUtil.connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET Username=?, Role=?, Email=? WHERE UserID=?")) {

                ps.setString(1, usernameField.getText());
                ps.setString(2, (String) roleBox.getSelectedItem());
                ps.setString(3, emailField.getText());
                ps.setInt(4, id);
                ps.executeUpdate();
                loadData();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage());
            }
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user to delete"); return; }

        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.connect();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM user WHERE UserID=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                loadData();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
            }
        }
    }
}
