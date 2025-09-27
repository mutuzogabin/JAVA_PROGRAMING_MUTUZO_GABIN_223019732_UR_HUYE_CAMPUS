package gui;

import db.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CategoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public CategoryPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{
                "CategoryID", "ResourceID", "Category", "Detail", "Owner", "Location", "CreatedAt"
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

        addBtn.addActionListener(e -> addCategory());
        editBtn.addActionListener(e -> editCategory());
        deleteBtn.addActionListener(e -> deleteCategory());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData(){
        tableModel.setRowCount(0);
        try(Connection conn=DBUtil.connect();
            Statement stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT * FROM category")) {

            while(rs.next()){
                tableModel.addRow(new Object[]{
                        rs.getInt("CategoryID"),
                        rs.getInt("ResourceID"),
                        rs.getString("Category"),
                        rs.getString("Detail"),
                        rs.getString("Owner"),
                        rs.getString("Location"),
                        rs.getTimestamp("CreatedAt")
                });
            }

        } catch(SQLException e){ JOptionPane.showMessageDialog(this,"Error loading categories: "+e.getMessage()); }
    }

    private void addCategory(){
        JTextField resourceIdField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField detailField = new JTextField();
        JTextField ownerField = new JTextField();
        JTextField locationField = new JTextField();

        Object[] fields = {
                "ResourceID:", resourceIdField,
                "Category:", categoryField,
                "Detail:", detailField,
                "Owner:", ownerField,
                "Location:", locationField
        };

        if(JOptionPane.showConfirmDialog(this, fields, "Add Category", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
            try(Connection conn=DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO category (ResourceID, Category, Detail, Owner, Location) VALUES (?,?,?,?,?)")) {

                ps.setInt(1, Integer.parseInt(resourceIdField.getText()));
                ps.setString(2, categoryField.getText());
                ps.setString(3, detailField.getText());
                ps.setString(4, ownerField.getText());
                ps.setString(5, locationField.getText());
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){ JOptionPane.showMessageDialog(this,"Error adding category: "+e.getMessage()); }
        }
    }

    private void editCategory(){
        int row = table.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"Select a category to edit"); return; }

        int id = (int)tableModel.getValueAt(row,0);
        JTextField resourceIdField = new JTextField(tableModel.getValueAt(row,1).toString());
        JTextField categoryField = new JTextField(tableModel.getValueAt(row,2).toString());
        JTextField detailField = new JTextField(tableModel.getValueAt(row,3).toString());
        JTextField ownerField = new JTextField(tableModel.getValueAt(row,4).toString());
        JTextField locationField = new JTextField(tableModel.getValueAt(row,5).toString());

        Object[] fields = {
                "ResourceID:", resourceIdField,
                "Category:", categoryField,
                "Detail:", detailField,
                "Owner:", ownerField,
                "Location:", locationField
        };

        if(JOptionPane.showConfirmDialog(this, fields, "Edit Category", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
            try(Connection conn=DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE category SET ResourceID=?, Category=?, Detail=?, Owner=?, Location=? WHERE CategoryID=?")) {

                ps.setInt(1, Integer.parseInt(resourceIdField.getText()));
                ps.setString(2, categoryField.getText());
                ps.setString(3, detailField.getText());
                ps.setString(4, ownerField.getText());
                ps.setString(5, locationField.getText());
                ps.setInt(6, id);
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){ JOptionPane.showMessageDialog(this,"Error updating category: "+e.getMessage()); }
        }
    }

    private void deleteCategory(){
        int row = table.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"Select a category to delete"); return; }

        int id = (int)tableModel.getValueAt(row,0);
        if(JOptionPane.showConfirmDialog(this,"Delete this category?","Confirm",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            try(Connection conn=DBUtil.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM category WHERE CategoryID=?")) {

                ps.setInt(1,id);
                ps.executeUpdate();
                loadData();

            } catch(SQLException e){ JOptionPane.showMessageDialog(this,"Error deleting category: "+e.getMessage()); }
        }
    }
}
