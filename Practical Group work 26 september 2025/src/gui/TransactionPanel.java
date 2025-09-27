package gui;

import db.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public TransactionPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{
                "TransactionID", "ActivityID", "ReferenceNo", "Amount", "Date", "Method", "Status"
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

        addBtn.addActionListener(e -> addTransaction());
        editBtn.addActionListener(e -> editTransaction());
        deleteBtn.addActionListener(e -> deleteTransaction());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try(Connection conn = DBUtil.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT TransactionID, ActivityID, ReferenceNo, Amount, Date, Method, Status FROM transaction")) {

            while(rs.next()){
                tableModel.addRow(new Object[]{
                        rs.getInt("TransactionID"),
                        rs.getInt("ActivityID"),
                        rs.getString("ReferenceNo"),
                        rs.getBigDecimal("Amount"),
                        rs.getDate("Date"),
                        rs.getString("Method"),
                        rs.getString("Status")
                });
            }

        } catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
        }
    }

    private void addTransaction() {
        JTextField activityIdField = new JTextField();
        JTextField referenceField = new JTextField();
        JTextField amountField = new JTextField();

        // Default today's date
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JTextField dateField = new JTextField(today);

        JComboBox<String> methodBox = new JComboBox<>(new String[]{"Cash","Bank Transfer","Grant","Other"});
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending","Approved","Rejected"});

        Object[] fields = {
                "ActivityID:", activityIdField,
                "ReferenceNo:", referenceField,
                "Amount:", amountField,
                "Date:", dateField,
                "Method:", methodBox,
                "Status:", statusBox
        };

        if(JOptionPane.showConfirmDialog(this, fields,"Add Transaction",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
            try(Connection conn = DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO transaction (ActivityID, ReferenceNo, Amount, Date, Method, Status) VALUES (?,?,?,?,?,?)")) {

                ps.setInt(1, Integer.parseInt(activityIdField.getText()));
                ps.setString(2, referenceField.getText());
                ps.setBigDecimal(3, new java.math.BigDecimal(amountField.getText()));
                ps.setDate(4, Date.valueOf(dateField.getText().trim()));
                ps.setString(5, (String) methodBox.getSelectedItem());
                ps.setString(6, (String) statusBox.getSelectedItem());
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){
                JOptionPane.showMessageDialog(this, "Error adding transaction: " + e.getMessage());
            } catch(IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    private void editTransaction() {
        int row = table.getSelectedRow();
        if(row == -1){ JOptionPane.showMessageDialog(this,"Select a transaction to edit"); return; }

        int id = (int)tableModel.getValueAt(row,0);
        JTextField activityIdField = new JTextField(tableModel.getValueAt(row,1).toString());
        JTextField referenceField = new JTextField(tableModel.getValueAt(row,2).toString());
        JTextField amountField = new JTextField(tableModel.getValueAt(row,3).toString());

        // Fallback to today if date is null
        Object dateVal = tableModel.getValueAt(row,4);
        String dateText = (dateVal != null) ? dateVal.toString() :
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JTextField dateField = new JTextField(dateText);

        JComboBox<String> methodBox = new JComboBox<>(new String[]{"Cash","Bank Transfer","Grant","Other"});
        methodBox.setSelectedItem(tableModel.getValueAt(row,5));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending","Approved","Rejected"});
        statusBox.setSelectedItem(tableModel.getValueAt(row,6));

        Object[] fields = {
                "ActivityID:", activityIdField,
                "ReferenceNo:", referenceField,
                "Amount:", amountField,
                "Date:", dateField,
                "Method:", methodBox,
                "Status:", statusBox
        };

        if(JOptionPane.showConfirmDialog(this,fields,"Edit Transaction",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            try(Connection conn = DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE transaction SET ActivityID=?, ReferenceNo=?, Amount=?, Date=?, Method=?, Status=? WHERE TransactionID=?")) {

                ps.setInt(1, Integer.parseInt(activityIdField.getText()));
                ps.setString(2, referenceField.getText());
                ps.setBigDecimal(3, new java.math.BigDecimal(amountField.getText()));
                ps.setDate(4, Date.valueOf(dateField.getText().trim()));
                ps.setString(5, (String) methodBox.getSelectedItem());
                ps.setString(6, (String) statusBox.getSelectedItem());
                ps.setInt(7, id);
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){
                JOptionPane.showMessageDialog(this, "Error updating transaction: " + e.getMessage());
            } catch(IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this,"Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    private void deleteTransaction() {
        int row = table.getSelectedRow();
        if(row == -1){ JOptionPane.showMessageDialog(this,"Select a transaction to delete"); return; }

        int id = (int)tableModel.getValueAt(row,0);
        if(JOptionPane.showConfirmDialog(this,"Delete this transaction?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try(Connection conn = DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM transaction WHERE TransactionID=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){
                JOptionPane.showMessageDialog(this, "Error deleting transaction: " + e.getMessage());
            }
        }
    }
}
