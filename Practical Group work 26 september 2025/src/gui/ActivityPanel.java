package gui;

import db.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ActivityPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public ActivityPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{
                "ActivityID", "ProjectID", "Title", "Date", "Status", "Value", "Notes"}, 0) {
          
            public boolean isCellEditable(int row, int col) { return false; }
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

        addBtn.addActionListener(e -> addActivity());
        editBtn.addActionListener(e -> editActivity());
        deleteBtn.addActionListener(e -> deleteActivity());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBUtil.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM activity")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ActivityID"),
                        rs.getInt("ProjectID"),
                        rs.getString("Title"),
                        rs.getDate("Date"),
                        rs.getString("Status"),
                        rs.getBigDecimal("Value"),
                        rs.getString("Notes")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading activities: " + e.getMessage());
        }
    }

    private void addActivity() {
        JTextField projectIdField = new JTextField();
        JTextField titleField = new JTextField();

        // Default today's date
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JTextField dateField = new JTextField(today);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Planned","Ongoing","Completed","Cancelled"});
        JTextField valueField = new JTextField();
        JTextArea notesArea = new JTextArea(3,20);

        Object[] fields = {
                "ProjectID:", projectIdField,
                "Title:", titleField,
                "Date:", dateField,
                "Status:", statusBox,
                "Value:", valueField,
                "Notes:", new JScrollPane(notesArea)
        };

        if(JOptionPane.showConfirmDialog(this, fields, "Add Activity", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
            try (Connection conn = DBUtil.connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO activity (ProjectID, Title, Date, Status, Value, Notes) VALUES (?,?,?,?,?,?)")) {

                ps.setInt(1, Integer.parseInt(projectIdField.getText()));
                ps.setString(2, titleField.getText());
                ps.setDate(3, Date.valueOf(dateField.getText().trim()));
                ps.setString(4, (String) statusBox.getSelectedItem());
                ps.setBigDecimal(5, valueField.getText().isEmpty() ? null : new java.math.BigDecimal(valueField.getText()));
                ps.setString(6, notesArea.getText());

                ps.executeUpdate();
                loadData();

            } catch(SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding activity: " + e.getMessage());
            } catch(IllegalArgumentException ie) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD");
            }
        }
    }

    private void editActivity() {
        int row = table.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"Select an activity to edit"); return; }

        int id = (int)tableModel.getValueAt(row,0);
        JTextField projectIdField = new JTextField(tableModel.getValueAt(row,1).toString());
        JTextField titleField = new JTextField(tableModel.getValueAt(row,2).toString());

        // Default today if date is null
        Object dateValue = tableModel.getValueAt(row,3);
        String dateText = (dateValue != null) ? dateValue.toString()
                : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JTextField dateField = new JTextField(dateText);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Planned","Ongoing","Completed","Cancelled"});
        statusBox.setSelectedItem(tableModel.getValueAt(row,4).toString());

        JTextField valueField = new JTextField(tableModel.getValueAt(row,5) != null ? tableModel.getValueAt(row,5).toString() : "");
        JTextArea notesArea = new JTextArea(tableModel.getValueAt(row,6) != null ? tableModel.getValueAt(row,6).toString() : "",3,20);

        Object[] fields = {
                "ProjectID:", projectIdField,
                "Title:", titleField,
                "Date:", dateField,
                "Status:", statusBox,
                "Value:", valueField,
                "Notes:", new JScrollPane(notesArea)
        };

        if(JOptionPane.showConfirmDialog(this, fields, "Edit Activity", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
            try (Connection conn = DBUtil.connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE activity SET ProjectID=?, Title=?, Date=?, Status=?, Value=?, Notes=? WHERE ActivityID=?")) {

                ps.setInt(1, Integer.parseInt(projectIdField.getText()));
                ps.setString(2, titleField.getText());
                ps.setDate(3, Date.valueOf(dateField.getText().trim()));
                ps.setString(4, (String) statusBox.getSelectedItem());
                ps.setBigDecimal(5, valueField.getText().isEmpty() ? null : new java.math.BigDecimal(valueField.getText()));
                ps.setString(6, notesArea.getText());
                ps.setInt(7, id);

                ps.executeUpdate();
                loadData();

            } catch(SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating activity: " + e.getMessage());
            } catch(IllegalArgumentException ie) {
                JOptionPane.showMessageDialog(this,"Invalid date format. Use YYYY-MM-DD");
            }
        }
    }

    private void deleteActivity() {
        int row = table.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"Select an activity to delete"); return; }

        int id = (int)tableModel.getValueAt(row,0);
        if(JOptionPane.showConfirmDialog(this,"Delete this activity?","Confirm",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            try(Connection conn = DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM activity WHERE ActivityID=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                loadData();

            } catch(SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting activity: " + e.getMessage());
            }
        }
    }
}
