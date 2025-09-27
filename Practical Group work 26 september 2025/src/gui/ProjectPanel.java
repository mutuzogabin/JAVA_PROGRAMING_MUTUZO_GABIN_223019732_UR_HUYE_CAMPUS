package gui;

import db.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProjectPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public ProjectPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{
                "ProjectID", "UserID", "Name", "Description", "ProjectStatus", "ProjectCategory", "CreatedAt"}, 0) {
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

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        loadData();

        addBtn.addActionListener(e -> addProject());
        editBtn.addActionListener(e -> editProject());
        deleteBtn.addActionListener(e -> deleteProject());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM project")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ProjectID"),
                        rs.getInt("UserID"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getString("ProjectStatus"),
                        rs.getString("ProjectCategory"),
                        rs.getTimestamp("CreatedAt")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading projects: " + e.getMessage());
        }
    }

    private void addProject() {
        JTextField userIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Completed", "Pending"});
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Health", "Education", "Environment", "Infrastructure"});

        Object[] fields = {
                "UserID:", userIdField,
                "Name:", nameField,
                "Description:", descField,
                "ProjectStatus:", statusCombo,
                "ProjectCategory:", categoryCombo
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Add Project", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection conn = DBUtil.connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO project (UserID, Name, Description, ProjectStatus, ProjectCategory) VALUES (?,?,?,?,?)")) {

                ps.setInt(1, Integer.parseInt(userIdField.getText()));
                ps.setString(2, nameField.getText());
                ps.setString(3, descField.getText());
                ps.setString(4, statusCombo.getSelectedItem().toString());
                ps.setString(5, categoryCombo.getSelectedItem().toString());

                ps.executeUpdate();
                loadData();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding project: " + e.getMessage());
            }
        }
    }

    private void editProject() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a project to edit");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        JTextField userIdField = new JTextField(tableModel.getValueAt(row, 1).toString());
        JTextField nameField = new JTextField(tableModel.getValueAt(row, 2).toString());
        JTextField descField = new JTextField(tableModel.getValueAt(row, 3).toString());

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Completed", "Pending"});
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());

        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Health", "Education", "Environment", "Infrastructure"});
        categoryCombo.setSelectedItem(tableModel.getValueAt(row, 5).toString());

        Object[] fields = {
                "UserID:", userIdField,
                "Name:", nameField,
                "Description:", descField,
                "ProjectStatus:", statusCombo,
                "ProjectCategory:", categoryCombo
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Edit Project", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection conn = DBUtil.connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE project SET UserID=?, Name=?, Description=?, ProjectStatus=?, ProjectCategory=? WHERE ProjectID=?")) {

                ps.setInt(1, Integer.parseInt(userIdField.getText()));
                ps.setString(2, nameField.getText());
                ps.setString(3, descField.getText());
                ps.setString(4, statusCombo.getSelectedItem().toString());
                ps.setString(5, categoryCombo.getSelectedItem().toString());
                ps.setInt(6, id);

                ps.executeUpdate();
                loadData();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating project: " + e.getMessage());
            }
        }
    }

    private void deleteProject() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a project to delete");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this project?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.connect();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM project WHERE ProjectID=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                loadData();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting project: " + e.getMessage());
            }
        }
    }
}
