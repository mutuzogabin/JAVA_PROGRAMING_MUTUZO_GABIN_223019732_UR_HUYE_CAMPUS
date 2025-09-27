package gui;

import db.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ResourcePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public ResourcePanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{
                "ResourceID", "ProjectID", "Name", "Type", "StartDate", "EndDate", "Status"
        }, 0){
            @Override
            public boolean isCellEditable(int row,int col){ return false; }
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

        addBtn.addActionListener(e -> addResource());
        editBtn.addActionListener(e -> editResource());
        deleteBtn.addActionListener(e -> deleteResource());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try(Connection conn = DBUtil.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM resource")) {

            while(rs.next()){
                tableModel.addRow(new Object[]{
                        rs.getInt("ResourceID"),
                        rs.getInt("ProjectID"),
                        rs.getString("Name"),
                        rs.getString("Type"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("Status")
                });
            }

        } catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error loading resources: " + e.getMessage());
        }
    }

    private void addResource() {
        JTextField projectIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();

        // Default today's date in yyyy-MM-dd format
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JTextField startField = new JTextField(today);
        JTextField endField = new JTextField(today);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available","In Use","Completed","Inactive"});

        Object[] fields = {
                "ProjectID:", projectIdField,
                "Name:", nameField,
                "Type:", typeField,
                "Start Date:", startField,
                "End Date:", endField,
                "Status:", statusBox
        };

        if(JOptionPane.showConfirmDialog(this, fields,"Add Resource",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try(Connection conn = DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO resource (ProjectID, Name, Type, StartDate, EndDate, Status) VALUES (?,?,?,?,?,?)")) {

                ps.setInt(1, Integer.parseInt(projectIdField.getText()));
                ps.setString(2, nameField.getText());
                ps.setString(3, typeField.getText());
                ps.setDate(4, startField.getText().isEmpty() ? null : Date.valueOf(startField.getText().trim()));
                ps.setDate(5, endField.getText().isEmpty() ? null : Date.valueOf(endField.getText().trim()));
                ps.setString(6, (String)statusBox.getSelectedItem());
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){
                JOptionPane.showMessageDialog(this, "Error adding resource: " + e.getMessage());
            } catch(IllegalArgumentException e2){
                JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD.");
            }
        }
    }

    private void editResource() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this,"Select a resource to edit"); return; }

        int id = (int)tableModel.getValueAt(row,0);
        JTextField projectIdField = new JTextField(tableModel.getValueAt(row,1).toString());
        JTextField nameField = new JTextField(tableModel.getValueAt(row,2).toString());
        JTextField typeField = new JTextField(tableModel.getValueAt(row,3).toString());

        // If null, fallback to today's date
        Object startVal = tableModel.getValueAt(row,4);
        String startDate = (startVal != null) ? startVal.toString() :
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JTextField startField = new JTextField(startDate);

        Object endVal = tableModel.getValueAt(row,5);
        String endDate = (endVal != null) ? endVal.toString() :
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JTextField endField = new JTextField(endDate);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available","In Use","Completed","Inactive"});
        statusBox.setSelectedItem(tableModel.getValueAt(row,6).toString());

        Object[] fields = {
                "ProjectID:", projectIdField,
                "Name:", nameField,
                "Type:", typeField,
                "Start Date:", startField,
                "End Date:", endField,
                "Status:", statusBox
        };

        if(JOptionPane.showConfirmDialog(this, fields,"Edit Resource",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
            try(Connection conn = DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE resource SET ProjectID=?, Name=?, Type=?, StartDate=?, EndDate=?, Status=? WHERE ResourceID=?")) {

                ps.setInt(1, Integer.parseInt(projectIdField.getText()));
                ps.setString(2, nameField.getText());
                ps.setString(3, typeField.getText());
                ps.setDate(4, startField.getText().isEmpty() ? null : Date.valueOf(startField.getText().trim()));
                ps.setDate(5, endField.getText().isEmpty() ? null : Date.valueOf(endField.getText().trim()));
                ps.setString(6, (String)statusBox.getSelectedItem());
                ps.setInt(7, id);
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){
                JOptionPane.showMessageDialog(this, "Error updating resource: " + e.getMessage());
            } catch(IllegalArgumentException e2){
                JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD.");
            }
        }
    }

    private void deleteResource() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this,"Select a resource to delete"); return; }

        int id = (int)tableModel.getValueAt(row,0);
        if(JOptionPane.showConfirmDialog(this,"Delete this resource?","Confirm",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            try(Connection conn = DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM resource WHERE ResourceID=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){
                JOptionPane.showMessageDialog(this, "Error deleting resource: " + e.getMessage());
            }
        }
    }
}
